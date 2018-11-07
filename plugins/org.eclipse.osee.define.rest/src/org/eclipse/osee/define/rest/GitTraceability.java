/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import static org.eclipse.osee.define.api.DefineTupleTypes.GitCommitFile;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitCommit;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public final class GitTraceability {
   private static final SimpleDateFormat filenameDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
   //   sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

   private static final Pattern mergePattern = Pattern.compile("Merge: \\w+ \\w+");
   private static final Pattern delimiterPattern = Pattern.compile("\\n+");
   private static final String endShowMarker = "GitShowEndsHere";
   private static final Pattern commitShaPattern = Pattern.compile("commit (\\w+)");
   private static final Pattern authorPattern = Pattern.compile("Author:\\s+(.*)");
   private static final Pattern datePattern = Pattern.compile("Date:\\s+(.*)");
   private static final Pattern signedOffPattern = Pattern.compile("\\s*Signed-off-by:\\s+(.*)");
   private static final Pattern changeIdPattern = Pattern.compile("\\s+Change-Id: (I\\w{40})");
   /*
    * must support rename case "R093    oldPath     newPath" also must support files with spaces (not consecutive) in
    * name A .gitignore
    */
   private static final Pattern filePattern = Pattern.compile("([CRM][01]\\d{2}|[ACDMRTUXB]{1,2})\\t([^\\t]+)\\t?(.*)");
   private final Matcher commitShaMatcher = commitShaPattern.matcher("");
   private final Matcher authorMatcher = authorPattern.matcher("");
   private final Matcher dateMatcher = datePattern.matcher("");
   private final Matcher signedOffMatcher = signedOffPattern.matcher("");
   private final Matcher changeIdMatcher = changeIdPattern.matcher("");
   private final Matcher fileMatcher = filePattern.matcher("");
   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private final TupleQuery tupleQuery;

   public GitTraceability(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.tupleQuery = queryFactory.tupleQuery();
   }

   public TransactionToken parseGitHistory(ArtifactId repository, String gitHistory, BranchId branch, UserId account) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
         "TraceabilityOperationsImpl.parseGitHistory repo [" + repository.getIdString() + "]");
      HistoryImportStrategy importStrategy = new FastHistoryStrategy(repository, tx);
      //new FullHistoryTolerant(repository, orcsApi.getQueryFactory().tupleQuery());
      return parseGitHistory(repository, gitHistory, branch, account, importStrategy);
   }

   public TransactionToken parseGitHistory(ArtifactId repoArtifact, String gitHistory, BranchId branch, UserId account, HistoryImportStrategy importStrategy) {
      Scanner scanner = new Scanner(gitHistory);
      scanner.useDelimiter(delimiterPattern);

      while (scanner.hasNext()) {
         parseGitCommit(repoArtifact, scanner, branch, account, importStrategy);
      }
      return importStrategy.finishImport();
   }

   private void parseGitCommit(ArtifactId repoArtifact, Scanner scanner, BranchId branch, UserId account, HistoryImportStrategy importStrategy) {
      TransactionBuilder tx = importStrategy.getTransactionBuilder(orcsApi, branch, account);

      String commitSHA = parseLine(scanner, commitShaMatcher);
      System.out.println(commitSHA);

      if (scanner.hasNext(mergePattern)) {
         scanner.next();
      }
      String author = parseLine(scanner, authorMatcher);
      String dateString = parseLine(scanner, dateMatcher);
      Date authorDate;
      try {
         authorDate = filenameDateFormat.parse(dateString);
      } catch (ParseException ex) {
         throw OseeCoreException.wrap(ex);
      }

      StringBuilder commitMessage = new StringBuilder(400);

      String commitMessageFirstLine;
      if (scanner.hasNext(filePattern)) { // will find file pattern here if commit message is missing
         commitMessageFirstLine = "";
      } else {
         commitMessageFirstLine = scanner.next();
         commitMessage.append(commitMessageFirstLine);
      }

      ArtifactId commit = tx.createArtifact(GitCommit, commitMessageFirstLine);
      tx.setSoleAttributeValue(commit, CoreAttributeTypes.GitCommitSHA, commitSHA);
      tx.setSoleAttributeValue(commit, CoreAttributeTypes.UserArtifactId, SystemUser.OseeSystem); //TODO: this must convert author to the corresponding user artifact
      tx.setSoleAttributeValue(commit, CoreAttributeTypes.GitCommitAuthorDate, authorDate);

      parseFileChanges(repoArtifact, commitSHA, commit, scanner, branch, tx, commitMessage, importStrategy);

      tx.setSoleAttributeValue(commit, CoreAttributeTypes.GitCommitMessage, commitMessage);

      importStrategy.finishGitCommit(tx);
   }

   private void parseFileChanges(ArtifactId repository, String commitSHA, ArtifactId commit, Scanner scanner, BranchId branch, TransactionBuilder tx, StringBuilder commitMessage, HistoryImportStrategy importStrategy) {
      String commitId = commitSHA;
      while (scanner.hasNext()) {
         String line = scanner.next();
         if (line.equals(endShowMarker)) {
            return;
         } else if (fileMatcher.reset(line).matches()) {
            ChangeType changeType = null; // fileMatcher.group(1);
            String path = fileMatcher.group(2);
            String newPath = fileMatcher.group(3);

            ArtifactId codeUnit = importStrategy.getCodeUnit(branch, tx, commitSHA, changeType, path, newPath);
            if (codeUnit.isValid()) {
               importStrategy.handleCodeUnit(branch, codeUnit, tx, repository, commit, changeType);
            }
         } else if (signedOffMatcher.reset(line).matches()) {
            String signer = signedOffMatcher.group(1);
         } else if (changeIdMatcher.reset(line).matches()) {
            commitId = changeIdMatcher.group(1);
            if (tupleQuery.doesTuple4E3Exist(GitCommitFile, commit)) {
               tx.abandon(); // don't save the same commit twice
            }
            tx.setSoleAttributeValue(commit, GitChangeId, commitId);
         } else {
            commitMessage.append(line);
            commitMessage.append('\n');
         }
      }
   }

   private String parseLine(Scanner scanner, Matcher matcher) {
      String line = scanner.next();
      if (matcher.reset(line).find()) {
         return matcher.group(1);
      } else {
         throw new OseeArgumentException("Did not match pattern: %s", matcher.pattern());
      }
   }
}