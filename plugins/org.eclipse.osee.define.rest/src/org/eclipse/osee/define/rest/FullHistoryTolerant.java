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

import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.GitCommitFile;
import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.GitLatest;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

public class FullHistoryTolerant implements HistoryImportStrategy {
   protected final Map<String, ArtifactId> pathToCodeunitMap = new HashMap<>(10000);
   private final TupleQuery tupleQuery;
   protected final ArtifactId repository;

   public FullHistoryTolerant(ArtifactId repository, TupleQuery tupleQuery) {
      this.repository = repository;
      this.tupleQuery = tupleQuery;
   }

   @Override
   public ArtifactId getCodeUnit(BranchId branch, TransactionBuilder tx, String commitSHA, String changeType, String path, String newPath) {
      if (path.startsWith("Tools") || newPath.startsWith("Tools")) {
         return ArtifactId.SENTINEL;
      }
      ArtifactId codeUnit = findCodeUnit(repository, branch, path);

      if (matchesChangeType(changeType, 'A')) {
         if (codeUnit.isValid()) {
            //            throw new OseeStateException("Attempting to add already existing code unit [%s] for repository [%s]", path,
            //               repository);
            System.out.printf("commit [%s] adds code unit [%s] but found existing code unit [%s]\n", commitSHA, path,
               codeUnit);
         } else {
            codeUnit = tx.createArtifact(CoreArtifactTypes.CodeUnit, path);
            pathToCodeunitMap.put(path, codeUnit);
         }
      } else if (matchesChangeType(changeType, 'C')) {
         codeUnit = tx.createArtifact(CoreArtifactTypes.CodeUnit, newPath);
         pathToCodeunitMap.put(newPath, codeUnit);
      } else if (matchesChangeType(changeType, 'D')) {
         if (codeUnit.isValid()) {
            tx.deleteArtifact(codeUnit);
            pathToCodeunitMap.remove(path);
         } else {
            System.out.printf("didn't find %s for deletion in commit %s\n", path, commitSHA);
         }
      } else if (matchesChangeType(changeType, 'R')) {
         if (codeUnit.isValid()) {
            tx.setName(codeUnit, newPath);
            pathToCodeunitMap.remove(path);
            pathToCodeunitMap.put(newPath, codeUnit);
         } else {
            System.out.printf("didn't find in commit [%s] for rename from [%s] to [%s]\n", commitSHA, path, newPath);
            codeUnit = tx.createArtifact(CoreArtifactTypes.CodeUnit, newPath);
            pathToCodeunitMap.put(newPath, codeUnit);
         }
      } else if (matchesChangeType(changeType, 'M')) {
         // no change to the code unit (we add the GitCommitFile tuple entry elsewhere)
      } else {
         System.out.printf("unexpected change type [%s] on path [%s]\n", changeType, path);
      }
      if (codeUnit.isInvalid()) {
         System.out.printf("unexpected invalid code unit with changeType [%s] in commit [%s] for path [%s]\n",
            changeType, commitSHA, path);
      }
      return codeUnit;
   }

   private ArtifactId findCodeUnit(ArtifactId repository, BranchId branch, String path) {
      ArtifactId codeUnit = pathToCodeunitMap.get(path);
      if (codeUnit != null) {
         return codeUnit;
      }
      return ArtifactId.SENTINEL;
   }

   @Override
   public void handleCodeUnit(BranchId branch, ArtifactId codeUnit, TransactionBuilder tx, ArtifactId repository, ArtifactId commit, String changeType) {
      tx.addTuple4(GitCommitFile, repository, codeUnit, commit, changeType);

      ArtifactId[] commitWraper = new ArtifactId[] {ArtifactId.SENTINEL};
      tupleQuery.getTuple4E3E4FromE1E2(GitLatest, branch, repository, codeUnit,
         (ignore, baselineCommit) -> commitWraper[0] = baselineCommit);

      tx.deleteTuple4ByE1E2(GitLatest, repository, codeUnit);

      tx.addTuple4(GitLatest, repository, codeUnit, commit, commitWraper[0]);
   }

   @Override
   public TransactionBuilder getTransactionBuilder(OrcsApi orcsApi, ArtifactId repository, BranchId branch, UserId account) {
      return orcsApi.getTransactionFactory().createTransaction(branch, account,
         "TraceabilityOperationsImpl.parseGitHistory repo [" + repository.getIdString() + "]");
   }

   @Override
   public void finishGitCommit(TransactionBuilder tx) {
      tx.commit();
   }

   @Override
   public void finishImport() {
   }
}