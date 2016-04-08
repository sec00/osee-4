package org.eclipse.osee.ote.message;

import java.util.List;
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

public class WaitOnCondition {

   private ReentrantLock lock;
   private Scheduler scheduler;
   private ICondition condition;
   private List<Message> messages;
   private long timeoutInMs;
   private Condition checkCondition;
   private SignalNewData signalCheck;
   private SignalTimeout timeout;
   private boolean maintain;

   public WaitOnCondition(Scheduler scheduler, ICondition condition, boolean maintain, List<Message> messages, long timeoutInMs){
      this.scheduler = scheduler;
      this.condition = condition;
      this.messages = messages;
      this.timeoutInMs = timeoutInMs;
      lock = new ReentrantLock();
      checkCondition = lock.newCondition();
      signalCheck = new SignalNewData(lock, checkCondition, condition);
      timeout = new SignalTimeout(lock, checkCondition);
      this.maintain = maintain;
   }

   public MsgWaitResult startWaiting(){
      long time = 0l;
      boolean pass = false;
      lock.lock();
      OTETaskRegistration cancelTask = null;
      try{
         //loop through messages and add the signal listener
         time = scheduler.getTime();
         for(Message msg:messages){
            msg.addListener(signalCheck);
         }
         if(timeoutInMs > 0){
            cancelTask = scheduler.scheduleWithDelay(timeout, timeoutInMs);
         }
         pass = condition.check();
         boolean done = pass ^ maintain;
         while (!done && (cancelTask != null && !cancelTask.isComplete())) {
            scheduler.pauseSimulated(false);
            checkCondition.await(1000, TimeUnit.MILLISECONDS);
//            System.out.println(getClass().getSimpleName() + " done with wait - " + scheduler.getTime());
            pass = condition.check();
//            System.out.println(getClass().getSimpleName() + " done with wait 2 - " + scheduler.getTime());
            done = pass ^ maintain;
//            System.out.println(getClass().getSimpleName() + " done with wait 3 - " + scheduler.getTime());
            if(done){
//               System.out.println(getClass().getSimpleName() + " done with wait 4 - " + scheduler.getTime());
               cancelTask.unregister();
//               System.out.println(getClass().getSimpleName() + " done with wait 5 - " + scheduler.getTime());
            }
//            System.out.println(getClass().getSimpleName() + " done with check - " + done +" " + scheduler.getTime());
         }
//         System.out.println(getClass().getSimpleName() + " out of check loop - " + scheduler.getTime());
         time = scheduler.getTime() - time;
      } catch (InterruptedException e) {
      } finally {
         for(Message msg:messages){
            msg.removeListener(signalCheck);
         }
         if(cancelTask != null){
            cancelTask.unregister();
         }
         scheduler.pauseSimulated(false);
         lock.unlock();
      }
//      System.out.println(getClass().getSimpleName() + " returning - " + scheduler.getTime());
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
      private ICondition check;

      SignalNewData(ReentrantLock lock, Condition condition, ICondition check){
         this.lock = lock;
         this.condition = condition;
         this.check = check;
      }

      @Override
      public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
         lock.lock();
         try{
            scheduler.pauseSimulated(true);
            check.increment();
            condition.signal();
//            System.out.println(getClass().getSimpleName() + " SignalNewData - " + scheduler.getTime());
         } finally {
            lock.unlock();
         }
      }

      @Override
      public void onInitListener() throws MessageSystemException {
         // TODO Auto-generated method stub

      }

   }


}
