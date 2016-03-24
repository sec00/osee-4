package org.eclipse.ote.scheduler;

import java.util.concurrent.Callable;

public class OTETask implements Callable<OTETaskResult>, Comparable<OTETask>{

   private long time = 0;
   private Runnable r;
   private int period;
   private final boolean isScheduled;
   private volatile boolean complete = false;
   private boolean canceled;
   private boolean isMainThread = false;
   
   public OTETask(Runnable runnable, int period){
      isScheduled = true;
      this.r = runnable;
      this.period = period;
   }
   
   public OTETask(Runnable runnable, long time) {
      isScheduled = false;
      this.time = time;
      this.r = runnable;
      this.period = 0;
   }

   public long getTime() {
      return time;
   }

   @Override
   public OTETaskResult call() throws Exception {
      try{
         if(!canceled){
            r.run();
         }
      } catch (Throwable th){
         th.printStackTrace();
      } 
      complete  = true;
      return null;
   }
   
   public boolean isComplete(){
      return complete;
   }
   
   public boolean isScheduled() {
      return isScheduled;
   }

   public long period() {
      return period;
   }
   
   public void cancel(){
      canceled = true;
   }

   public void setNextTime(long l) {
      time = l;      
   }

   @Override
   public int compareTo(OTETask o) {
      if(equals(o)){
         return 0;
      }
      long delta = getTime() - o.getTime();
      if(delta > 0){
         return 1;
      } else if (delta < 0){
         return -1;
      } else {
         long periodDelta = period() - o.period();
         if(periodDelta > 0){
            return 1;
         } else if (periodDelta < 0){
            return -1;
         } else {
            return hashCode() - o.hashCode();
         }
      }
   }

   void setCanceled() {
      canceled = true;
   }
   
   public String toString(){
      return String.format("%d %d %s", this.time, period, r.toString());
   }

   public void setMain(boolean isMainThread) {
      this.isMainThread = isMainThread;
   }
   
   public boolean isMainThread(){
      return isMainThread;
   }
}
