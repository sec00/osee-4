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

import org.eclipse.osee.ote.core.environment.TimerControl;
import org.eclipse.ote.scheduler.SchedulerImpl;
import org.eclipse.ote.scheduler.SchedulerImpl.DelayStrategy;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RealTime extends TimerControl {
   
   /**
    * Constructor
    */
   public RealTime() {
      super(new SchedulerImpl(false, DelayStrategy.sleep), (Runtime.getRuntime().availableProcessors() + 1) / 2 + 1);
   }

   @Override
   public long getTimeOfDay() {
      return getEnvTime();
   }

   @Override
   public boolean isRealtime() {
      return true;
   }

}