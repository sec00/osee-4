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

package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.search.ui.text.Match;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactDoubleClick implements IDoubleClickListener {
   @Override
   public void doubleClick(DoubleClickEvent event) {
      openArtifact(event.getSelection());
   }

   public static void openArtifact(ISelection selection) {
      IStructuredSelection structSel = (IStructuredSelection) selection;
      Object object = structSel.getFirstElement();
      Artifact artifact = null;
      if (object instanceof Artifact) {
         artifact = (Artifact) structSel.getFirstElement();
      } else if (object instanceof Match) {
         Match match = (Match) object;

         if (match.getElement() instanceof Artifact) {
            artifact = (Artifact) match.getElement();
         }
      }

      if (artifact == null) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "The artifact associated with the double-click was null");
      } else {
         try {
            if (AccessControlManager.hasPermission(artifact, PermissionEnum.READ)) {
               RendererManager.openInJob(artifact, PresentationType.DEFAULT_OPEN);
            } else {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                  "The user " + UserManager.getUser() + " does not have read access to " + artifact);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }
}