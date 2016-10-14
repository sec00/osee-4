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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.change;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.osee.framework.ui.skynet.action.WasIsCompareEditorAction;
import org.eclipse.osee.framework.ui.skynet.action.WasIsCompareEditorChangeAction;

/**
 * @author Donald G. Dunne
 */
public class ViewChangeReportWasIsHandler extends AbstractHandler {

   @Override
   public Object execute(ExecutionEvent event) {
      new WasIsCompareEditorChangeAction().run();
      return null;
   }

   @Override
   public boolean isEnabled() {
      return WasIsCompareEditorAction.isEnabledStatic();
   }
}