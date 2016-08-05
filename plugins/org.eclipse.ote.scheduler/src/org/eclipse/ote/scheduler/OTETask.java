package org.eclipse.ote.scheduler;

import java.util.concurrent.Callable;

public class OTETask implements Callable<OTETaskResult>, Comparable<OTETask>{

   private long time = 0;
   private Runnable r;
   private int period;
   private final boolean isScheduled;
   private volatile boolean complete = false;
   private volatile boolean canceled = false;
   private boolean isMainThread = false;
   private int weight = 0;
   
   public OTETask(Runnable runnable, int period, int weight){
      isScheduled = true;
      this.r = runnable;
      this.period = period;
      this.weight  = weight;
   }
   
   public OTETask(Runnable runnable, long time, int weight) {
      isScheduled = false;
      this.time = time;
      this.r = runnable;
      this.period = 0;
      this.weight  = weight;
   }

   public long getTime() {
      return time;
   }

   @Override
   public OTETaskResult call() throws Exception {
      try{
         if(!canceled){
//            System.out.println(r.toString());
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
            int weightDelta = weight - o.weight;
            if(weightDelta > 0){
               return 1;
            } else if (weightDelta < 0){
               return -1;
            } else {
               return hashCode() - o.hashCode();
            }
         }
      }
   }

   void setCanceled() {
      canceled = true;
      complete = true;
   }
   
   public String toString(){
      return String.format("%015d %12d %6d %s", hashCode(), this.time, period, r.toString());
   }

   public void setMain(boolean isMainThread) {
      this.isMainThread = isMainThread;
   }
   
   public boolean isMainThread(){
      return isMainThread;
   }
}
