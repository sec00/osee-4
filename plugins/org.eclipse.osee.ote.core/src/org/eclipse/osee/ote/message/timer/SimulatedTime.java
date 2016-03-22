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
package org.eclipse.osee.ote.message.timer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TimerControl;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.ote.scheduler.OTETaskRegistration;
import org.eclipse.ote.scheduler.Scheduler;

/**
 * We use a frequency resolution of 300hz.
 * 
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class SimulatedTime extends TimerControl {

   private final long sysTime;

   /**
    * @param scriptControl -
    */
   public SimulatedTime(Scheduler scheduler, IScriptControl scriptControl) throws IOException {
      super(scheduler, 3);
      sysTime = System.currentTimeMillis();
   }

   @Override
   public long getTimeOfDay() {
      return sysTime + getEnvTime();
   }

   @Override
   public boolean isRealtime() {
      return false;
   }
}