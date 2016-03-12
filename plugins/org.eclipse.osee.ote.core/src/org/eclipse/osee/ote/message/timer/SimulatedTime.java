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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TimerControl;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.ote.scheduler.OTETaskRegistration;
import org.eclipse.ote.scheduler.Scheduler;

/**
 * We use a frequency resolution of 300hz.
 * 
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class SimulatedTime extends TimerControl {

//   private static final class Task {
//      private final EnvironmentTask task;
//      private final TestEnvironment env;
//
//      public Task(EnvironmentTask task, TestEnvironment env) {
//         super();
//         this.task = task;
//         this.env = env;
//      }
//
//      public void doTask(int cycleCount) {
//         try {
//            task.baseRunOneCycle(cycleCount);
//         } catch (Throwable ex) {
//            OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE,
//                  "Aborting the test script because an Environment Task is failing", ex);
//            if(env != null){
//               env.getRunManager().abort(ex, false);
//            }
//         }
//      }
//   }
//   private final Collection<CycleCountDown> cycleCounters;
//   private final Collection<CycleCountDown> scriptCycleCounters;
   private final IScriptControl scriptControl;
   private int cycleCount;
//   private final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<>();

   private Map<EnvironmentTask, OTETaskRegistration> tasks = new ConcurrentHashMap<>();
   
   private final long sysTime;

   /**
    * @param scriptControl -
    */
   public SimulatedTime(Scheduler scheduler, IScriptControl scriptControl) throws IOException {
      super(scheduler, 3);
      this.scriptControl = scriptControl;
//      cycleCounters = new HashSet<>(32);
//      scriptCycleCounters = new HashSet<>(32);
      cycleCount = 0;
      sysTime = System.currentTimeMillis();
   }

   @Override
   public long getEnvTime() {
      return scheduler.getTime();
//      return (long) (cycleCount * 1000.0 / EnvironmentTask.cycleResolution);
   }

   @Override
   public ICancelTimer setTimerFor(ITimeout objToNotify, int milliseconds) {
      OTETaskRegistration reg = scheduler.scheduleWithDelay(new NotifyAfterTimeout(objToNotify), true, milliseconds);
//      CycleCountDown cycleCountDown = new CycleCountDown(scriptControl, objToNotify,
//            (int) Math.rint(milliseconds / (1000.0 / EnvironmentTask.cycleResolution)) - 1);
//      synchronized (cycleCounters) {
//         if(getRunManager().isCurrentThreadScript()){
//            scriptCycleCounters.add(cycleCountDown);
//         } else {
//            cycleCounters.add(cycleCountDown);
//         }
//      }
//
//      unlockScriptControl();

      return new CancelTimerFromReg(reg);
   }

   protected void unlockScriptControl() {
      try {
         scriptControl.unlock();
      } catch (IllegalMonitorStateException ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void addTask(EnvironmentTask envTask, TestEnvironment environment) {
      
      System.out.println(envTask.getClass().getName() + " - " + envTask.toString());
      
      EnvTaskWrapper task = new EnvTaskWrapper(envTask);
      tasks.put(envTask, scheduler.scheduleAtFixedRate(task, envTask.getHzRate()));
      
      
//      for (Task task : tasks) {
//         if (task.task == envTask) {
//            return;
//         }
//      }
//
//      tasks.add(new Task(envTask, environment));
   }

   @Override
   public void removeTask(EnvironmentTask task) {
      OTETaskRegistration envTask = tasks.remove(task);
      if(envTask != null){
         envTask.unregister();
      }
//      Task itemToRemove = null;
//      for (Task t : tasks) {
//         if (t.task == task) {
//            itemToRemove = t;
//            break;
//         }
//      }
//      if (itemToRemove != null) {
//         OseeLog.log(MessageSystemTestEnvironment.class, Level.FINE, "removing environment task " + task.toString());
//         tasks.remove(itemToRemove);
//      }
   }

   @Override
   public void step() {

//      for (Task t : tasks) {
//         t.doTask(cycleCount);
//      }
//      incrementCycleCount();
   }

   @Override
   public int getCycleCount() {
      return cycleCount;
   }

   public Collection<CycleCountDown> getCycleCounters() {
      return null;//scriptCycleCounters;
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

   public void removeOccurredCycleCounters() {
//      synchronized (cycleCounters) {
//         Iterator<CycleCountDown> iter = cycleCounters.iterator();
//         while (iter.hasNext()) {
//            CycleCountDown counter = iter.next();
//            if (counter.cycleOccurred()) {
//               iter.remove();
//            }
//         }
//         iter = scriptCycleCounters.iterator();
//         while (iter.hasNext()) {
//            CycleCountDown counter = iter.next();
//            if (counter.cycleOccurred()) {
//               iter.remove();
//            }
//         }
//      }
   }

   @Override
   public void dispose() {
//      cycleCounters.clear();
//      scriptCycleCounters.clear();
//      tasks.clear();
      cancelAllTasks();
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