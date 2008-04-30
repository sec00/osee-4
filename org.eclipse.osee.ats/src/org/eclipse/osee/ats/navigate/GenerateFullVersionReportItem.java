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

package org.eclipse.osee.ats.navigate;

import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.VersionReportJob;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class GenerateFullVersionReportItem extends XNavigateItemAction {

   private TeamDefinitionArtifact teamDef;
   private final String teamDefName;

   public GenerateFullVersionReportItem(XNavigateItem parent) {
      super(parent, "Generate Full Version Report");
      this.teamDefName = null;
      this.teamDef = null;
   }

   public GenerateFullVersionReportItem(XNavigateItem parent, TeamDefinitionArtifact teamDef) {
      super(parent, "Generate Full Version Report");
      this.teamDefName = null;
      this.teamDef = teamDef;
   }

   public GenerateFullVersionReportItem(XNavigateItem parent, String teamDefName) {
      super(parent, "Generate Full Version Report");
      this.teamDefName = teamDefName;
      this.teamDef = null;
   }

   @Override
   public void run() throws SQLException {
      TeamDefinitionArtifact teamDef = getTeamDefinition();
      if (teamDef == null) return;
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      PublishReportJob job = new PublishReportJob(teamDef);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
   }

   public TeamDefinitionArtifact getTeamDefinition() throws SQLException {
      if (teamDef != null) return teamDef;
      if (teamDefName != null && !teamDefName.equals("")) {
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch(TeamDefinitionArtifact.ARTIFACT_NAME, teamDefName,
                     BranchPersistenceManager.getAtsBranch());
         TeamDefinitionArtifact teamDef = srch.getSingletonArtifactOrException(TeamDefinitionArtifact.class);
         if (teamDef != null) return teamDef;
      }
      TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
      try {
         ld.setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(Active.Active));
      } catch (MultipleAttributesExist ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      int result = ld.open();
      if (result == 0) {
         return (TeamDefinitionArtifact) ld.getResult()[0];
      }
      return null;
   }

   private class PublishReportJob extends Job {

      private final TeamDefinitionArtifact teamDef;

      public PublishReportJob(TeamDefinitionArtifact teamDef) {
         super(teamDef.getDescriptiveName() + " as of " + XDate.getDateNow());
         this.teamDef = teamDef;
      }

      @Override
      public IStatus run(IProgressMonitor monitor) {
         try {
            String html = VersionReportJob.getFullReleaseReport(teamDef, monitor);
            XResultData rd = new XResultData(AtsPlugin.getLogger());
            rd.addRaw(html);
            rd.report(getName(), Manipulations.RAW_HTML);
         } catch (Exception ex) {
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
         }

         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
