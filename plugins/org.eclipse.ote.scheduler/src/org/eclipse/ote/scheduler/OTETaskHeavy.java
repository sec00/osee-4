package org.eclipse.ote.scheduler;

public class OTETaskHeavy extends OTETask {

   private OTETaskResult result;
   private Stopwatch stopwatch;
   
   public OTETaskHeavy(Runnable runnable, int period){
      super(runnable, period);
      result = new OTETaskResult();
      stopwatch = new Stopwatch(runnable.toString());
   }
   
   public OTETaskHeavy(Runnable runnable, long time) {
      super(runnable, time);
      result = new OTETaskResult();
      stopwatch = new Stopwatch(runnable.toString());
   }

   @Override
   public OTETaskResult call() throws Exception {
      stopwatch.start();
      super.call();
      stopwatch.stop();
      result.elapsedTime = stopwatch.getLastElapsedTime();
      return result;
   }
   
   public String toString(){
      return String.format("%s %s", super.toString(), stopwatch.toString());
   }

}
