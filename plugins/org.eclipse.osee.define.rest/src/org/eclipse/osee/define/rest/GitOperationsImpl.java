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
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitRepository;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.FileSystemPath;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RepositoryUrl;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Git_Repository_Commit;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.NetRCCredentialsProvider;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public final class GitOperationsImpl implements GitOperations {
   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private final TupleQuery tupleQuery;
   private final SystemPreferences systemPrefs;

   private static final Pattern changeIdPattern = Pattern.compile("\\s+Change-Id: (I\\w{40})");
   private final Matcher changeIdMatcher = changeIdPattern.matcher("");

   public GitOperationsImpl(OrcsApi orcsApi, SystemPreferences systemPrefs) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.tupleQuery = queryFactory.tupleQuery();
      this.systemPrefs = systemPrefs;
   }

   @Override
   public ArtifactReadable getRepoArtifact(BranchId branch, String repositoryName) {
      return queryFactory.fromBranch(branch).andNameEquals(repositoryName).andTypeEquals(
         CoreArtifactTypes.GitRepository).getArtifact();
   }

   private Repository getLocalRepoReference(String repoPath) {
      File gitDirPath = new File(repoPath + File.separator + ".git");
      try {
         return new FileRepositoryBuilder().setGitDir(gitDirPath).readEnvironment().findGitDir().setMustExist(
            true).build();
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public void fetch(ArtifactReadable repoArtifact, String password) {
      Repository jgitRepo = getLocalRepoReference(repoArtifact.getSoleAttributeValue(FileSystemPath));
      fetch(jgitRepo, password);
   }

   private void fetch(Repository localRepo, String password) {
      try (Git git = new Git(localRepo)) {

         FetchCommand fetchCommand = git.fetch().setCheckFetchedObjects(true).setTagOpt(TagOpt.NO_TAGS);

         configurateAuthentication(localRepo, fetchCommand, password);

         FetchResult result = fetchCommand.call();
      } catch (GitAPIException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void configurateAuthentication(Repository repo, TransportCommand<?, ?> transportCommand, String password) {
      String gitRepoUrl = repo.getConfig().getString(ConfigConstants.CONFIG_REMOTE_SECTION, "origin", "url");
      configurateAuthentication(gitRepoUrl, transportCommand, password);
   }

   private void configurateAuthentication(String gitRepoUrl, TransportCommand<?, ?> transportCommand, String password) {
      if (gitRepoUrl.startsWith("ssh")) {
         configureSsh(transportCommand, password);
      } else {
         transportCommand.setCredentialsProvider(new NetRCCredentialsProvider());
      }
   }

   private void configureSsh(TransportCommand<?, ?> transportCommand, String password) {
      SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
         @Override
         protected void configure(Host host, Session session) {
            session.setPassword(password);
         }

         @Override
         protected JSch createDefaultJSch(FS fs) throws JSchException {
            JSch defaultJSch = super.createDefaultJSch(fs);
            defaultJSch.addIdentity("~/.ssh/id_rsa", password);
            return defaultJSch;
         }
      };

      transportCommand.setTransportConfigCallback(new TransportConfigCallback() {
         @Override
         public void configure(Transport transport) {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshSessionFactory);
         }
      });
   }

   @Override
   public ArtifactId trackGitBranch(String gitRepoUrl, BranchId branch, UserId account, String gitBranchName, boolean clone, String password) {
      ArtifactReadable repoArtifact = clone(gitRepoUrl, branch, account, clone, password);
      return updateGitTrackingBranch(branch, repoArtifact, account, gitBranchName, !clone, password);
   }

   @Override
   public ArtifactId updateGitTrackingBranch(BranchId branch, ArtifactReadable repoArtifact, UserId account, String gitBranchName, boolean fetch, String password) {
      Repository jgitRepo = getLocalRepoReference(repoArtifact.getSoleAttributeValue(FileSystemPath));
      if (fetch) {
         fetch(jgitRepo, password);
      }
      try {
         String fromString = "remotes/origin/" + gitBranchName;
         ObjectId from = jgitRepo.resolve(fromString);
         if (from == null) {
            throw new OseeStateException("Failed to resolve commit [%s]", fromString);
         }

         ObjectId to = null;
         ArtifactReadable latestCommit =
            repoArtifact.getRelated(Git_Repository_Commit).getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
         if (latestCommit.isValid()) {
            String latestImportedSHA = latestCommit.getSoleAttributeValue(CoreAttributeTypes.GitCommitSHA);
            to = ObjectId.fromString(latestImportedSHA);
            if (to == null) {
               throw new OseeStateException("Failed to resolve commit [%s]", latestImportedSHA);
            }
         }

         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(repoArtifact.getBranch(), account,
            "updateGitTrackingBranch repo [" + repoArtifact + "]");
         HistoryImportStrategy importStrategy = new FastHistoryStrategy(repoArtifact, tx);
         walkTree(repoArtifact, jgitRepo, to, from, repoArtifact.getBranch(), account, importStrategy);
      } catch (RevisionSyntaxException | IOException ex) {
         throw OseeCoreException.wrap(ex);
      }

      return repoArtifact;
   }

   public ArtifactReadable clone(String gitRepoUrl, BranchId branch, UserId account, boolean clone, String password) {
      String serverDataLocation = systemPrefs.getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      String repoName = gitRepoUrl.substring(gitRepoUrl.lastIndexOf('/') + 1).replaceAll("\\.git$", "");
      File localPath = new File(serverDataLocation + File.separator + "git", repoName);

      if (clone) {
         CloneCommand jgitClone = Git.cloneRepository().setURI(gitRepoUrl).setDirectory(localPath).setNoCheckout(true);
         configurateAuthentication(gitRepoUrl, jgitClone, password);
         try {
            jgitClone.call();
         } catch (GitAPIException ex) {
            throw OseeCoreException.wrap(ex);
         }
      }

      if (queryFactory.fromBranch(branch).andNameEquals(repoName).andTypeEquals(GitRepository).exists()) {
         throw new OseeStateException("A reporisotry named %s already exists on branch %s", repoName, branch);
      }

      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "GitOperationsImpl.createGitRepository()");

      ArtifactId repoArtifact = tx.createArtifact(CoreArtifactTokens.GitRepoFolder, GitRepository, repoName);
      tx.setSoleAttributeValue(repoArtifact, RepositoryUrl, gitRepoUrl);
      try {
         tx.setSoleAttributeValue(repoArtifact, FileSystemPath, localPath.getCanonicalPath());
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
      tx.commit();

      return queryFactory.fromBranch(branch).andId(repoArtifact).getArtifact();
   }

   private TransactionToken walkTree(ArtifactReadable repoArtifact, Repository jgitRepo, ObjectId to, ObjectId from, BranchId branch, UserId account, HistoryImportStrategy importStrategy) {
      ArtifactId lastCommitArtifact = ArtifactId.SENTINEL;

      try (RevWalk revWalk = new RevWalk(jgitRepo)) {

         revWalk.markStart(revWalk.parseCommit(from)); //newest commit

         if (to != null) {
            RevCommit toRev = revWalk.parseCommit(to);
            revWalk.markUninteresting(toRev); // oldest commit - the last one we previously imported
         }
         revWalk.sort(RevSort.TOPO, true);
         revWalk.sort(RevSort.REVERSE, true);

         DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
         df.setRepository(jgitRepo);
         df.setDiffComparator(RawTextComparator.DEFAULT);
         df.setDetectRenames(true);
         //TODO use df.setPathFilter(filter); to exclude unwanted paths

         ObjectReader objectReader = revWalk.getObjectReader();
         for (RevCommit revCommit : revWalk) {
            lastCommitArtifact =
               parseGitCommit(objectReader, df, repoArtifact, revCommit, branch, account, importStrategy);
         }

         if (lastCommitArtifact.isValid()) {
            TransactionBuilder tx = importStrategy.getTransactionBuilder(orcsApi, branch, account);
            tx.unrelateFromAll(Git_Repository_Commit.getOpposite(), repoArtifact);
            tx.relate(repoArtifact, Git_Repository_Commit, lastCommitArtifact);
            importStrategy.finishGitCommit(tx);
         }
         return importStrategy.finishImport();
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private ArtifactId parseGitCommit(ObjectReader objectReader, DiffFormatter df, ArtifactId repoArtifact, RevCommit revCommit, BranchId branch, UserId account, HistoryImportStrategy importStrategy) {
      TransactionBuilder tx = importStrategy.getTransactionBuilder(orcsApi, branch, account);

      String commitSHA = revCommit.getId().name();
      ArtifactId commitArtifact = tx.createArtifact(GitCommit, revCommit.getShortMessage());
      tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitSHA, commitSHA);
      tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.UserArtifactId, SystemUser.OseeSystem); //TODO: this must convert author to the corresponding user artifact
      tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitAuthorDate,
         revCommit.getAuthorIdent().getWhen());
      tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitMessage, revCommit.getFullMessage());

      String commitId = commitSHA;

      if (changeIdMatcher.reset(revCommit.getFullMessage()).find()) {
         commitId = changeIdMatcher.group(1);
         if (tupleQuery.doesTuple4E3Exist(GitCommitFile, commitArtifact)) {
            tx.abandon(); // don't save the same commit twice
         }
      }
      tx.setSoleAttributeValue(commitArtifact, GitChangeId, commitId);

      parseFileChanges(objectReader, df, repoArtifact, revCommit, commitSHA, commitArtifact, branch, tx,
         importStrategy);

      revCommit.disposeBody();
      importStrategy.finishGitCommit(tx);
      return commitArtifact;
   }

   private void parseFileChanges(ObjectReader objectReader, DiffFormatter df, ArtifactId repoArtifact, RevCommit revCommit, String commitSHA, ArtifactId commitArtifact, BranchId branch, TransactionBuilder tx, HistoryImportStrategy importStrategy) {
      if (revCommit.getParents().length > 1) {
         return;
      }

      RevTree parentTree = revCommit.getParentCount() > 0 ? revCommit.getParent(0).getTree() : null;
      List<DiffEntry> diffs = null;
      try {
         diffs = df.scan(parentTree, revCommit.getTree());

         for (DiffEntry entry : diffs) {
            ChangeType changeType = entry.getChangeType();
            String path = entry.getOldPath();
            String newPath = entry.getNewPath();

            String testPath = "Components/Apps/DataLoader/Test/SSD/BCG-Z-MMMM-AAA1.LUH";
            if (path.startsWith(testPath) || newPath.equals(testPath)) {
               System.out.println("found " + testPath);
            }

            ArtifactId codeUnit = importStrategy.getCodeUnit(branch, tx, commitSHA, changeType, path, newPath);
            if (codeUnit.isValid()) {
               importStrategy.handleCodeUnit(branch, codeUnit, tx, repoArtifact, commitArtifact, changeType);
            }
         }
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}