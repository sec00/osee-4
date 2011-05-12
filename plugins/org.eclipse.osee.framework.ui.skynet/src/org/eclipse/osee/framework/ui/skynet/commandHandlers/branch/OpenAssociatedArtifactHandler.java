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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCm;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
 */
public class OpenAssociatedArtifactHandler extends CommandHandler {

   @Override
   public Object executeWithException(ExecutionEvent event) throws OseeCoreException {
      IStructuredSelection selection =
         (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      Branch selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      Artifact associatedArtifact = BranchManager.getAssociatedArtifact(selectedBranch);
      if (associatedArtifact == null) {
         AWorkbench.popup("Open Associated Artifact", "No artifact associated with branch " + selectedBranch);
         return null;
      }
      if (AccessControlManager.hasPermission(associatedArtifact, PermissionEnum.READ)) {
         if (OseeCm.getInstance().isPcrArtifact(associatedArtifact)) {
            OseeCm.getInstance().openArtifact(associatedArtifact, OseeCmEditor.CmPcrEditor);
         } else {
            RendererManager.open(associatedArtifact, PresentationType.DEFAULT_OPEN);
         }
      } else {
         OseeLog.log(
            SkynetGuiPlugin.class,
            OseeLevel.SEVERE_POPUP,
            "The user " + UserManager.getUser() + " does not have read access to " + selectedBranch.getAssociatedArtifactId());
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends IOseeBranch> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return branches.size() == 1;
   }
}