/*
 * Created on Apr 14, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.ide.util.widgets.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * Generic task creation off change report from sibling or self workflow based on ATS Task Set definition.
 *
 * @author Donald G. Dunne
 */
public class XCreateChangeReportTasksXButton extends XButton implements IArtifactWidget {

   public static final Object WIDGET_ID = XCreateChangeReportTasksXButton.class.getSimpleName();
   // Team Workflow hosting this button (not necessarily one holding branch/commit
   private TeamWorkFlowArtifact hostTeamWf;
   private final AtsTaskDefToken taskDefToken;
   private final XResultData rd;

   public XCreateChangeReportTasksXButton(String name, AtsTaskDefToken taskDefToken) {
      super(name);
      Conditions.assertNotNull(taskDefToken, "taskDefToken must not be null");
      this.taskDefToken = taskDefToken;
      setImage(ImageManager.getImage(AtsImage.PLAY_GREEN));
      setToolTip(String.format("Click to Create/Update Change Report Tasks from [%s]", taskDefToken.getName()));
      addXModifiedListener(listener);
      rd = new XResultData();
   }

   private final XModifiedListener listener = new XModifiedListener() {
      String fName = getLabel();

      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         createUpdateTasks(fName);
      }

   };

   protected void createUpdateTasks(String name) {

      Job job = new Job(name) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            ChangeReportTaskData data = new ChangeReportTaskData();
            data.setTaskDefToken(taskDefToken);
            data.setHostTeamWf(hostTeamWf);
            data.setAsUser((AtsUser) AtsClientService.get().getUserService().getCurrentUser());
            data = AtsClientService.getTaskEp().create(data);
            XResultDataUI.report(data.getResults(), getName());

            // Reload team wfs if tasks created

            return Status.OK_STATUS;
         }
      };
      job.schedule();
   };

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return hostTeamWf;
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.hostTeamWf = (TeamWorkFlowArtifact) artifact;
      }
      super.setEditable(true);
   }

}