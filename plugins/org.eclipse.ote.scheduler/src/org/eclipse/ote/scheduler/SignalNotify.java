package org.eclipse.ote.scheduler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class SignalNotify implements Runnable {

   private ReentrantLock lock;
   private Condition wakeUp;

   public SignalNotify(ReentrantLock lock, Condition wakeUp) {
      this.lock = lock;
      this.wakeUp = wakeUp;
   }

   @Override
   public void run() {
      lock.lock();
      try{
         wakeUp.signal();
      } finally {
         lock.unlock();
      }
   }

}
