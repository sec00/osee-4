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

import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.GitLatest;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

public class FastHistoryStrategy extends FullHistoryTolerant {
   private final Map<ArtifactId, ArtifactId> codeunitToCommitMap = new HashMap<>(10000);
   private final TransactionBuilder tx;

   public FastHistoryStrategy(ArtifactId repository, TransactionBuilder tx) {
      super(repository, null);
      this.tx = tx;
   }

   @Override
   public void handleCodeUnit(BranchId branch, ArtifactId codeUnit, TransactionBuilder tx, ArtifactId repository, ArtifactId commit, String changeType) {
      codeunitToCommitMap.put(codeUnit, commit);

   }

   @Override
   public TransactionBuilder getTransactionBuilder(OrcsApi orcsApi, ArtifactId repository, BranchId branch, UserId account) {
      return tx;
   }

   @Override
   public void finishImport() {
      pathToCodeunitMap.forEach((path, codeUnit) -> tx.addTuple4(GitLatest, repository, codeUnit,
         codeunitToCommitMap.get(codeUnit), ArtifactId.SENTINEL));
      tx.commit();
   }

   @Override
   public void finishGitCommit(TransactionBuilder tx) {
      // only finish transaction in finalize
   }
}