package org.eclipse.ote.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SchedulerImpl implements Scheduler {

   private ReentrantLock lock = new ReentrantLock();

   public enum DelayStrategy{
      busy, yeild, sleep
   }
   
   private OTEClock clock;
   private volatile boolean run = false;
   
   
//   private SortedOnInsertList<OTETask> tasks = new SortedOnInsertList<>(OTETask.class);
//   private SortedOnInsertList<OTETask> simulatedEnvNotifyTasks = new SortedOnInsertList<>(OTETask.class);
   
// +  private NavigableSet<OTETask> simulatedEnvNotifyTasks = new ConcurrentSkipListSet<OTETask>();
   private SortedLinkedList<OTETask> tasks = new SortedLinkedList<OTETask>();
   private SortedLinkedList<OTETask> simulatedEnvNotifyTasks = new SortedLinkedList<OTETask>();
 
   private ExecutorService pool;
   private boolean isTimeSimulated;
   private List<Future<OTETaskResult>> submittedTasks;
//   private ConcurrentLinkedQueue<OTETask> newTasks;
   private Thread mainTimer;
   
   Runnable busyDelay = () -> {  };
   Runnable yieldDelay = () -> { Thread.yield(); };
   Runnable sleepDelay = () -> { try{ Thread.sleep(0, 1); } catch (Throwable th){} };
   protected boolean noPause = false;
   protected boolean doTasksHaveAnyMainThreadWaits = false;
   private Thread mainThread;
   
   private SchedulerImpl wallClockScheduler;
   private volatile boolean ignoreWaits;
   
   /**
    * if time is simulated we wait for tasks to complete, if not fire and forget
    * 
    * @param isTimeSimulated
    */
   public SchedulerImpl(boolean isTimeSimulated, DelayStrategy delayStrategy){
      if(isTimeSimulated){
         clock = new OTEClockSimulated();
         wallClockScheduler = new SchedulerImpl(false, DelayStrategy.sleep);
         wallClockScheduler.start();
      } else {
         switch(delayStrategy){
         case busy:
            clock = new OTEClockReal(busyDelay);
            break;
         case sleep:
            clock = new OTEClockReal(sleepDelay);
            break;
         case yeild:
            clock = new OTEClockReal(yieldDelay);
            break;
         default:
            break;
         }
      }
      submittedTasks = new ArrayList<Future<OTETaskResult>>();
//      newTasks = new ConcurrentLinkedQueue<OTETask>();
      pool = Executors.newFixedThreadPool(10, new ThreadFactory() {
         private int count = 0;
         @Override
         public Thread newThread(Runnable r) {
            Thread th = new Thread(r);
            th.setDaemon(true);
            th.setName("OTEScheduler " + ++count);
            return th;
         }
      });
      this.isTimeSimulated = isTimeSimulated;
   }
   
   public void start(){
      if(!run){
         run = true;
         run();
      }
   }
   
   public void stop(){
      run = false; 
      if(wallClockScheduler != null){
         wallClockScheduler.stop();
      }
   }
   
   private void run(){
      if((mainTimer != null && !mainTimer.isAlive()) || mainTimer == null){
         mainTimer = new Thread(new Runnable(){
            @Override
            public void run() {
               while(run){
                  boolean paused = false;
                  if(isTimeSimulated) {
                     if(!noPause){
                        try{
                           lock.lock();
                           long time = clock.currentTimeMillis();
                           Iterator<OTETask> simIt = simulatedEnvNotifyTasks.iterator();
                           while(simIt.hasNext()){
                              OTETask task = simIt.next();
                              if(task.getTime() <= time){
                                 try{
                                    simIt.remove();
                                    task.call();
                                 } catch (Exception ex){
                                    ex.printStackTrace();
                                 }
                              } else {
                                 break;
                              }
                           }
                           if(simulatedEnvNotifyTasks.isEmpty()){
                              paused = true;
                           } 
                        } finally {
                           lock.unlock();
                        }
                     }
                  } 
                  if(paused){
                     SchedulerImpl.this.sleepDelay.run();
                  } else {
                     executeTasks();
                     clock.step();
                  }
               }
               System.out.println("exit scheduler");
            }

            
         });
         String name = "OTEScheduler MASTER";
         if(isTimeSimulated){
            name+="(sim)";
         }
         mainTimer.setName(name);
         mainTimer.start();   
      }
   }
   
   private void executeTasks() {
      long time = clock.currentTimeMillis();
      try{
         lock.lock();
         if(!tasks.isEmpty()){
            Iterator<OTETask> it = tasks.iterator();
            while(it.hasNext()){
               OTETask task = it.next();
               if(task == null){
                  System.out.println("what?");
               }
               if(task != null && task.getTime() <= time){
                  submittedTasks.add(pool.submit(task));
                  it.remove();
                  if(task.isMainThread()){
                     doTasksHaveAnyMainThreadWaits = false;
                  }
                  if(task.isScheduled()){
                     task.setNextTime(time + task.period());
//                     tasks.addToQueue(task);
                     tasks.add(task);
                  }               
               } else {
                  break;
               }
            }
//            tasks.flushAddQueue();
         }
      } finally {
         lock.unlock();
      }
      if(isTimeSimulated){
         for(Future<OTETaskResult> f:submittedTasks){
            try {
               f.get();    
            } catch (InterruptedException e) {
               e.printStackTrace();
            } catch (ExecutionException e) {
               e.printStackTrace();
            }
         }
         submittedTasks.clear();
      }
   }



   public long getTime() {
      return clock.currentTimeMillis();
   }
   
   public OTETaskRegistration scheduleAtFixedRate(Runnable runnable, double d){
      if(d > 1000.0){
         throw new IllegalArgumentException();
      }
      OTETaskRegistration reg;
      double periodMS = 1000.0/d;
      int period = (int)periodMS;
      OTETask task = new OTETaskHeavy(runnable, period);
      reg = new OTETaskRegistration(this, task);
      try{
         lock.lock();
         if(!tasks.add(task)){
            System.out.println("no no");
         }
      } finally {
         lock.unlock();
      }
      return reg;
   }
   
   public OTETaskRegistration scheduleWithDelay(Runnable runnable, long msInTheFuture){
      OTETaskRegistration reg;
//      synchronized (newTasks) {
         boolean mainThreadWait = false;
         if(isTimeSimulated){
            if(mainThread != null && Thread.currentThread().equals(mainThread)){
               mainThreadWait = true;
            }
//            msInTheFuture= msInTheFuture - 1;//this makes stuff slow?
         }
         OTETask task = new OTETask(runnable, getTime() + msInTheFuture);
         task.setMain(mainThreadWait);
         reg = new OTETaskRegistration(this, task);
         if(ignoreWaits){
            try {
               task.call();
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         } else {
         try{
            lock.lock();
            if(mainThreadWait && isTimeSimulated){
               simulatedEnvNotifyTasks.add(task);
            } else {
               tasks.add(task);
            }
         } finally {
            lock.unlock();
         }
         doTasksHaveAnyMainThreadWaits = true;
         }
//      }
      return reg;
   }
   
   
   public OTETaskRegistration scheduleWithDelayRealTime(Runnable runnable, long msInTheFuture){
      OTETaskRegistration reg;
      if(isTimeSimulated){
         reg = wallClockScheduler.scheduleWithDelay(runnable, msInTheFuture);
      } else {
         reg = scheduleWithDelay(runnable, msInTheFuture);
      }
      return reg;
   }
   
//   void setTimerFor(OteReentrantLock lock, OteCondition condition, int milliseconds){
//      addEnvEvent(new Timeout(lock, condition), milliseconds);
//   }
   
   
//   private void addEnvEvent(Timeout timeout, long milliseconds) {
//      
//      
//      
//   }
//
//
//   private static class Timeout implements Runnable {
//
//      private OteCondition condition;
//      private OteReentrantLock lock;
//      
//      public Timeout(OteReentrantLock lock, OteCondition condition) {
//         this.condition = condition;
//         this.lock = lock;
//      }
//      
//      @Override
//      public void run() {
//         try{
//            lock.lock();
//            condition.signalAll();
//         } finally {
//            lock.unlock();
//         }
//      }
//      
//   }
   
   public static void main(String[] args){
      final SchedulerImpl sch = new SchedulerImpl(false, DelayStrategy.sleep);
      sch.scheduleAtFixedRate(new Runnable(){
         long lastTimeRun = sch.getTime();
         @Override
         public void run() {
//            try {
//               Thread.sleep(20);
               long period = sch.getTime() - lastTimeRun;
               if(period > 21 || period < 19){
                  System.out.println(period);
                  System.out.println(sch.clock.tick);
               }
//               System.out.println(sch.getTime() - lastTimeRun);
               lastTimeRun = sch.getTime();
//            } catch (InterruptedException e) {
//               // TODO Auto-generated catch block
//               e.printStackTrace();
//            }            
         }
      }, 50.0);      
      long time = System.currentTimeMillis();
      sch.start();
      
      try {
         Thread.sleep(10000);
         System.out.println("warm up done");
         Thread.sleep(60000);
         sch.stop();
         long elapsed = System.currentTimeMillis() - time;
         System.out.println(elapsed);
//         System.out.println((double)sch.getTime()/(double)elapsed);
         
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println("done with main?");
   }

   boolean removeTask(OTETask task) {
//      System.out.println("remove " + getTime() + " " + task.toString());
      boolean removed = false;
      if(isTimeSimulated){
         removed = wallClockScheduler.removeTask(task);
         if(!removed){
            lock.lock();
            try{
            removed = simulatedEnvNotifyTasks.remove(task);
            } finally {
               lock.unlock();
            }
         }
      }
      if(!removed){
         lock.lock();
         try{
         removed = tasks.remove(task);
         } finally {
            lock.unlock();
         }
//         System.out.printf("Removed Task: %s\n", task.toString());
      }
//      if(!removed){
//         System.out.println("failed to remove :************************************************************" + task);   
//      }
//      System.out.println("removed task:************************************************************");
//      for(OTETask t:tasks){
//         System.out.println(t);
//      }
//      System.out.println("**************************************************************");
      return removed;
   }
   
   public void cancelAndIgnoreWaits(boolean ignoreWaits){
      this.ignoreWaits = ignoreWaits;
      if(isTimeSimulated){
         wallClockScheduler.cancelAndIgnoreWaits(ignoreWaits);
      }
      if(ignoreWaits){
         try{
            lock.lock();
            for(OTETask task:tasks){
               tasks.remove(task);
               if(task.period()>0){
                  task.setNextTime(getTime() + task.period());
                  tasks.add(task);
               } else {
                  if(!task.isComplete()){
                     try {
                        task.call();
                     } catch (Exception e) {
                        e.printStackTrace();
                     }
                  }
               }
            }     
         } finally {
            lock.unlock();
         }
      }
   }

   @Override
   public void envWait(long milliseconds) {
      if(ignoreWaits){
         return;
      }
      long time = getTime() + milliseconds;
      ReentrantLock lock = new ReentrantLock();
      Condition wakeUp = lock.newCondition();
      lock.lock();
      try{
         scheduleWithDelay(new SignalNotify(lock, wakeUp), milliseconds);
         while(getTime() < time){
            wakeUp.await(1000, TimeUnit.MILLISECONDS);
            if(Thread.interrupted() || ignoreWaits){
               break;
            }
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      } finally {
         lock.unlock();
      }
   }
   
   public void setMainThread(Thread thread){
      this.mainThread = thread;
//      if(mainThread != null){
//         clock.reset();
//      }
      try{
         lock.lock();
         for(OTETask task:tasks){
            tasks.remove(task);
            if(task.period()>0){
               task.setNextTime(getTime() + task.period());
               tasks.add(task);
            } else {
               task.cancel();
            }
         }     
         
         System.out.println("set main thread task:" + tasks.size() +"************************************************************");
         for(OTETask t:tasks){
            System.out.println(t);
         }
         System.out.println("**************************************************************");
         
      } finally {
         lock.unlock();
      }
   }
   
   public void resetClock(){
      clock.reset();
   }

}
