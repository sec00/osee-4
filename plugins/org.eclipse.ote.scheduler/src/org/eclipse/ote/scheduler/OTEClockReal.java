package org.eclipse.ote.scheduler;

public class OTEClockReal extends OTEClock {

   private long lastStepTime = System.nanoTime();
   private static final long step = 1000000; 
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
      while(System.nanoTime() - lastStepTime < step){
         delay.run();
      }
      lastStepTime = System.nanoTime();
   }

}
