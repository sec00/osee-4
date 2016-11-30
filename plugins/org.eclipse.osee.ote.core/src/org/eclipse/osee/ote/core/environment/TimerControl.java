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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.osee.ote.core.environment.interfaces.BasicTimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.message.timer.CancelTimerFromReg;
import org.eclipse.osee.ote.message.timer.EnvTaskWrapper;
import org.eclipse.osee.ote.message.timer.NotifyAfterTimeout;
import org.eclipse.ote.scheduler.OTETaskRegistration;
import org.eclipse.ote.scheduler.Scheduler;

public abstract class TimerControl implements ITimerControl {

   private IRunManager runManager;
   protected Scheduler scheduler;
   private Map<EnvironmentTask, OTETaskRegistration> tasks = new ConcurrentHashMap<>();
   private int cycleCount;
   
   public TimerControl(Scheduler scheduler, int maxTimers) {
      this.scheduler = scheduler;
      this.scheduler.start();
   }

   public Scheduler getScheduler(){
      return scheduler;
   }
   
   @Override
   public void envWait(int milliseconds) throws InterruptedException {
      envWait(new BasicTimeout(), milliseconds);
   }

   @Override
   public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
      scheduler.envWait((long)milliseconds);
   }

   @Override
   public void setRunManager(IRunManager runManager) {
      this.runManager = runManager;
   }

   @Override
   public IRunManager getRunManager() {
      return runManager;
   }
   
   @Override
   public long getEnvTime() {
      return scheduler.getTime();
   }

   @Override
   public ICancelTimer setTimerFor(ITimeout objToNotify, int milliseconds) {
      objToNotify.setTimeout(false);
      OTETaskRegistration reg = scheduler.scheduleWithDelay(new NotifyAfterTimeout(objToNotify), milliseconds);
      return new CancelTimerFromReg(reg);
   }

   @Override
   public void addTask(EnvironmentTask envTask, TestEnvironment environment) {
      EnvTaskWrapper task = new EnvTaskWrapper(envTask);
      tasks.put(envTask, scheduler.scheduleAtFixedRate(task, envTask.getHzRate()));
   }

   @Override
   public void removeTask(EnvironmentTask task) {
      OTETaskRegistration envTask = tasks.remove(task);
      if(envTask != null){
         envTask.unregister();
      }
   }

   @Override
   public void step() {
   }

   @Override
   public int getCycleCount() {
      return cycleCount;
   }

   @Override
   public void incrementCycleCount() {
      cycleCount++;
   }

   @Override
   public void setCycleCount(int cycle) {
      cycleCount = cycle;
   }

   @Override
   public void cancelAllTasks() {
      for (OTETaskRegistration t : tasks.values()) {
         t.unregister();
      }
      tasks.clear();
   }

   @Override
   public void dispose() {
      cancelAllTasks();
   }
   
   @Override
   public void cancelTimers() {
      
   }

}
