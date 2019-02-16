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
package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsGroup;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;

/**
 * @author Donald G. Dunne
 */
public class ToggleAtsAdmin extends XNavigateItemAction {

   public ToggleAtsAdmin(XNavigateItem parent) {
      super(parent, "Toggle ATS Admin - Temporary", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      run();
   }

   public static void run() {
      try {
         if (!AtsGroup.AtsTempAdmin.isCurrentUserMember()) {
            AWorkbench.popup("Current User not configured for Temporary Admin");
            return;
         }
         boolean isAdmin = AtsClientService.get().getUserService().isAtsAdmin();
         String message = "Currently " + (isAdmin ? "ADMIN" : "NOT ADMIN") + " - Toggle?";
         CheckBoxDialog diag = new CheckBoxDialog("Toggle Admin", message, "Persist Admin?");
         if (diag.open() == Window.OK) {
            boolean persist = diag.isChecked();
            if (!isAdmin) {
               if (persist) {
                  AtsGroup.AtsAdmin.addMember(UserManager.getUser());
                  AtsGroup.AtsAdmin.getArtifact().persist("Toggle Admin");
                  SystemGroup.OseeAdmin.addMember(UserManager.getUser());
                  SystemGroup.OseeAdmin.getArtifact().persist("Toggle Admin");
               }
               AtsGroup.AtsAdmin.setTemporaryOverride(true);
               SystemGroup.OseeAdmin.setTemporaryOverride(true);
            } else {
               if (persist) {
                  AtsGroup.AtsAdmin.removeMember(UserManager.getUser());
                  AtsGroup.AtsAdmin.getArtifact().persist("Toggle Admin");
                  SystemGroup.OseeAdmin.removeMember(UserManager.getUser());
                  SystemGroup.OseeAdmin.getArtifact().persist("Toggle Admin");
               }
               AtsGroup.AtsAdmin.removeTemporaryOverride();
               SystemGroup.OseeAdmin.removeTemporaryOverride();
            }
            AtsClientService.get().getConfigService().getConfigurationsWithPend();
            AtsClientService.get().getUserService().isAtsAdmin(false);
            NavigateViewItems.getInstance().clearCaches();
            for (WorkflowEditor editor : WorkflowEditor.getWorkflowEditors()) {
               editor.refreshPages();
            }
            NavigateView.getNavigateView().refreshData();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}