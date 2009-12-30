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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.AddNoteAction;
import org.eclipse.osee.ats.actions.CopyActionDetailsAction;
import org.eclipse.osee.ats.actions.EmailActionAction;
import org.eclipse.osee.ats.actions.FavoriteAction;
import org.eclipse.osee.ats.actions.OpenInArtifactEditorAction;
import org.eclipse.osee.ats.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.actions.OpenInSkyWalkerAction;
import org.eclipse.osee.ats.actions.OpenParentAction;
import org.eclipse.osee.ats.actions.OpenTeamDefinitionAction;
import org.eclipse.osee.ats.actions.OpenVersionArtifactAction;
import org.eclipse.osee.ats.actions.PrivilegedEditAction;
import org.eclipse.osee.ats.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.actions.ShowChangeReportAction;
import org.eclipse.osee.ats.actions.ShowMergeManagerAction;
import org.eclipse.osee.ats.actions.SubscribedAction;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.NoteItem;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.widget.ReviewInfoXWidget;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AnnotationComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.MessageSummaryNote;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowTab extends FormPage implements IActionable {
   private final SMAManager smaMgr;
   private final ArrayList<SMAWorkFlowSection> sections = new ArrayList<SMAWorkFlowSection>();
   private final XFormToolkit toolkit;
   private static String ORIGINATOR = "Originator:";
   private Label origLabel;
   private final List<AtsWorkPage> atsWorkPages = new ArrayList<AtsWorkPage>();
   private ScrolledForm scrolledForm;
   private final Integer HEADER_COMP_COLUMNS = 4;
   private static Map<String, Integer> guidToScrollLocation = new HashMap<String, Integer>();
   private SMARelationsHyperlinkComposite smaRelationsComposite;
   private IManagedForm managedForm;
   private Composite bodyComp;
   private Composite atsBody;
   private SMAActionableItemHeader actionableItemHeader;
   private SMAWorkflowMetricsHeader workflowMetricsHeader;
   private SMADetailsSection smaDetailsSection;
   private SMARelationsSection smaRelationsSection;
   private SMAOperationsSection smaOperationsSection;
   private SMAGoalMembersSection smaGoalMembersSection;
   private SMAHistorySection smaHistorySection;

   public SMAWorkFlowTab(SMAManager smaMgr) {
      super(smaMgr.getEditor(), "overview", "Workflow");
      this.smaMgr = smaMgr;
      toolkit = smaMgr.getEditor().getToolkit();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      try {
         scrolledForm = managedForm.getForm();
         scrolledForm.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
               try {
                  storeScrollLocation();
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
               }
            }
         });

         scrolledForm.setText(smaMgr.getEditor().getTitleStr());
         scrolledForm.setImage(ImageManager.getImage(smaMgr.getSma()));

         bodyComp = managedForm.getForm().getBody();
         GridLayout gridLayout = new GridLayout(1, false);
         bodyComp.setLayout(gridLayout);
         bodyComp.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, true, false));

         createAtsBody();

         addMessageDecoration(scrolledForm);
         FormsUtil.addHeadingGradient(toolkit, scrolledForm, true);

         refreshToolbar();

         if (smaMgr.getSma().getHelpContext() != null) AtsPlugin.getInstance().setHelp(scrolledForm,
               smaMgr.getSma().getHelpContext(), "org.eclipse.osee.ats.help.ui");

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void createAtsBody() throws OseeCoreException {

      atsBody = toolkit.createComposite(bodyComp);
      atsBody.setLayoutData(new GridData(GridData.FILL_BOTH));
      atsBody.setLayout(new GridLayout(1, false));

      createHeaderSection();
      createPageSections();
      createGoalSection();
      createHistorySection();
      createRelationsSection();
      createOperationsSection();
      createDetailsSection();
      createDebugSection();

      atsBody.layout();
      atsBody.setFocus();
      // Jump to scroll location if set
      Integer selection = guidToScrollLocation.get(smaMgr.getSma().getGuid());
      if (selection != null) {
         JumpScrollbarJob job = new JumpScrollbarJob("");
         job.schedule(500);
      }

      managedForm.refresh();
   }

   private void createDebugSection() {
      try {
         if (AtsUtil.isAtsAdmin()) {
            managedForm.addPart(new SMAWorkFlowDebugSection(atsBody, toolkit, SWT.NONE, smaMgr));
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createDetailsSection() {
      try {
         smaDetailsSection = new SMADetailsSection(smaMgr.getEditor(), atsBody, toolkit, SWT.NONE);
         managedForm.addPart(smaDetailsSection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createOperationsSection() {
      try {
         smaOperationsSection = new SMAOperationsSection(smaMgr.getEditor(), atsBody, toolkit, SWT.NONE);
         managedForm.addPart(smaOperationsSection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createRelationsSection() {
      try {
         smaRelationsSection = new SMARelationsSection(smaMgr.getEditor(), atsBody, toolkit, SWT.NONE);
         managedForm.addPart(smaRelationsSection);

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createHistorySection() {
      try {
         smaHistorySection = new SMAHistorySection(smaMgr.getEditor(), atsBody, toolkit, SWT.NONE);
         managedForm.addPart(smaHistorySection);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createGoalSection() {
      try {
         if (smaMgr.getSma() instanceof GoalArtifact) {
            smaGoalMembersSection = new SMAGoalMembersSection(smaMgr.getEditor(), atsBody, toolkit, SWT.NONE);
            managedForm.addPart(smaGoalMembersSection);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createPageSections() {
      try {
         // Only display current or past states
         for (WorkPageDefinition workPageDefinition : smaMgr.getSma().getWorkFlowDefinition().getPagesOrdered()) {
            try {
               AtsWorkPage atsWorkPage =
                     new AtsWorkPage(smaMgr.getWorkFlowDefinition(), workPageDefinition, null,
                           ATSXWidgetOptionResolver.getInstance());
               if (smaMgr.isCurrentState(atsWorkPage.getName()) || smaMgr.getStateMgr().isStateVisited(
                     atsWorkPage.getName())) {
                  // Don't show completed or cancelled state if not currently those state
                  if (atsWorkPage.isCompletePage() && !smaMgr.isCompleted()) continue;
                  if (atsWorkPage.isCancelledPage() && !smaMgr.isCancelled()) continue;
                  SMAWorkFlowSection section = new SMAWorkFlowSection(atsBody, toolkit, SWT.NONE, atsWorkPage, smaMgr);
                  managedForm.addPart(section);
                  control = section.getMainComp();
                  sections.add(section);
                  atsWorkPages.add(atsWorkPage);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createHeaderSection() {
      Composite headerComp = toolkit.createComposite(atsBody);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 100;
      headerComp.setLayoutData(gd);
      headerComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      // mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

      // Display relations
      try {
         createCurrentStateAndTeamHeaders(headerComp, toolkit);
         FormsUtil.createLabelText(toolkit, headerComp, "Assignee(s)", smaMgr.getStateMgr().getAssigneesStr(150));

         createLatestHeader(headerComp, toolkit);
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            actionableItemHeader = new SMAActionableItemHeader(headerComp, toolkit, smaMgr);
         }
         workflowMetricsHeader = new SMAWorkflowMetricsHeader(headerComp, toolkit, smaMgr);
         createSMANotesHeader(headerComp, toolkit, smaMgr, HEADER_COMP_COLUMNS);
         createStateNotesHeader(headerComp, toolkit, smaMgr, HEADER_COMP_COLUMNS, null);
         createAnnotationsHeader(headerComp, toolkit);

         sections.clear();
         atsWorkPages.clear();

         if (SMARelationsHyperlinkComposite.relationExists(smaMgr.getSma())) {
            smaRelationsComposite = new SMARelationsHyperlinkComposite(atsBody, toolkit, SWT.NONE);
            smaRelationsComposite.create(smaMgr);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void addMessageDecoration(ScrolledForm form) {
      form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {

         @Override
         public void linkActivated(HyperlinkEvent e) {
            String title = e.getLabel();
            Object href = e.getHref();
            if (href instanceof IMessage[]) {
               Point noteLocation = ((Control) e.widget).toDisplay(0, 0);
               noteLocation.x += 10;
               noteLocation.y += 10;

               MessageSummaryNote note = new MessageSummaryNote(getManagedForm(), title, (IMessage[]) href);
               note.setLocation(noteLocation);
               note.open();
            }
         }

      });
   }

   private void refreshToolbar() throws OseeCoreException {
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      scrolledForm.getToolBarManager().removeAll();

      if (smaMgr.isTeamWorkflow() && (((TeamWorkFlowArtifact) smaMgr.getSma()).getBranchMgr().isCommittedBranchExists() || ((TeamWorkFlowArtifact) smaMgr.getSma()).getBranchMgr().isWorkingBranchInWork())) {
         toolBarMgr.add(new ShowMergeManagerAction((TeamWorkFlowArtifact) smaMgr.getSma()));
         toolBarMgr.add(new ShowChangeReportAction((TeamWorkFlowArtifact) smaMgr.getSma()));
      }
      toolBarMgr.add(new FavoriteAction(smaMgr.getEditor()));
      if (smaMgr.getSma().getParentSMA() != null) {
         toolBarMgr.add(new OpenParentAction(smaMgr));
      }
      toolBarMgr.add(new EmailActionAction(smaMgr.getEditor()));
      toolBarMgr.add(new AddNoteAction(smaMgr));
      toolBarMgr.add(new OpenInAtsWorldAction(smaMgr));
      if (AtsUtil.isAtsAdmin()) {
         toolBarMgr.add(new OpenInArtifactEditorAction(smaMgr.getEditor()));
      }
      toolBarMgr.add(new OpenInSkyWalkerAction(smaMgr));
      toolBarMgr.add(new OpenVersionArtifactAction(smaMgr));
      toolBarMgr.add(new OpenTeamDefinitionAction(smaMgr));
      toolBarMgr.add(new SubscribedAction(smaMgr.getEditor()));
      toolBarMgr.add(new CopyActionDetailsAction(smaMgr));
      toolBarMgr.add(new PrivilegedEditAction(smaMgr));
      toolBarMgr.add(new ResourceHistoryAction(smaMgr));

      OseeAts.addButtonToEditorToolBar(smaMgr.getEditor(), this, AtsPlugin.getInstance(),
            scrolledForm.getToolBarManager(), SMAEditor.EDITOR_ID, "ATS Editor");

      scrolledForm.updateToolBar();
   }

   public Result isXWidgetDirty() throws OseeCoreException {
      for (SMAWorkFlowSection section : sections) {
         Result result = section.isXWidgetDirty();
         if (result.isTrue()) return result;
      }
      return Result.FalseResult;
   }

   public Result isXWidgetSavable() throws OseeCoreException {
      for (SMAWorkFlowSection section : sections) {
         Result result = section.isXWidgetSavable();
         if (result.isFalse()) return result;
      }
      return Result.TrueResult;
   }

   public void saveXWidgetToArtifact() throws OseeCoreException {
      List<IArtifactWidget> artWidgets = new ArrayList<IArtifactWidget>();
      // Collect all dirty widgets first (so same attribute shown on different sections don't colide
      for (SMAWorkFlowSection section : sections) {
         section.getDirtyIArtifactWidgets(artWidgets);
      }
      for (IArtifactWidget widget : artWidgets) {
         widget.saveToArtifact();
      }
   }

   @Override
   public void dispose() {
      if (actionableItemHeader != null) {
         actionableItemHeader.dispose();
      }
      if (workflowMetricsHeader != null) {
         workflowMetricsHeader.dispose();
      }
      if (smaDetailsSection != null) {
         smaDetailsSection.dispose();
      }
      if (smaHistorySection != null) {
         smaHistorySection.dispose();
      }
      if (smaRelationsSection != null) {
         smaRelationsSection.dispose();
      }
      if (smaGoalMembersSection != null) {
         smaGoalMembersSection.dispose();
      }
      for (SMAWorkFlowSection section : sections)
         section.dispose();

      if (toolkit != null) {
         toolkit.dispose();
      }
   }

   public String getActionDescription() {
      return "Workflow Tab";
   }

   public final static String normalColor = "#FFFFFF";
   private final static String activeColor = "#EEEEEE";

   public String getHtml() throws OseeCoreException {
      StringBuffer htmlSb = new StringBuffer();
      for (WorkPage wPage : atsWorkPages) {
         AtsWorkPage page = (AtsWorkPage) wPage;
         StringBuffer notesSb = new StringBuffer();
         for (NoteItem note : smaMgr.getNotes().getNoteItems()) {
            if (note.getState().equals(page.getName())) {
               notesSb.append(note.toHTML() + AHTML.newline());
            }
         }
         if (smaMgr.isCurrentState(page.getName()) || smaMgr.getStateMgr().isStateVisited(page.getName()) && smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            htmlSb.append(page.getHtml(smaMgr.isCurrentState(page.getName()) ? activeColor : normalColor,
                  notesSb.toString(), ReviewInfoXWidget.toHTML((TeamWorkFlowArtifact) smaMgr.getSma(), page.getName())));
            htmlSb.append(AHTML.newline());
         }
      }
      return htmlSb.toString();
   }

   private Control control = null;

   private void storeScrollLocation() throws OseeStateException {
      if (scrolledForm != null) {
         Integer selection = scrolledForm.getVerticalBar().getSelection();
         // System.out.println("Storing selection => " + selection);
         guidToScrollLocation.put(smaMgr.getSma().getGuid(), selection);
      }
   }

   private class JumpScrollbarJob extends Job {
      public JumpScrollbarJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               try {
                  Integer selection = guidToScrollLocation.get(smaMgr.getSma().getGuid());
                  // System.out.println("Restoring selection => " + selection);

                  // Find the ScrolledComposite operating on the control.
                  ScrolledComposite sComp = null;
                  if (control == null || control.isDisposed()) return;
                  Composite parent = control.getParent();
                  while (parent != null) {
                     if (parent instanceof ScrolledComposite) {
                        sComp = (ScrolledComposite) parent;
                        break;
                     }
                     parent = parent.getParent();
                  }

                  if (sComp != null) {
                     sComp.setOrigin(0, selection);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
               }
            }
         });
         return Status.OK_STATUS;

      }
   }

   private void createCurrentStateAndTeamHeaders(Composite comp, XFormToolkit toolkit) {
      Composite topLineComp = new Composite(comp, SWT.NONE);
      topLineComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      topLineComp.setLayout(ALayout.getZeroMarginLayout(3, false));
      toolkit.adapt(topLineComp);

      try {
         FormsUtil.createLabelText(toolkit, topLineComp, "Current State: ", smaMgr.getStateMgr().getCurrentStateName());
         FormsUtil.createLabelText(toolkit, topLineComp, "Created: ", XDate.getDateStr(
               smaMgr.getLog().getCreationDate(), XDate.MMDDYYHHMM));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      try {
         createOriginatorHeader(topLineComp, toolkit);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      try {
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            FormsUtil.createLabelText(toolkit, topLineComp, "Team: ",
                  ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamName());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      try {
         FormsUtil.createLabelText(
               toolkit,
               topLineComp,
               "Action Id: ",
               smaMgr.getSma().getParentActionArtifact() == null ? "??" : smaMgr.getSma().getParentActionArtifact().getHumanReadableId());
         if (smaMgr.getSma().getParentSMA() != null) {
            FormsUtil.createLabelText(toolkit, topLineComp, "Parent Workflow Id: ",
                  smaMgr.getSma().getParentSMA() == null ? "??" : smaMgr.getSma().getParentSMA().getHumanReadableId());
         }
         FormsUtil.createLabelText(toolkit, topLineComp, smaMgr.getSma().getArtifactSuperTypeName() + " Id: ",
               smaMgr.getSma().getHumanReadableId());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   private void createLatestHeader(Composite comp, XFormToolkit toolkit) throws OseeStateException {
      if (smaMgr.isHistoricalVersion()) {
         Label label =
               toolkit.createLabel(
                     comp,
                     "This is a historical version of this " + smaMgr.getSma().getArtifactTypeName() + " and can not be edited; Select \"Open Latest\" to view/edit latest version.");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      }
   }

   private void createAnnotationsHeader(Composite comp, XFormToolkit toolkit) throws OseeCoreException {
      if (smaMgr.getSma().getAnnotations().size() > 0) {
         new AnnotationComposite(toolkit, comp, SWT.None, smaMgr.getSma());
      }
   }

   public static void createSMANotesHeader(Composite comp, XFormToolkit toolkit, SMAManager smaMgr, int horizontalSpan) throws OseeCoreException {
      // Display SMA Note
      String note = smaMgr.getSma().getSoleAttributeValue(ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName(), "");
      if (!note.equals("")) {
         FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, "Note: " + note);
      }
   }

   public static void createStateNotesHeader(Composite comp, XFormToolkit toolkit, SMAManager smaMgr, int horizontalSpan, String forStateName) throws MultipleAttributesExist {
      // Display global Notes
      for (NoteItem noteItem : smaMgr.getNotes().getNoteItems()) {
         if (forStateName == null || noteItem.getState().equals(forStateName)) {
            FormsUtil.createLabelOrHyperlink(comp, toolkit, horizontalSpan, noteItem.toString());
         }
      }
   }

   private void createOriginatorHeader(Composite comp, XFormToolkit toolkit) throws OseeCoreException {
      Composite topLineComp = new Composite(comp, SWT.NONE);
      topLineComp.setLayoutData(new GridData());
      topLineComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      toolkit.adapt(topLineComp);

      if (!smaMgr.isCancelled() && !smaMgr.isCompleted()) {
         Hyperlink link = toolkit.createHyperlink(topLineComp, ORIGINATOR, SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            public void linkEntered(HyperlinkEvent e) {
            }

            public void linkExited(HyperlinkEvent e) {
            }

            public void linkActivated(HyperlinkEvent e) {
               try {
                  if (smaMgr.promptChangeOriginator()) {
                     updateOrigLabel();
                     smaMgr.getEditor().onDirtied();
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         if (smaMgr.getOriginator() == null) {
            Label errorLabel = toolkit.createLabel(topLineComp, "Error: No originator identified.");
            errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         } else {
            origLabel = toolkit.createLabel(topLineComp, smaMgr.getOriginator().getName());
            origLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         }
      } else {
         if (smaMgr.getOriginator() == null) {
            Label errorLabel = toolkit.createLabel(topLineComp, "Error: No originator identified.");
            errorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         } else {
            Label origLabel = toolkit.createLabel(topLineComp, ORIGINATOR + smaMgr.getOriginator().getName());
            origLabel.setLayoutData(new GridData());
         }
      }
   }

   public void updateOrigLabel() throws OseeCoreException {
      origLabel.setText(smaMgr.getOriginator().getName());
      origLabel.getParent().layout();
   }

   public void refresh() throws OseeCoreException {
      if (smaMgr.getEditor() != null && !smaMgr.isInTransition()) {
         //         System.out.println("SMAWorkFlowTab refresh...");
         for (SMAWorkFlowSection section : sections) {
            section.dispose();
         }
         atsBody.dispose();
         createAtsBody();
         scrolledForm.setText(smaMgr.getEditor().getTitleStr());
         scrolledForm.setImage(ImageManager.getImage(smaMgr.getSma()));
         refreshToolbar();
      }
   }

   public List<AtsWorkPage> getPages() {
      return atsWorkPages;
   }

   public List<SMAWorkFlowSection> getSections() {
      return sections;
   }

   public SMAWorkFlowSection getSectionForCurrentState() {

      return null;
   }

   public List<XWidget> getXWidgetsFromState(String stateName, Class<?> clazz) {
      for (SMAWorkFlowSection section : sections) {
         if (section.getPage().getName().equals(stateName)) {
            return section.getXWidgets(clazz);
         }
      }
      return Collections.emptyList();
   }

}