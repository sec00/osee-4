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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowCancelledSection extends SMAWorkFlowSection {

   /**
    * @param parent
    * @param toolkit
    * @param style
    * @param page
    * @param smaMgr
    * @throws Exception
    */
   public SMAWorkFlowCancelledSection(Composite parent, XFormToolkit toolkit, int style, AtsWorkPage page, SMAManager smaMgr) throws Exception {
      super(parent, toolkit, style, page, smaMgr);
   }

   @Override
   protected Composite createWorkArea(Composite comp, AtsWorkPage page, XFormToolkit toolkit) throws Exception {
      Composite workComp = super.createWorkArea(comp, page, toolkit);
      LogItem item = smaMgr.getSma().getLog().getStateEvent(LogType.StateCancelled);
      toolkit.createLabel(workComp, "Cancellation Reason: " + item.getMsg());
      toolkit.createLabel(workComp, "Cancelled From State: " + item.getState());

      if (smaMgr.getSma().isUnCancellable()) {
         final LogItem fItem = item;
         Button button = toolkit.createButton(workComp, "Return to \"" + item.getState() + "\"", SWT.PUSH);
         button.addListener(SWT.MouseUp, new Listener() {
            public void handleEvent(Event event) {
               handleUnCancel(fItem.getState());
            }
         });
      }
      return workComp;
   }

   private void handleUnCancel(String toStateName) {
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Return to \"" + toStateName + "\"",
            "Return to \"" + toStateName + "\"?")) {
         SMAState toSmaState = smaMgr.getStateDam().getState(toStateName, false);
         if (toSmaState == null) {
            AWorkbench.popup("ERROR", "Return to state doesn't exist");
            throw new IllegalArgumentException("Invalid return-to state \"" + toStateName + "\"");
         }
         Result result = smaMgr.transition(toStateName, toSmaState.getAssignees(), true, false);
         if (result.isFalse()) result.popup();
      }
   }

}
