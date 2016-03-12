package org.eclipse.ote.scheduler;

public class OTEClockReal extends OTEClock {

   private long lastStepTime = System.nanoTime();
   private static final long step = 1000000; 
   private int averageCount;
   private Runnable delay;
   
   public OTEClockReal(Runnable delay){
      this.delay = delay;
   }
   
   public long nanoTime() {
      return System.nanoTime();
   }

   public long currentTimeMillis() {
      return System.currentTimeMillis();
   }
   
   public void step(){
      super.step();
      int count = 0;
      while(System.nanoTime() - lastStepTime < step){
         //busy loop
         count++;
         delay.run();
//         Thread.yield();
//         try {
//            Thread.sleep(0, 1);
//         } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//         }
      }
//      System.out.println(count);
      lastStepTime = System.nanoTime();
   }

}
