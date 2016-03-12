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
package org.eclipse.osee.ote.core.environment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.osee.ote.core.environment.interfaces.BasicTimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.ote.scheduler.Scheduler;

public abstract class TimerControl implements ITimerControl {

   private final ScheduledExecutorService executor;
   private IRunManager runManager;
   protected Scheduler scheduler;
   

   public TimerControl(Scheduler scheduler, int maxTimers) {
      executor = Executors.newScheduledThreadPool(maxTimers);
      this.scheduler = scheduler;
      this.scheduler.start();
   }

   public Scheduler getScheduler(){
      return scheduler;
   }
   
   @Override
   public void cancelTimers() {
      executor.shutdown();
   }

   protected ScheduledFuture<?> schedulePeriodicTask(Runnable task, long initialDelay, long period) {
      throw new IllegalArgumentException("no one should call this");
//      return executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
   }

   protected ScheduledFuture<?> scheduleOneShotTask(Runnable task, long delay) {
      throw new IllegalStateException("no one should call this");
//      return executor.schedule(task, delay, TimeUnit.MILLISECONDS);
   }

   @Override
   public void envWait(int milliseconds) throws InterruptedException {
      envWait(new BasicTimeout(), milliseconds);
   }

   @Override
   public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
      scheduler.envWait((long)milliseconds);
//      synchronized (obj) {
//         setTimerFor(obj, milliseconds);
//         obj.wait();
//      }
   }

   @Override
   public void setRunManager(IRunManager runManager) {
      this.runManager = runManager;
   }

   @Override
   public IRunManager getRunManager() {
      return runManager;
   }
}
