package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.ote.scheduler.Scheduler;

public class FakeTimer implements ITimerControl {
    long step = 0;
      
      @Override
      public void step() {
         step++;
      }
      
      public void step(long time) {
         step += time;
      }
      
      @Override
      public ICancelTimer setTimerFor(ITimeout objToNotify, int milliseconds) {
         return null;
      }
      
      @Override
      public void setRunManager(IRunManager runManager) {
      }
      
      @Override
      public void setCycleCount(int cycle) {
      }
      
      @Override
      public void removeTask(EnvironmentTask task) {
      }
      
      @Override
      public boolean isRealtime() {
         return false;
      }
      
      @Override
      public void incrementCycleCount() {
      }
      
      @Override
      public long getTimeOfDay() {
         return step;
      }
      
      @Override
      public IRunManager getRunManager() {
         return null;
      }
      
      @Override
      public long getEnvTime() {
         return step;
      }
      
      @Override
      public int getCycleCount() {
         return 0;
      }
      
      @Override
      public void envWait(int milliseconds) throws InterruptedException {
      }
      
      @Override
      public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
      }
      
      @Override
      public void dispose() {
      }
      
      @Override
      public void cancelTimers() {
      }
      
      @Override
      public void cancelAllTasks() {
      }
      
      @Override
      public void addTask(EnvironmentTask task, TestEnvironment environment) {
      }

      @Override
      public Scheduler getScheduler() {
         // TODO Auto-generated method stub
         return null;
      }
   }