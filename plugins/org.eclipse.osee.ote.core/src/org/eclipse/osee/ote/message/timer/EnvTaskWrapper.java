package org.eclipse.osee.ote.message.timer;

import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;

public class EnvTaskWrapper implements Runnable {

   private EnvironmentTask envTask;

   public EnvTaskWrapper(EnvironmentTask envTask) {
      this.envTask = envTask;
   }

   @Override
   public void run() {
      try {
         envTask.runOneCycle();
      } catch (TestException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
   
   public String toString(){
      return String.format("%s", envTask.toString());
   }

}
