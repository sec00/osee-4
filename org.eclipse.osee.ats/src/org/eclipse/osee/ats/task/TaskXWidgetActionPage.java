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
package org.eclipse.osee.ats.task;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.AtsXWidgetActionFormPage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class TaskXWidgetActionPage extends AtsXWidgetActionFormPage {

   private final TaskEditor taskEditor;
   private TaskComposite taskComposite;
   private static String HELP_CONTEXT_ID = "atsWorkflowEditorTaskTab";

   public TaskXWidgetActionPage(TaskEditor taskEditor) {
      super(taskEditor, "org.eclipse.osee.ats.actionPage", "Actions");
      this.taskEditor = taskEditor;
   }

   @Override
   public Section createResultsSection(Composite body) throws OseeCoreException {
      resultsSection = toolkit.createSection(body, Section.NO_TITLE);
      resultsSection.setText("Results");
      resultsSection.setLayoutData(new GridData(GridData.FILL_BOTH));

      resultsContainer = toolkit.createClientContainer(resultsSection, 1);
      taskComposite = new TaskComposite(taskEditor, resultsContainer, SWT.BORDER, toolBar);
      AtsPlugin.getInstance().setHelp(taskComposite, HELP_CONTEXT_ID, "org.eclipse.osee.ats.help.ui");
      return resultsSection;
   }

   public TaskComposite getTaskComposite() {
      return taskComposite;
   }

   @Override
   public void createPartControl(Composite parent) {
      super.createPartControl(parent);

      Result result = AtsPlugin.areOSEEServicesAvailable();
      if (result.isFalse()) {
         AWorkbench.popup("ERROR", "DB Connection Unavailable");
         return;
      }
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      if (taskEditor.getTaskEditorProvider() instanceof TaskEditorParameterSearchItemProvider) {
         return (((TaskEditorParameterSearchItemProvider) taskEditor.getTaskEditorProvider()).getWorldSearchItem());
      }
      return null;
   }

   @Override
   public Result isResearchSearchValid() throws OseeCoreException {
      return taskEditor.isDirty() ? new Result("Changes un-saved. Save first.") : Result.TrueResult;
   }

   public void reSearch() throws OseeCoreException {
      Result result = isResearchSearchValid();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      taskEditor.handleRefreshAction();
   }

   @Override
   public String getXWidgetsXml() throws OseeCoreException {
      if (taskEditor.getTaskEditorProvider() instanceof TaskEditorParameterSearchItemProvider) {
         return (((TaskEditorParameterSearchItemProvider) taskEditor.getTaskEditorProvider()).getWorldSearchItem()).getParameterXWidgetXml();
      }
      return null;
   }

   @Override
   public void handleSearchButtonPressed() {
      try {
         reSearch();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
