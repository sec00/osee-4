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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.AEmail;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.util.IAutoRunTask;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

/**
 * This class will be kicked off during any normal run of OSEE. It will check for any -DAutoRun=taskId options and
 * inquire, through extension points, which plugins need to perform tasks for the taskId specified. After tests are
 * completed, this class will shutdown the workbench.
 * 
 * @author Donald G. Dunne
 */
public class AutoRunStartup implements IStartup {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(AutoRunStartup.class);

   /* (non-Javadoc)
    * @see org.eclipse.ui.IStartup#earlyStartup()
    */
   public void earlyStartup() {
      final String autoRunTaskId = OseeProperties.getInstance().getAutoRun();
      try {
         if (autoRunTaskId == null) {
            logger.log(Level.INFO, "Checked AutoRunStartup...Nothing to run.");
            return;
         }

         // Run the tasks that match the taskId
         logger.log(Level.INFO, "Running AutoRunStartup; Id=\"" + autoRunTaskId + "\"");
         run(autoRunTaskId);

         // Send email of completion
         AEmail email =
               new AEmail(new String[] {"donald.g.dunne@boeing.com"}, "donald.g.dunne@boeing.com",
                     "donald.g.dunne@boeing.com", "Auto Run Completed; Id=\"" + autoRunTaskId + "\" ", "Completed");
         email.send();
         logger.log(Level.INFO, "Sleeping...");
         Thread.sleep(2000);
         logger.log(Level.INFO, "Exiting AutoRunStartup; Id=\"" + autoRunTaskId + "\"");
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            public void run() {
               PlatformUI.getWorkbench().close();
            }
         });
      } catch (Exception ex) {
         AEmail email =
               new AEmail(new String[] {"donald.g.dunne@boeing.com"}, "donald.g.dunne@boeing.com",
                     "donald.g.dunne@boeing.com", "Auto Run Exceptioned; Id=\"" + autoRunTaskId + "\" Exceptioned",
                     "Exception\n\n" + Lib.exceptionToString(ex));
         email.send();
      }
   }

   /**
    * Execute the autoRun.startTasks with the given unique extension id
    * 
    * @param autoRunTaskId unique AutoRunTask extension id
    * @throws Exception
    */
   private void run(String autoRunTaskId) throws Exception {
      List<IExtension> iExtensions =
            ExtensionPoints.getExtensionsByUniqueId("org.eclipse.osee.framework.skynet.core.AutoRunTask",
                  Arrays.asList(new String[] {autoRunTaskId}));
      for (IExtension iExtension : iExtensions) {
         for (IConfigurationElement element : iExtension.getConfigurationElements()) {
            String className = element.getAttribute("classname");
            String bundleName = element.getContributor().getName();
            if (className != null && bundleName != null) {
               Bundle bundle = Platform.getBundle(bundleName);
               Class<?> interfaceClass = bundle.loadClass(className);
               IAutoRunTask autoRunTask = (IAutoRunTask) interfaceClass.getConstructor().newInstance();
               autoRunTask.startTasks();
            }
         }
      }
   }
}