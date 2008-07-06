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
package org.eclipse.osee.ats.health;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAutoRunAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ValidateChangeReports extends XNavigateItemAutoRunAction {

   /**
    * @param parent
    */
   public ValidateChangeReports(XNavigateItem parent) {
      super(parent, "Validate Change Reports");
   }

   public ValidateChangeReports() {
      this(null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            final XResultData rd = new XResultData(AtsPlugin.getLogger());
            runIt(monitor, rd);
            rd.report(getName());
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private void runIt(IProgressMonitor monitor, XResultData rd) throws OseeCoreException, SQLException {
      rd.log(AHTML.beginMultiColumnTable(100, 1));
      rd.log(AHTML.addHeaderRowMultiColumnTable(new String[] {"Team", "New", "Del", "Mod", "OldNew", "OldDel", "OldMod"}));
      for (String artifactTypeName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {
         rd.log(AHTML.addRowSpanMultiColumnTable(artifactTypeName, 7));
         for (Artifact artifact : ArtifactQuery.getArtifactsFromType(artifactTypeName, AtsPlugin.getAtsBranch())) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
            if (teamArt.getSmaMgr().getBranchMgr().isCommittedBranch()) {

            } else if (teamArt.getSmaMgr().getBranchMgr().isWorkingBranch()) {
               Collection<Change> changes =
                     RevisionManager.getInstance().getChangesPerBranch(
                           teamArt.getSmaMgr().getBranchMgr().getWorkingBranch());
               Set<Artifact> modArt = new HashSet<Artifact>();
               Set<Artifact> delArt = new HashSet<Artifact>();
               Set<Artifact> newArt = new HashSet<Artifact>();
               for (Change change : changes) {
                  if (change.getModificationType() == ModificationType.CHANGE) {
                     modArt.add(change.getArtifact());
                  }
                  if (change.getModificationType() == ModificationType.DELETED) {
                     delArt.add(change.getArtifact());
                  }
                  if (change.getModificationType() == ModificationType.NEW) {
                     newArt.add(change.getArtifact());
                  }
               }
               rd.log(AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(),
                     String.valueOf(newArt.size()), String.valueOf(delArt.size()), String.valueOf(modArt.size()),
                     "OldNew", "OldDel", "OldMod"}));
            }
         }
      }
      rd.log(AHTML.endMultiColumnTable());
   }
}
