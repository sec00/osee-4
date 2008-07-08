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
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactNameDescriptorCache;
import org.eclipse.osee.framework.skynet.core.revision.AttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.AttributeSummary;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.branch.BranchContentProvider;
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
   private static ArtifactNameDescriptorCache artifactNameDescriptorCache = new ArtifactNameDescriptorCache();

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

   // Lba SA11 Req Team Workflow
   // Lba B3 Req Team Workflow
   // Lba V11 REU Req Team Workflow
   // Lba V13 Req Team Workflow
   private void runIt(IProgressMonitor monitor, XResultData rd) throws OseeCoreException, SQLException {
      StringBuffer sb = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      String[] columnHeaders = new String[] {"Team", "Working", "Mod", "New", "Del", "Notes"};
      sb.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      for (String artifactTypeName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {
         //      for (String artifactTypeName : new String[] {"Lba V13 Req Team Workflow"}) {
         rd.log(AHTML.addRowSpanMultiColumnTable(artifactTypeName, columnHeaders.length));
         try {
            int x = 1;
            Collection<Artifact> artifacts =
                  ArtifactQuery.getArtifactsFromType(artifactTypeName, AtsPlugin.getAtsBranch());
            for (Artifact artifact : artifacts) {
               String result = String.format("Processing %s/%s  - %s", x++, artifacts.size(), artifact);
               OSEELog.logInfo(AtsPlugin.class, result, false);
               //               if (!artifact.getHumanReadableId().equals("1PL1U")) continue;
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               try {
                  Collection<Change> changes = null;
                  if (teamArt.getSmaMgr().getBranchMgr().isCommittedBranch()) {
                     changes =
                           RevisionManager.getInstance().getChangesPerTransaction(
                                 teamArt.getSmaMgr().getBranchMgr().getTransactionId().getTransactionNumber());

                  } else if (teamArt.getSmaMgr().getBranchMgr().isWorkingBranch()) {
                     changes =
                           RevisionManager.getInstance().getChangesPerBranch(
                                 teamArt.getSmaMgr().getBranchMgr().getWorkingBranch());
                  }
                  if (changes != null) {
                     Set<Artifact> modArt = new HashSet<Artifact>();
                     Set<Artifact> delArt = new HashSet<Artifact>();
                     Set<Artifact> newArt = new HashSet<Artifact>();
                     for (Change change : changes) {
                        if (change.getItemKind().equals("Artifact")) {
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
                     }
                     Set<Artifact> oldModArt = new HashSet<Artifact>();
                     Set<Artifact> oldDelArt = new HashSet<Artifact>();
                     Set<Artifact> oldNewArt = new HashSet<Artifact>();
                     for (ArtifactChange artifactChange : teamArt.getSmaMgr().getBranchMgr().getArtifactChanges()) {
                        if (artifactChange.getModType() == ModificationType.CHANGE) {
                           // If there was at least one attribute changed, count it; don't count relation only changes
                           for (Object obj : BranchContentProvider.summarize(RevisionManager.getInstance().getTransactionChanges(
                                 artifactChange, artifactNameDescriptorCache))) {
                              if ((obj instanceof AttributeSummary) || (obj instanceof AttributeChange)) {
                                 oldModArt.add(artifactChange.getArtifact());
                              }
                           }
                        }
                        if (artifactChange.getModType() == ModificationType.DELETED) {
                           oldDelArt.add(artifactChange.getArtifact());
                        }
                        if (artifactChange.getModType() == ModificationType.NEW) {
                           oldNewArt.add(artifactChange.getArtifact());
                        }
                     }
                     boolean modMismatch = false;
                     boolean newMismatch = false;
                     boolean delMismatch = false;
                     StringBuffer notes = new StringBuffer();
                     if (modArt.size() != oldModArt.size()) modMismatch = true;
                     if (newArt.size() != oldNewArt.size()) newMismatch = true;
                     if (delArt.size() != oldDelArt.size()) delMismatch = true;
                     sb.append(AHTML.addRowMultiColumnTable(new String[] {teamArt.getHumanReadableId(),
                           teamArt.getSmaMgr().getBranchMgr().isWorkingBranch() ? "Working" : "Committed",
                           String.format("%s%d/%d", (modMismatch ? "Error: " : ""), modArt.size(), oldModArt.size()),
                           String.format("%s%d/%d", (newMismatch ? "Error: " : ""), newArt.size(), oldNewArt.size()),
                           String.format("%s%d/%d", (delMismatch ? "Error: " : ""), delArt.size(), oldDelArt.size()),
                           notes.toString()}));

                  }
               } catch (Exception ex) {
                  sb.append(AHTML.addRowSpanMultiColumnTable(
                        "Artifact " + artifact.getHumanReadableId() + " - Exception: " + ex.getLocalizedMessage(),
                        columnHeaders.length));
               }
            }
         } catch (Exception ex) {
            sb.append(AHTML.addRowSpanMultiColumnTable("Exception: " + ex.getLocalizedMessage(), columnHeaders.length));
         }
      }
      sb.append(AHTML.endMultiColumnTable());
      rd.addRaw(sb.toString().replaceAll("\n", ""));
   }
}
