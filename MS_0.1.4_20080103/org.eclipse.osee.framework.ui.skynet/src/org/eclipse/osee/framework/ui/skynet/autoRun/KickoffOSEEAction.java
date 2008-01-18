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

package org.eclipse.osee.framework.ui.skynet.autoRun;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class KickoffOSEEAction extends Action {

   @Override
   public void run() {
      try {
         ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
         String launchFile = "osee.runConfigs.db.postgres\\OSEE product [postgresql localhost].launch";
         File file = AWorkspace.getWorkspaceFile(launchFile);
         if (!file.exists()) {
            AWorkbench.popup("ERROR", "Can't locate file \"" + launchFile + "\"");
            return;
         }
         IFile iFile = AWorkspace.fileToIFile(file);
         if (iFile == null || !iFile.exists()) {
            AWorkbench.popup("ERROR", "Can't locate file \"" + launchFile + "\"");
            return;
         }
         ILaunchConfiguration config = manager.getLaunchConfiguration(iFile);
         config.launch(ILaunchManager.RUN_MODE, null);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   public void kickoffOsee() {
      //    String command =
      //    "\"C:\\Program Files\\OSEE\\eclipse.exe\" -product org.eclipse.osee.framework.ui.product.osee" + " -showsplash org.eclipse.osee.framework.ui.product -data C:\\UserData\\workspace_autorun -vmargs -XX:MaxPermSize=256m -Xmx768M -DAtsAdmin -DEmailMe";
      //try {
      // Process child = Runtime.getRuntime().exec(command);
      //} catch (Exception ex) {
      // OSEELog.logException(AtsPlugin.class, ex, true);
      //}
   }
}
