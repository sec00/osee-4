package org.eclipse.ote.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class OteReentrantLock implements Lock {

   /**
    * 
    */
//   private static final long serialVersionUID = -7698141028909085955L;

   public OteReentrantLock(Scheduler scheduler){
      super();
   }

   @Override
   public Condition newCondition() {
      return null;
   }

   @Override
   public void lock() {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void lockInterruptibly() throws InterruptedException {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean tryLock() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void unlock() {
      // TODO Auto-generated method stub
      
   }
   

   
}
