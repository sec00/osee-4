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
   
}
