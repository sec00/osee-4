package org.eclipse.ote.scheduler;

public interface Scheduler {

   public void start();
   
   public void stop();
   

   public long getTime();
   
   public OTETaskRegistration scheduleAtFixedRate(Runnable runnable, double d);
   /**
    * 
    * @param runnable
    * @param mainThread - set this to true if this is the primary thread of execution.  In simulated
    * mode this has special behavior where all other threads will only run when this thread has a wait
    * it is essentially the thread that the simulated clock is locked to.  
    * @param msInTheFuture
    * @return
    */
   public OTETaskRegistration scheduleWithDelay(Runnable runnable, boolean mainThread, long msInTheFuture);

   public void envWait(long milliseconds);
   
   public void setMainThread(Thread thread);
   
}
