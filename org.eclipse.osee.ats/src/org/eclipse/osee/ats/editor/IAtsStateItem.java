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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateItem {

   public Result pageCreated(FormToolkit toolkit, AtsWorkPage page, SMAManager smaMgr, XModifiedListener xModListener, boolean isEditable) throws Exception;

   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws Exception;

   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws Exception;

   public void widgetModified(SMAWorkFlowSection section, XWidget xWidget) throws Exception;

   public String getOverrideTransitionToStateName(SMAWorkFlowSection section) throws Exception;

   public Collection<User> getOverrideTransitionToAssignees(SMAWorkFlowSection section) throws Exception;

   public String getDescription() throws Exception;

   public String getBranchShortName(SMAManager smaMgr) throws Exception;

   public boolean isAccessControlViaAssigneesEnabledForBranching() throws Exception;

   public Collection<String> getIds() throws Exception;

   public List<WorkPageService> getServices(SMAManager smaMgr) throws Exception;

   /**
    * @param smaMgr
    * @param fromState
    * @param toState
    * @param toAssignees
    * @return Result of operation. If Result.isFalse(), transition will not continue and Result.popup will occur.
    * @throws Exception TODO
    */
   public Result transitioning(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws Exception;

   public void transitioned(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws Exception;

   /**
    * @param smaMgr TODO
    * @return Result of operation. If Result.isFalse(), commit will not continue and Result.popup will occur.
    * @throws Exception TODO
    */
   public Result committing(SMAManager smaMgr) throws Exception;

}
