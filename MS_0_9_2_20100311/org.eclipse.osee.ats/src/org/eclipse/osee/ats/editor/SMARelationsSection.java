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
package org.eclipse.osee.ats.editor;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.RelationsFormSection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class SMARelationsSection extends RelationsFormSection {

   public SMARelationsSection(SMAEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style | Section.TWISTIE | Section.TITLE_BAR);
   }

   @Override
   public SMAEditor getEditor() {
      return (SMAEditor) super.getEditor();
   }

   @Override
   public BaseArtifactEditorInput getEditorInput() {
      return super.getEditorInput();
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      // Don't allow users to see all relations
      if (!AtsUtil.isAtsAdmin()) {
         getRelationComposite().getTreeViewer().addFilter(userRelationsFilter);
      }
   }

   @Override
   protected void handleExpandAndCollapse() {
      ((SMAWorkFlowTab) getEditor().getSelectedPage()).getManagedForm().getForm().layout();
   }

   @Override
   protected void addDragAndDrop(Control dropArea) {
      new SMADragAndDrop(dropArea, (StateMachineArtifact) getEditorInput().getArtifact(), SMAEditor.EDITOR_ID);
   }

   private static ViewerFilter userRelationsFilter = new ViewerFilter() {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
         if (element instanceof RelationType) {
            return !filteredRelationTypeNames.contains(((RelationType) element).getName());
         }
         return true;
      }
   };

   private static List<String> filteredRelationTypeNames =
         Arrays.asList(AtsRelationTypes.ActionToWorkflow_Action.getName(), AtsRelationTypes.SmaToTask_Sma.getName(),
               AtsRelationTypes.TeamActionableItem_ActionableItem.getName(),
               AtsRelationTypes.TeamWorkflowTargetedForVersion_Version.getName(), AtsRelationTypes.TeamLead_Lead.getName(),
               AtsRelationTypes.TeamMember_Member.getName(), AtsRelationTypes.TeamWorkflowToReview_Review.getName(),
               CoreRelationTypes.WorkItem__Child.getName(), CoreRelationTypes.Default_Hierarchical__Child.getName(),
               CoreRelationTypes.Users_Artifact.getName());

}
