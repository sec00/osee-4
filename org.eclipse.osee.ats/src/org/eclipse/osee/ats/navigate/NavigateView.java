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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.BulkLoadAtsCache;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.ats.world.search.MultipleHridSearchItem;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Insert the type's description here.
 * 
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class NavigateView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.ats.navigate.NavigateView";
   public static final String HELP_CONTEXT_ID = "atsNavigator";
   private AtsNavigateComposite xNavComp;

   /**
    * The constructor.
    */
   public NavigateView() {
   }

   @Override
   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      BulkLoadAtsCache.run(false);
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      OseeContributionItem.addTo(this, false);

      xNavComp = new AtsNavigateComposite(new AtsNavigateViewItems(), parent, SWT.NONE);

      AtsPlugin.getInstance().setHelp(xNavComp, HELP_CONTEXT_ID);
      createActions();
      if (savedFilterStr != null) {
         xNavComp.getFilteredTree().getFilterControl().setText(savedFilterStr);
      }
      xNavComp.refresh();
      xNavComp.getFilteredTree().getFilterControl().setFocus();
   }

   protected void createActions() {
      Action collapseAction = new Action("Collapse All") {

         @Override
         public void run() {
            xNavComp.refresh();
         }
      };
      collapseAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("collapseAll.gif"));
      collapseAction.setToolTipText("Collapse All");

      Action expandAction = new Action("Expand All") {

         @Override
         public void run() {
            xNavComp.getFilteredTree().getViewer().expandAll();
         }
      };
      expandAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("expandAll.gif"));
      expandAction.setToolTipText("Expand All");

      Action openByIdAction = new Action("Open by Id") {

         @Override
         public void run() {
            MultipleHridSearchItem srch = new MultipleHridSearchItem();
            try {
               Collection<Artifact> artifacts = srch.performSearchGetResults(true);
               final Set<Artifact> addedArts = new HashSet<Artifact>();
               for (Artifact artifact : artifacts) {
                  if ((!(artifact instanceof ActionArtifact)) && (!(artifact instanceof StateMachineArtifact))) {
                     ArtifactEditor.editArtifact(artifact);
                     continue;
                  } else
                     addedArts.add(artifact);
               }
               WorldView.getWorldView().load("Open by Id: \"" + srch.getEnteredIds() + "\"", addedArts);
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };
      openByIdAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("openId.gif"));
      openByIdAction.setToolTipText("Open by Id");

      Action openChangeReportById = new Action("Open Change Report by Id") {

         @Override
         public void run() {
            MultipleHridSearchItem srch = new MultipleHridSearchItem("Open Change Report by Id");
            try {
               Collection<Artifact> artifacts = srch.performSearchGetResults(true);
               final Set<Artifact> addedArts = new HashSet<Artifact>();
               for (Artifact artifact : artifacts) {
                  if (artifact instanceof ActionArtifact) {
                     for (TeamWorkFlowArtifact team : ((ActionArtifact) artifact).getTeamWorkFlowArtifacts()) {
                        if (team.getSmaMgr().getBranchMgr().isCommittedBranch() || team.getSmaMgr().getBranchMgr().isWorkingBranch()) {
                           addedArts.add(team);
                        }
                     }
                  }
               }
               if (addedArts.size() == 0) {
                  AWorkbench.popup("ERROR", "No committed or working branches for entered id.");
                  return;
               }
               if (addedArts.size() < 3 || MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                     "Open Change Reports",
                     "Opening " + addedArts.size() + " Change Reports?\n\n(may want to run this off-hours)")) {
                  for (Artifact art : addedArts) {
                     ((StateMachineArtifact) art).getSmaMgr().getBranchMgr().showChangeReport();
                  }
               }
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };
      openChangeReportById.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch_change.gif"));
      openChangeReportById.setToolTipText("Open Change Report by Id");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(collapseAction);
      toolbarManager.add(expandAction);
      toolbarManager.add(openChangeReportById);
      toolbarManager.add(openByIdAction);
      toolbarManager.add(new NewAction());

      OseeAts.addBugToViewToolbar(this, this, AtsPlugin.getInstance(), VIEW_ID, "ATS Navigator");

   }

   /**
    * Provided for tests to be able to simulate a double-click
    * 
    * @param item
    */
   public void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) throws OseeCoreException {
      OseeLog.log(AtsPlugin.class, Level.INFO,
            "===> Simulating NavigateView Double-Click for \"" + item.getName() + "\"...");
      xNavComp.handleDoubleClick(item, tableLoadOptions);
   }

   public static NavigateView getNavigateView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (NavigateView) page.showView(NavigateView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Launch OSEE ATS NavigateView " + e1.getMessage());
      }
      return null;
   }

   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) return String.format("Currently Selected - %s",
            ((XNavigateItem) sel.iterator().next()).getName());
      return "";
   }

   private static final String INPUT = "filter";
   private static final String FILTER_STR = "filterStr";

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);

      if (xNavComp != null && xNavComp.getFilteredTree().getFilterControl() != null && !xNavComp.getFilteredTree().isDisposed()) {
         String filterStr = xNavComp.getFilteredTree().getFilterControl().getText();
         memento.putString(FILTER_STR, filterStr);
      }
   }
   private String savedFilterStr = null;

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               savedFilterStr = memento.getString(FILTER_STR);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "NavigateView error on init", ex);
      }
   }
}