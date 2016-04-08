package org.eclipse.ote.scheduler;

public class OTETaskHeavy extends OTETask {

   private OTETaskResult result;
   private Stopwatch stopwatch;
   private Stopwatch[] stopwatches;
   private int count = 1;
   
   public OTETaskHeavy(Runnable runnable, int period){
      super(runnable, period);
      result = new OTETaskResult();
      if(period == 6 ){
         stopwatches = new Stopwatch[5];
         for(int i = 0; i < stopwatches.length; i++){
            stopwatches[i] = new Stopwatch(runnable.getClass().getSimpleName()+"_"+i);
         }
      }
      stopwatch = new Stopwatch(runnable.getClass().getSimpleName());
   }
   
   public OTETaskHeavy(Runnable runnable, long time) {
      super(runnable, time);
      result = new OTETaskResult();
      stopwatch = new Stopwatch(runnable.toString());
   }

   @Override
   public OTETaskResult call() throws Exception {
      Stopwatch fromArray = null;
      if(stopwatches != null){
         fromArray = stopwatches[count % 5];
         fromArray.start();
      }
      stopwatch.start();
      super.call();
      stopwatch.stop();
      result.elapsedTime = stopwatch.getLastElapsedTime();
      if(fromArray != null){
         fromArray.stop();
      }
      count++;
      return result;
   }
   
   public String toString(){
      if(stopwatches != null){
         StringBuilder sb = new StringBuilder();
         for(int i = 0; i < stopwatches.length; i++){
            sb.append(String.format("%s %s\n", super.toString(), stopwatches[i].toString()));
         }
         return sb.toString();
      }
      return String.format("%s %s", super.toString(), stopwatch.toString());
   }

}
