package org.eclipse.ote.scheduler;

abstract class OTEClock {

//   ConcurrentSkipListSet<NotifyObject> notifiers = new ConcurrentSkipListSet<OTEClock.NotifyObject>();
   
   long startTime;
   long tick;
   
   //in ms
   long resolution = 1L;
   
   public void step(){
      tick++;
//      if(notifiers.isEmpty()){
//         return;
//      } else {
//         long currentTimeNanos = nanoTime();
//         Iterator<NotifyObject> it = notifiers.iterator();
//         while(it.hasNext()){
//            NotifyObject notify = it.next();
//            if(notify.targetTime - currentTimeNanos > 0){
//               break;//exit the loop
//            } else {
//               it.remove();
//               notify.obj.notifyAll();
//            }
//         }
//      }
   }
   
   public abstract long nanoTime();

   public abstract long currentTimeMillis();

   static final long spinForTimeoutThreshold = 1000L;
   
//   void lockSupportParkUntil(Object conditionObject, long abstime) {
//      lockSupportParkNanos(conditionObject, (abstime - currentTimeMillis()) * 1000000);
//   }
//
//   public void lockSupportParkNanos(Object conditionObject, long nanosTimeout) {
//      if(nanosTimeout <= 0){
//         return;
//      }
//      long timeToWaitNanos = nanosTimeout;
//      long targetTime = nanoTime() + nanosTimeout;
//      notifiers.add(new NotifyObject(targetTime, conditionObject));
//      while(timeToWaitNanos > 0){
//         try {
//            synchronized (conditionObject) {
//               conditionObject.wait();   
//               timeToWaitNanos = targetTime - nanoTime();
//            }
//         } catch (InterruptedException e) {
//            e.printStackTrace();
//         }
//      }
//   }
   
//   private static class NotifyObject implements Comparable<NotifyObject> {
//      Object obj;
//      long targetTime;
//
//      public NotifyObject(long targetTime, Object obj){
//         this.obj = obj;
//         this.targetTime = targetTime;
//      }
//      
//      @Override
//      public int compareTo(NotifyObject o) {
//         long diff = targetTime - o.targetTime;
//         if(diff > 0){
//            return 1;
//         } else if(diff == 0){
//            return 0;
//         } else {
//            return -1;
//         }
//      }
//   }
   

}
