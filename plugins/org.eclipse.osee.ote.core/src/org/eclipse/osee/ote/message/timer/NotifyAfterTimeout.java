package org.eclipse.osee.ote.message.timer;

import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;

public class NotifyAfterTimeout implements Runnable {

   private ITimeout obj;

   public NotifyAfterTimeout(ITimeout objToNotify) {
      this.obj = objToNotify;
   }

   @Override
   public void run() {
      synchronized (obj) {
         obj.setTimeout(true);
         obj.notifyAll();
      }
   }

}
