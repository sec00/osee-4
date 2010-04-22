/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.render.word.WordChangeReportOperation;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ExportChangeReportsAction extends Action {
   private final WorldEditor worldEditor;

   public ExportChangeReportsAction(WorldEditor worldEditor) {
      setText("Export Change Report(s)");
      setImageDescriptor(getImageDescriptor());
      this.worldEditor = worldEditor;
   }

   public Set<TeamWorkFlowArtifact> getWorkflows() {
      return worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts();
   }

   @Override
   public void run() {
      IOperation operation = new ExportChangesOperation(getWorkflows());
      Operations.executeAsJob(operation, true);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA);
   }

   public void updateEnablement() {
      setEnabled(!getWorkflows().isEmpty());
   }

   private static final class ExportChangesOperation extends AbstractOperation {
      private final Collection<TeamWorkFlowArtifact> workflows;

      public ExportChangesOperation(Collection<TeamWorkFlowArtifact> workflows) {
         super("Exporting Change Report(s)", AtsPlugin.PLUGIN_ID);
         this.workflows = workflows;
      }

      private TransactionRecord pickTransaction(IArtifact workflow, IOseeBranch branch) throws OseeCoreException {
         for (TransactionRecord transaction : TransactionManager.getCommittedArtifactTransactionIds(workflow)) {
            if (transaction.getBranch().equals(branch)) {
               return transaction;
            }
         }
         throw new OseeStateException("no transaction record for " + branch + " found.");
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         Branch branch = BranchManager.getBranchByGuid("NBdJRXpKwHF0bAvVHSwA");
         for (Artifact workflow : workflows) {
            AtsBranchManager atsBranchMgr = ((TeamWorkFlowArtifact) workflow).getBranchMgr();

            Collection<Change> changes = new ArrayList<Change>();
            IOperation operation = null;
            if (atsBranchMgr.isCommittedBranchExists()) {
               operation = ChangeManager.comparedToPreviousTx(pickTransaction(workflow, branch), changes);
            } else {
               Branch workingBranch = atsBranchMgr.getWorkingBranch();
               if (workingBranch != null) {
                  operation = ChangeManager.comparedToParent(workingBranch, changes);
               }
            }
            if (operation != null) {
               doSubWork(operation, monitor, 0.50);
            }
            if (!changes.isEmpty()) {
               String folderName = workflow.getSoleAttributeValueAsString(AtsAttributeTypes.LegacyPCRId, null);
               IOperation subOp = new WordChangeReportOperation(changes, true, folderName);
               doSubWork(subOp, monitor, 0.50);
            } else {
               monitor.worked(calculateWork(0.50));
            }
         }

      }
   }

}
