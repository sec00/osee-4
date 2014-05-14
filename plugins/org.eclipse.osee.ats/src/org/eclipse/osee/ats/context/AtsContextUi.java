/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.context;

import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.context.AtsContextUtil;
import org.eclipse.osee.ats.api.context.IAtsContextLoadListener;
import org.eclipse.osee.ats.api.context.IAtsContextService;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialogWithBranchSelect;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class AtsContextUi {

   private final IAtsContextService context;
   private final IAtsContextLoadListener loadListener;

   public AtsContextUi(IAtsContextService context, IAtsContextLoadListener loadListener) {
      this.context = context;
      this.loadListener = loadListener;
   }

   public void createContextAction(final IMenuManager mm) {
      for (String uuid : context.getContextUuids()) {
         createContextActionItem(mm, uuid, context.getContextName(uuid));
      }

      mm.add(new Separator());

      Action action2 = new Action("Store Current Context as Default") {
         @Override
         public void run() {
            storeCurrentAsDefault();
         }
      };
      action2.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.CENTER));
      mm.add(action2);

      mm.add(new Separator());

      Action action = new Action("Configure Context Branch") {
         @Override
         public void run() {
            if (configureContextBranch()) {
               try {
                  loadListener.store();
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Exception configuring context branch", ex);
               }
            }
         }
      };
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.CENTER));
      mm.add(action);

      action = new Action("Remove Context Branch Configuration") {
         @Override
         public void run() {
            removeContext();
         }
      };
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.CENTER));
      mm.add(action);

      mm.add(new Separator());

      action = new Action("Create New Context Branch") {
         @Override
         public void run() {
            if (!AtsUtilClient.isAtsAdmin()) {
               AWorkbench.popup("Admin-Only Function");
               return;
            }
            loadListener.createNewContextBranch();
         }
      };
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.CENTER));
      mm.add(action);

   }

   protected void storeCurrentAsDefault() {
      String currentUuid = System.getProperty(AtsContextUtil.ATS_SYSTEM_CURRENT_CONTEXT_UUID);
      try {
         loadListener.storeAsDefault(currentUuid);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Exception storing default context branch", ex);
      }
   }

   protected void removeContext() {
      if (!AtsUtilClient.isAtsAdmin()) {
         AWorkbench.popup("Admin-Only Function");
         return;
      }
      AWorkbench.popup("No Implemented");
   }

   protected boolean configureContextBranch() {
      boolean success = false;
      if (!AtsUtilClient.isAtsAdmin()) {
         AWorkbench.popup("Admin-Only Function");
      } else {
         EntryDialogWithBranchSelect dialog =
            new EntryDialogWithBranchSelect("Configure ATS Context Branch",
               "Enter Unique Context Name and Select Existing Branch");
         if (dialog.open() == 0) {
            Set<String> ids = context.getContextUuids();
            IOseeBranch branch = dialog.getBranch();
            String contextUuid = String.valueOf(branch.getUuid());
            String contextName = dialog.getEntry();
            if (!Strings.isValid(contextName)) {
               AWorkbench.popup("Invalid Ats Context Name: [" + contextName + "]");
            } else if (ids.contains(contextUuid)) {
               AWorkbench.popup("Branch already an Ats Context with id: " + contextUuid);
            } else if (context.getContextName(contextUuid) != null) {
               AWorkbench.popup("Branch already an Ats Context named: " + context.getContextName(contextUuid));
            } else {
               context.addContext(contextUuid, contextName);
               success = true;
            }
         }
      }
      return success;
   }

   private void createContextActionItem(final IMenuManager mm, final String contextUuid, final String contextName) {
      Action action = new Action(String.format("Select Context: %s (%s)", contextName, contextUuid)) {
         @Override
         public void run() {
            loadListener.switchToContext(contextUuid, contextName);
         }
      };
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.CENTER));
      mm.add(action);
   }
}
