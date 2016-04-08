package org.eclipse.ote.scheduler;

import org.eclipse.ote.scheduler.SchedulerImpl.DelayStrategy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class SchedulerImplTest {

   
   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testSchedulingBasics() {
      SchedulerImpl sch = new SchedulerImpl(true, DelayStrategy.sleep);
      
      Counter counter10 = new Counter();
      Counter counter20 = new Counter();
      Counter counter40 = new Counter();
      Counter counter80 = new Counter();
      Counter counterDelay = new Counter();
      sch.scheduleAtFixedRate(counter10, 100.0);
      sch.scheduleAtFixedRate(counter20, 50.0);
      sch.scheduleAtFixedRate(counter40, 25.0);
      sch.scheduleAtFixedRate(counter80, 12.5);
      sch.setMainThread(Thread.currentThread());
      sch.scheduleWithDelay(counterDelay, 1000);
//      sch.envWait(1000);
      sch.step();
      for(int i = 0; i < 10; i++){
         sch.step();
      }
     
       Assert.assertEquals(1, counter10.getCount());
      Assert.assertEquals(0, counter20.getCount());
      Assert.assertEquals(0, counter40.getCount());
      Assert.assertEquals(0, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(2, counter10.getCount());
      Assert.assertEquals(1, counter20.getCount());
      Assert.assertEquals(0, counter40.getCount());
      Assert.assertEquals(0, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(3, counter10.getCount());
      Assert.assertEquals(1, counter20.getCount());
      Assert.assertEquals(0, counter40.getCount());
      Assert.assertEquals(0, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(4, counter10.getCount());
      Assert.assertEquals(2, counter20.getCount());
      Assert.assertEquals(1, counter40.getCount());
      Assert.assertEquals(0, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(5, counter10.getCount());
      Assert.assertEquals(2, counter20.getCount());
      Assert.assertEquals(1, counter40.getCount());
      Assert.assertEquals(0, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(6, counter10.getCount());
      Assert.assertEquals(3, counter20.getCount());
      Assert.assertEquals(1, counter40.getCount());
      Assert.assertEquals(0, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(7, counter10.getCount());
      Assert.assertEquals(3, counter20.getCount());
      Assert.assertEquals(1, counter40.getCount());
      Assert.assertEquals(0, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(8, counter10.getCount());
      Assert.assertEquals(4, counter20.getCount());
      Assert.assertEquals(2, counter40.getCount());
      Assert.assertEquals(1, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(9, counter10.getCount());
      Assert.assertEquals(4, counter20.getCount());
      Assert.assertEquals(2, counter40.getCount());
      Assert.assertEquals(1, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(10, counter10.getCount());
      Assert.assertEquals(5, counter20.getCount());
      Assert.assertEquals(2, counter40.getCount());
      Assert.assertEquals(1, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(11, counter10.getCount());
      Assert.assertEquals(5, counter20.getCount());
      Assert.assertEquals(2, counter40.getCount());
      Assert.assertEquals(1, counter80.getCount());
      
      for(int i = 0; i < 10; i++){
         sch.step();
      }
      
      Assert.assertEquals(12, counter10.getCount());
      Assert.assertEquals(6, counter20.getCount());
      Assert.assertEquals(3, counter40.getCount());
      Assert.assertEquals(1, counter80.getCount());
      
   }
   
   private static class Counter implements Runnable {

      int count = 0;
      
      public int getCount(){
         return count;
      }
      
      @Override
      public void run() {
         count++;
      }
      
   }

}
