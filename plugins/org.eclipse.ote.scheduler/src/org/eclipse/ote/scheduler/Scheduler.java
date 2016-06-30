package org.eclipse.ote.scheduler;

public interface Scheduler {

   public void start();
   
   public void stop();
   

   public long getTime();
   
   public OTETaskRegistration scheduleAtFixedRate(Runnable runnable, double d);
   /**
    * 
    * @param runnable
    * @param msInTheFuture
    * @return
    */
   public OTETaskRegistration scheduleWithDelay(Runnable runnable, long msInTheFuture);
   public OTETaskRegistration scheduleWithDelay(Runnable runnable, long msInTheFuture, boolean overrideEnvThread);
   public OTETaskRegistration scheduleWithDelayRealTime(Runnable runnable, long msInTheFuture);

   public void envWait(long milliseconds);
   
   public void setMainThread(Thread thread);

   public void resetClock();

   public void cancelAndIgnoreWaits(boolean b);

   public void pauseSimulated(boolean b);
   
   /**
    * Call this function to not do any pause of execution in the simulated scheduled.  In general if there is no wait in the system then the system will not continue to 
    * run tasks. 
    * @param pause
    */
   public void setNoPause(boolean pause);
   
}
