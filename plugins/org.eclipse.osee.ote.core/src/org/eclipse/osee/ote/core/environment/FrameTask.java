package org.eclipse.osee.ote.core.environment;

import java.util.List;

import org.eclipse.osee.ote.core.TestException;

public class FrameTask extends EnvironmentTask {
   
   // 
   
   // run every 1 ms - determine what frameTasks are supposed to be run - and run the common stuff
   //
   //get messages - synchronized - will be contention between frames running at different rates
   // do concurrent sim stuff + do not concurrent stuff
   //send messages - synchronized - will be contention between frames running at different rates
   List<EnvironmentTask> tasks;
   
   
   
   public FrameTask(double hzRate) {
      super(hzRate);
   }

   @Override
   public void runOneCycle() throws InterruptedException, TestException {
      // TODO Auto-generated method stub
//      for(tasks){
//         if(task.shouldRun() && task.isConcurrent()){
//            waitFor.add(pool.submit(task));
//         }
//      }
//      for(tasks){
//         if(task.shouldRun() && !task.isConcurrent()){
//            task.run();
//         }
//      }
//      for(waitFor){
//         waitFor.get()//or some blocking call
//      }
//      checkTiming();
//      computeNextRunTime();
   }

}
