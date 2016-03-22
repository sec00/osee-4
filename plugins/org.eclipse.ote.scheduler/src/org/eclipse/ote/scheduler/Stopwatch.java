/*
 * Created on Aug 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.ote.scheduler;

/**
 * @author b1528444
 *
 */
public class Stopwatch {

   private final String name;
   private long startTime;
   private long elapsedTime;
   
   private long numberOfIterations = 0;
   private long mean;
   private long stdDev;
   private long min = Long.MAX_VALUE;
   private long max = Long.MIN_VALUE;
   
   public Stopwatch(String name){
      this.name = name;
   }

   public void start(){
      startTime = System.nanoTime();
   }

   public void stop(){
      elapsedTime = System.nanoTime() - startTime;
      long previousMean = mean;
      numberOfIterations++;
      mean = mean + (elapsedTime-mean)/numberOfIterations;
      if(elapsedTime < min){
         min = elapsedTime;
      }
      if(elapsedTime > max){
         max = elapsedTime;
      }
      if(numberOfIterations > 1){
         stdDev = stdDev + (elapsedTime-mean)*(elapsedTime-previousMean);
      }
   }

   public double getStdDev(){
      if(numberOfIterations > 0){
         return Math.sqrt(stdDev/numberOfIterations);
      } else {
         return 0.0;
      }
   }
   
   public String toString(){
      return String.format("%s[%d] mean[%dns] stdDev[%fns] min[%dns] max[%dns]", name, numberOfIterations, this.mean, getStdDev(), min, max);
   }

   public long getLastElapsedTime() {
      return elapsedTime;
   }

}
