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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.ui.IStartup;

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
      logger.log(Level.INFO, "Running AutoRunStartup");
   }
}