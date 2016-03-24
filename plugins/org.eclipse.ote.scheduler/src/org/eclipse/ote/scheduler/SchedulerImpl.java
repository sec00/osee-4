package org.eclipse.ote.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SchedulerImpl implements Scheduler {

   private ReentrantLock lock = new ReentrantLock();
   
   public enum DelayStrategy{
      busy, yeild, sleep
   }
   
   private OTEClock clock;
   private volatile boolean run = false;
   
   private NavigableSet<OTETask> tasks = new ConcurrentSkipListSet<OTETask>();
   private NavigableSet<OTETask> simulatedEnvNotifyTasks = new ConcurrentSkipListSet<OTETask>();
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
//               int pauseCount = 0;
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
                           if(simulatedEnvNotifyTasks.size() == 0){
                              paused = true;
                           } 
//                           if(!tasks.isEmpty() ){
//                              paused = true;
//                              for(OTETask t:tasks){//if there is a wait from the main thread then we execute tasks otherwise the rest of the world does not move, we're waiting for the main thread
//                                 if(t.isMainThread()){
//                                    paused = false;
//                                    break;
//                                 }
//                              }
//                           }
                        } finally {
                           lock.unlock();
                        }
                     }
                  } 
                  if(paused){
//                     pauseCount++;
//                     if(pauseCount < 1000000){
//                        SchedulerImpl.this.busyDelay.run();
//                     } else
//                     if (pauseCount < 1000000){
//                        SchedulerImpl.this.yieldDelay.run();   
//                     } else {
//                        long time = System.nanoTime();
                        SchedulerImpl.this.sleepDelay.run();
//                        System.out.println("time:" + ( System.nanoTime() - time));
//                     }
//                     synchronized (newTasks) {
//                        tasks.addAll(newTasks);
//                        newTasks.clear();
//                     }
                  } else {
//                     if(pauseCount > 1){
//                     System.out.println(pauseCount);
//                     }
//                     pauseCount = 0;
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
            if(isTimeSimulated){
               
            }

            
            Iterator<OTETask> it = tasks.iterator();
            while(it.hasNext()){
               OTETask task = it.next();
               if(task.getTime() <= time){
                  submittedTasks.add(pool.submit(task));
                  //               System.out.printf("%d %s\n", time, task);
                  it.remove();
                  if(task.isMainThread()){
                     doTasksHaveAnyMainThreadWaits = false;
                  }
                  if(task.isScheduled()){
                     task.setNextTime(time + task.period());
                     tasks.add(task);
                     //                  System.out.println("readding " + task);
                     //                  synchronized (newTasks) {
                     //                     newTasks.add(task);
                     //                  }
                  }               
               } else {
                  break;
               }
            }

         }
      } finally {
         lock.unlock();
      }
      if(isTimeSimulated){
         for(Future<OTETaskResult> f:submittedTasks){
            try {
               //               OTETaskResult result = 
               f.get();    
               //     System.out.println(result.elapsedTime);
            } catch (InterruptedException e) {
               e.printStackTrace();
            } catch (ExecutionException e) {
               e.printStackTrace();
            }
         }
         submittedTasks.clear();
      }
//      synchronized (newTasks) {
//         tasks.addAll(newTasks);
//         newTasks.clear();
//      }
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
//      synchronized (newTasks) {
         OTETask task = new OTETaskHeavy(runnable, period);
         reg = new OTETaskRegistration(this, task);
//         System.out.println("new task " + task);
         try{
            lock.lock();
         if(!tasks.add(task)){
            System.out.println("no no");
         }
         } finally {
            lock.unlock();
         }
         
//      }
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

   void removeTask(OTETask task) {
//      System.out.println("remove " + getTime() + " " + task.toString());
      boolean removed = false;
      if(isTimeSimulated){
         removed = simulatedEnvNotifyTasks.remove(task);
      }
      if(!removed){
         removed = tasks.remove(task);
//         System.out.printf("Removed Task: %s\n", task.toString());
      }
      if(!removed){
         System.out.println("failed to remove :************************************************************" + task);   
      }
//      System.out.println("removed task:************************************************************");
//      for(OTETask t:tasks){
//         System.out.println(t);
//      }
//      System.out.println("**************************************************************");
   }

   @Override
   public void envWait(long milliseconds) {
      long time = getTime() + milliseconds;
      ReentrantLock lock = new ReentrantLock();
      Condition wakeUp = lock.newCondition();
      lock.lock();
      try{
         scheduleWithDelay(new SignalNotify(lock, wakeUp), milliseconds);
         while(getTime() < time){
            wakeUp.await(1000, TimeUnit.MILLISECONDS);
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
         for(OTETask task:tasks.toArray(new OTETask[tasks.size()])){
            tasks.remove(task);
            if(task.period()>0){
               task.setNextTime(getTime() + task.period());
               tasks.add(task);
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
