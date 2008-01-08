/*
 * Created on Jan 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamWorkflow;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemBlam extends XNavigateItem {
   private static Artifact workflowFolder;
   private final BlamOperation blamOperation;

   /**
    * @param parent
    * @param name
    */
   public XNavigateItemBlam(XNavigateItem parent, BlamOperation blamOperation) {
      super(parent, blamOperation.getClass().getSimpleName());
      this.blamOperation = blamOperation;
   }

   @Override
   public void run() throws SQLException {
      if (workflowFolder == null) {
         workflowFolder =
               ArtifactPersistenceManager.getInstance().getArtifactFromTypeName("Folder", "Blam Workflows",
                     BranchPersistenceManager.getInstance().getCommonBranch());
      }

      BlamWorkflow workflow;
      try {
         workflow = (BlamWorkflow) workflowFolder.getChild(getName());
      } catch (IllegalArgumentException ex) {
         workflow = BlamWorkflow.createBlamWorkflow(blamOperation);
         workflow.setDescriptiveName(getName());
         workflowFolder.addChild(workflow);
         workflow.persist(true);
      }
      workflow.setSoleOperation(blamOperation);

      WorkflowEditor.editArtifact(workflow);
   }
}