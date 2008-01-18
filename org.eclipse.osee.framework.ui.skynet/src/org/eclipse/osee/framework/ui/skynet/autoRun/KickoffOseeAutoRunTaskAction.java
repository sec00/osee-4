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
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * This Action (kicked off from Artifact Explorer pulldown toolbar menu) will kickoff an event task that will in turn
 * kickoff instances of OSEE to run certain tests. Each individual instance of OSEE will run the AutoRunStartup.java
 * class which will in turn, through extension points, run whatever test was requested to run.
 * 
 * @author Donald G. Dunne
 */
public class KickoffOseeAutoRunTaskAction extends Action {

   public KickoffOseeAutoRunTaskAction() {
      super("Kickoff Osee Auto Run Task");
      setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("clock.gif"));
      setEnabled(OseeProperties.getInstance().isDeveloper());
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
         String launchFile = "org.eclipse.osee.framework.ui.skynet\\AutoRun.launch";
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

}
