package org.eclipse.osee.ote.message;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osee.ote.message.condition.ICondition;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.MsgWaitResult;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;
import org.eclipse.ote.scheduler.OTETaskRegistration;
import org.eclipse.ote.scheduler.Scheduler;

public class WaitOnConditionWithWallClockTimeout {

   private ReentrantLock lock;
   private Scheduler scheduler;
   private ICondition condition;
   private Condition checkCondition;
   private SignalNewData signalCheck;
   private SignalTimeout timeout;
   private boolean maintain;

   public WaitOnConditionWithWallClockTimeout(Scheduler scheduler, ICondition condition, boolean maintain){
      this.scheduler = scheduler;
      this.condition = condition;
      lock = new ReentrantLock();
      checkCondition = lock.newCondition();
      signalCheck = new SignalNewData(lock, checkCondition);
      timeout = new SignalTimeout(lock, checkCondition);
      this.maintain = maintain;
   }

   public MsgWaitResult startWaiting(long timeoutInMs, long wallClockTimeoutInMs, Message message){
      long time = 0l;
      boolean pass = false;
      lock.lock();
      OTETaskRegistration cancelTask = null;
      OTETaskRegistration cancelOtherTask = null;
      try{
         time = scheduler.getTime();
         message.addListener(signalCheck);
         cancelTask = scheduler.scheduleWithDelay(timeout, timeoutInMs);
         cancelOtherTask = scheduler.scheduleWithDelayRealTime(timeout, wallClockTimeoutInMs);
         pass = condition.check();
         boolean done = pass ^ maintain;
         while (!done && !cancelTask.isComplete() && !cancelOtherTask.isComplete()) {
            checkCondition.await(1000, TimeUnit.MILLISECONDS);
            pass = condition.checkAndIncrement();
            done = pass ^ maintain;
         }
         time = scheduler.getTime() - time;
      } catch (InterruptedException e) {
      } finally {
         message.removeListener(signalCheck);
         if(cancelTask != null){
            cancelTask.unregister();
         }
         if(cancelOtherTask != null){
            cancelOtherTask.unregister();
         }
         lock.unlock();
      }
      return new MsgWaitResult(time, condition.getCheckCount(), pass);
   }

   class SignalTimeout implements Runnable {

      private ReentrantLock lock;
      private Condition condition;
      private volatile boolean timeout = false;

      SignalTimeout(ReentrantLock lock, Condition condition){
         this.lock = lock;
         this.condition = condition;
      }

      @Override
      public void run() {
         lock.lock();
         try{
            timeout = true;
            condition.signal();            
         } finally {
            lock.unlock();
         }
      }

      public boolean getTimeout(){
         return timeout;
      }

   }

   class SignalNewData implements IOSEEMessageListener {

      private ReentrantLock lock;
      private Condition condition;

      SignalNewData(ReentrantLock lock, Condition condition){
         this.lock = lock;
         this.condition = condition;
      }

      @Override
      public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
         lock.lock();
         try{
            condition.signal();
         } finally {
            lock.unlock();
         }
      }

      @Override
      public void onInitListener() throws MessageSystemException {

      }

   }


}
