package org.eclipse.ote.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SchedulerImpl implements Scheduler {

   public enum DelayStrategy{
      busy, yeild, sleep
   }
   
   private OTEClock clock;
   private volatile boolean run = false;
   
   private SortedLinkedListCopyOnWrite<OTETask> tasks = new SortedLinkedListCopyOnWrite<OTETask>();
   private SortedLinkedListCopyOnWrite<OTETask> simulatedEnvNotifyTasks = new SortedLinkedListCopyOnWrite<OTETask>();
   private ObjectPool<SignalNotify> signalNotifyPool;
 
   private ExecutorService pool;
   private boolean isTimeSimulated;
   private List<Future<OTETaskResult>> submittedTasks;
   private Thread mainTimer;
   
   private Runnable busyDelay = () -> {  };
   private Runnable yieldDelay = () -> { Thread.yield(); };
   private Runnable sleepDelay = () -> { try{ Thread.sleep(0, 1); } catch (Throwable th){} };
   private volatile boolean noPause = false;
   private Thread mainThread;
   
   private SchedulerImpl wallClockScheduler;
   private volatile boolean ignoreWaits;
   private volatile boolean pauseSimulated;
   
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
      
      signalNotifyPool = new ObjectPool<>(new Create<SignalNotify>() {
         @Override
         public SignalNotify create() {
            ReentrantLock lock = new ReentrantLock();
            Condition wakeUp = lock.newCondition();
            return new SignalNotify(lock, wakeUp);
         }
      }, new Initialize<SignalNotify>() {
         @Override
         public void initialize(SignalNotify obj) {
            
         }});
      
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
      pool.shutdown();
      tasks.dispose();
      simulatedEnvNotifyTasks.dispose();
   }
   
   void step(){
      boolean paused = false;
      if(isTimeSimulated) {
         synchronized (this) {
            if(pauseSimulated){
               paused = true;
            } else { 
               long time = clock.currentTimeMillis();
               MyInnerIterator<OTETask> simIt = simulatedEnvNotifyTasks.iterator();
               while(simIt.hasNext()){
                  OTETask task = simIt.next();
                  if(task != null && task.getTime() <= time){
                     try{
                        boolean removed = simulatedEnvNotifyTasks.remove(task);
                        if(!removed){
                           System.out.println("boo");
                        }
                        simulatedEnvNotifyTasks.print();
                        task.call();
                     } catch (Exception ex){
                        ex.printStackTrace();
                     }
                  } else {
                     break;
                  }
               }
               simulatedEnvNotifyTasks.doneWithIterator(simIt);
               if (!noPause) {
                  if(simulatedEnvNotifyTasks.isEmpty()){
                     paused = true;
                  }
               }
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
   
   private void run(){
      if((mainTimer != null && !mainTimer.isAlive()) || mainTimer == null){
         mainTimer = new Thread(new Runnable(){
            @Override
            public void run() {
               while(run){
                  step();
               }
               for(OTETask t:tasks){
                  System.out.println(t);
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
      if(!tasks.isEmpty()){
         MyInnerIterator<OTETask> it = tasks.iterator();
         while(it.hasNext()){
            OTETask task = it.next();
            if(task == null){
               System.out.println("what?");
            }
            if(task != null && task.getTime() <= time){
               submittedTasks.add(pool.submit(task));
               tasks.remove(task);
               if(task.isScheduled()){
                  task.setNextTime(time + task.period());
                  tasks.add(task);
               }               
            } else {
               break;
            }
         }
         tasks.doneWithIterator(it);
      }
      if(isTimeSimulated){
         for(int i = 0; i < submittedTasks.size(); i++){
            try {
               submittedTasks.get(i).get();    
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
      return scheduleAtFixedRate(runnable, d, 0);
   }
   
   public OTETaskRegistration scheduleAtFixedRate(Runnable runnable, double d, int weight){
      if(d > 1000.0){
         throw new IllegalArgumentException();
      }
      OTETaskRegistration reg;
      double periodMS = 1000.0/d;
      int period = (int)periodMS;
      OTETask task = new OTETaskHeavy(runnable, period, weight);
      reg = new OTETaskRegistration(this, task);
      if(!tasks.add(task)){
         throw new RuntimeException("Failed to add a scheduled task");
      }
      return reg;
   }
   
   /**
    * 
    * @param runnable
    * @param msInTheFuture
    * @param overrideEnvThread - set to true so if you are using this to schedule a one shot task from the test thread, which means that in simulated 
    * mode we will not wait for the testThread to catch up.
    * @return
    */
   public OTETaskRegistration scheduleWithDelay(Runnable runnable, long msInTheFuture, boolean overrideEnvThread){
      OTETaskRegistration reg;
      boolean mainThreadWait = false;
      if(isTimeSimulated && !overrideEnvThread){
         if(mainThread != null && Thread.currentThread().equals(mainThread)){
            mainThreadWait = true;
         }
      }
      OTETask task = new OTETask(runnable, getTime() + msInTheFuture, 0);
      task.setMain(mainThreadWait);
      reg = new OTETaskRegistration(this, task);
      if(ignoreWaits){
         try {
            task.call();
         } catch (Exception e) {
            e.printStackTrace();
         }
      } else {
         if(mainThreadWait && isTimeSimulated){
            simulatedEnvNotifyTasks.add(task);
         } else {
            tasks.add(task);
         }
      }
      return reg;
   }
   
   public OTETaskRegistration scheduleWithDelay(Runnable runnable, long msInTheFuture){
      return scheduleWithDelay(runnable, msInTheFuture, false);
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
   
   public static void main(String[] args){
      final SchedulerImpl sch = new SchedulerImpl(false, DelayStrategy.sleep);
      sch.scheduleAtFixedRate(new Runnable(){
         long lastTimeRun = sch.getTime();
         @Override
         public void run() {
            long period = sch.getTime() - lastTimeRun;
            if(period > 21 || period < 19){
               System.out.println(period);
               System.out.println(sch.clock.tick);
            }
            lastTimeRun = sch.getTime();
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

      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      System.out.println("done with main?");
   }

   boolean removeTask(OTETask task) {
      boolean removed = false;
      if(isTimeSimulated){
         removed = wallClockScheduler.removeTask(task);
         if(!removed){
            removed = simulatedEnvNotifyTasks.remove(task);
         }
      }
      if(!removed){
         removed = tasks.remove(task);
      }
      if(removed && task instanceof OTETaskHeavy){
         System.out.println(task.toString());
      }
      return removed;
   }
   
   public void cancelAndIgnoreWaits(boolean ignoreWaits){
      this.ignoreWaits = ignoreWaits;
      if(isTimeSimulated){
         wallClockScheduler.cancelAndIgnoreWaits(ignoreWaits);
      }
      if(ignoreWaits){
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
      }
   }

   @Override
   public void envWait(long milliseconds) {
      if(ignoreWaits){
         return;
      }
      long time = getTime() + milliseconds;
      SignalNotify signalNotify = signalNotifyPool.get();
      signalNotify.getLock().lock();
      try{
         scheduleWithDelay(signalNotify, milliseconds);
         while(getTime() < time){
            signalNotify.getCondition().await(1000, TimeUnit.MILLISECONDS);
            if(Thread.interrupted() || ignoreWaits){
               break;
            }
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      } finally {
         signalNotify.getLock().unlock();
         signalNotifyPool.push(signalNotify);
      }
   }
   
   public void setMainThread(Thread thread){
      this.mainThread = thread;
      for(OTETask task:tasks){
         tasks.remove(task);
         if(task.period()>0){
            task.setNextTime(getTime() + task.period());
            tasks.add(task);
         } else {
            task.cancel();
         }
      }     
   }
   
   public void resetClock(){
      clock.reset();
   }

   @Override
   public synchronized void pauseSimulated(boolean pause) {
      this.pauseSimulated = pause;
   }
   
   /**
    * Call this function to not do any pause of execution in the simulated scheduled.  In general if there is no wait in the system then the system will not continue to 
    * run tasks. 
    * @param pause
    */
   public void setNoPause(boolean noPause){
      this.noPause = noPause;
   }
   
}
