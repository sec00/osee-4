package org.eclipse.ote.scheduler;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class OteCondition implements Condition {

   private Condition condition;

   OteCondition(Condition condition){
      this.condition = condition;
   }
   
   @Override
   public final void await() throws InterruptedException {
      condition.await();
   }

   @Override
   public final void awaitUninterruptibly() {
      condition.awaitUninterruptibly();
   }

   @Override
   public final long awaitNanos(long nanosTimeout) throws InterruptedException {
      return condition.awaitNanos(nanosTimeout);
   }

   @Override
   public final boolean await(long time, TimeUnit unit) throws InterruptedException {
      return condition.await(time, unit);
   }

   @Override
   public final boolean awaitUntil(Date deadline) throws InterruptedException {
      return condition.awaitUntil(deadline);
   }

   @Override
   public final void signal() {
      condition.signal();
   }

   @Override
   public final void signalAll() {
      condition.signalAll();
   }
   

}
