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

package org.eclipse.osee.ats.editor.service.branch;

import java.sql.SQLException;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.event.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class CommitWorkingBranchService extends WorkPageService implements IEventReceiver {

   private Hyperlink link;
   private final boolean overrideStateValidation;

   public CommitWorkingBranchService(SMAManager smaMgr, boolean overrideStateValidation) {
      super(smaMgr);
      this.overrideStateValidation = overrideStateValidation;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) {
      return isCurrentState(page) && page.isAllowCommitBranch();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createSidebarService(org.eclipse.swt.widgets.Group, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   @Override
   public void createSidebarService(Group workGroup, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      if (smaMgr.getCurrentStateName().equals(page.getName())) {
         link =
               toolkit.createHyperlink(workGroup,
                     getName() + (overrideStateValidation ? "\nOverride State Validation" : ""), SWT.NONE);
         if (smaMgr.getSma().isReadOnly())
            link.addHyperlinkListener(readOnlyHyperlinkListener);
         else
            link.addHyperlinkListener(new IHyperlinkListener() {

               public void linkEntered(HyperlinkEvent e) {
               }

               public void linkExited(HyperlinkEvent e) {
               }

               public void linkActivated(HyperlinkEvent e) {
                  Result result = smaMgr.getBranchMgr().commitWorkingBranch(true, overrideStateValidation);
                  if (result.isFalse()) result.popup();
               }
            });
      }
      SkynetEventManager.getInstance().register(LocalBranchEvent.class, this);
      SkynetEventManager.getInstance().register(RemoteBranchEvent.class, this);
      refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Commit Working Branch";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getSidebarCategory()
    */
   @Override
   public String getSidebarCategory() {
      return CreateWorkingBranchService.BRANCH_CATEGORY;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#refresh()
    */
   @Override
   public void refresh() {
      if (link != null && !link.isDisposed()) {
         boolean enabled = false;
         try {
            enabled = smaMgr.getBranchMgr().isWorkingBranch() && !smaMgr.getBranchMgr().isCommittedBranch();
         } catch (SQLException ex) {
            // do nothing
         }
         link.setEnabled(enabled);
         link.setUnderlined(enabled);
      }
   }

   public void onEvent(Event event) {
      if (event instanceof BranchEvent) {
         refresh();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#dispose()
    */
   @Override
   public void dispose() {
      SkynetEventManager.getInstance().unRegisterAll(this);
   }
}
