/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.mock;

import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.core.testPoint.Operation;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;
import org.eclipse.ote.scheduler.OTETaskRegistration;
import org.eclipse.ote.scheduler.SchedulerImpl;
import org.junit.Assert;

public class UnitTestSupport {

   private final UnitTestAccessor accessor;
   private final Random rand = new Random();
   
//   private StringBuilder debug = new StringBuilder();

   public void printDebug(){
//      System.out.println(debug.toString());
//      debug.delete(0, debug.length());
   }
   
   public UnitTestSupport() {
         accessor = new UnitTestAccessor();
   }

   public void activateMsg(final TestMessage msg) {
      
      accessor.addTask(new EnvironmentTask(msg.getRate(), 0) {

         @Override
         public void runOneCycle() throws InterruptedException, TestException {
            accessor.getMsgManager().publish(msg);
         }

      });
   }

   public void cleanup() {
      accessor.shutdown();
   }

   public <T extends Comparable<T>> void setAfter(final DiscreteElement<T> element, final T value, int millis) {
      accessor.getTimerCtrl().getScheduler().scheduleWithDelay(new Runnable() {
         
         @Override
         public void run() {
//            printTime();
            element.setValue(value);
            element.getMessage().send();
//            printTime();
//            debug.append(String.format("%s,%d\n", element.toString(), accessor.getTimerCtrl().getScheduler().getTime()));
//            System.out.print("poop");
//            System.out.printf("%s,%d\n", element.toString(), accessor.getTimerCtrl().getScheduler().getTime());
         }
      }, millis, true);
      
   }

   public <T extends Comparable<T>> void maintain(final DiscreteElement<T> element, final T value, final T postValue, int millis) {
      element.setValue(value);
      element.getMessage().send();
//      debug.append(String.format("%s,%d\n", element.toString(), accessor.getTimerCtrl().getScheduler().getTime()));
  accessor.getTimerCtrl().getScheduler().scheduleWithDelay(new Runnable() {
         
         @Override
         public void run() {
            element.setValue(postValue);
            element.getMessage().send();
//            debug.append(String.format("%s,%d\n", element.toString(), accessor.getTimerCtrl().getScheduler().getTime()));
//            System.out.printf("%s,%d\n", element.toString(), accessor.getTimerCtrl().getScheduler().getTime());
         }
      }, millis);
      
   }

   public <T extends Comparable<T>> ISequenceHandle setSequence(final DiscreteElement<T> element, final T[] sequence) throws InterruptedException {
      final SequenceHandle seqHandle = new SequenceHandle();
      element.setValue(sequence[0]);
//      element.getMessage().send();
      
      IOSEEMessageListener listener = new IOSEEMessageListener() {
         int index = 1;

         @Override
         public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
            if (index < sequence.length) {
               element.setValue(sequence[index]);
//               element.getMessage().send();
               System.out.println(element.getName() + " is now " + element.getValue() + " - " + index + " " + UnitTestSupport.this.accessor.getTimerCtrl().getScheduler().getTime());
               index++;
            } else {
               element.getMessage().removeListener(this);
               seqHandle.signalEndSequence();
            }
         }

         @Override
         public void onInitListener() throws MessageSystemException {

         }

      };

      //  wait for a transmission so that the sequence begins on transimssion edges
      element.getMessage().waitForTransmission(accessor);
      element.getMessage().addListener(listener);
      return seqHandle;
   }

   public <T extends Comparable<T>> OTETaskRegistration maintainRandomizedList(final DiscreteElement<T> element, final T[] values, double rate) {
      element.setValue(values[0]);
      element.getMessage().send();
//      System.out.printf("%s,%d\n", values[0], accessor.getTimerCtrl().getScheduler().getTime());
//      debug.append(String.format("%s,%d\n", values[0].toString(), accessor.getTimerCtrl().getScheduler().getTime()));
      return accessor.getTimerCtrl().getScheduler().scheduleAtFixedRate(new Runnable() {
         
         @Override
         public void run() {
            element.setValue(selectRandom(values));
            element.getMessage().send();
//            debug.append(String.format("%s,%d\n", element.toString(), accessor.getTimerCtrl().getScheduler().getTime()));
//            System.out.printf("%s,%d\n", element.toString(), accessor.getTimerCtrl().getScheduler().getTime());
         }
      }, rate);
   }


   public <T extends Comparable<T>> void checkRange(final DiscreteElement<T> element, final T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      Assert.assertTrue(element.getName() + ".checkRange()->failed",
         element.checkRange(accessor, null, minValue, minInclusive, maxValue, maxInclusive, millis));
   }

   public <T extends Comparable<T>> void checkMaintainRange(final DiscreteElement<T> element, final T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      CheckGroup chkGrp = new CheckGroup(Operation.AND, "checkMaintainRangeGrp");
      T val = element.checkMaintainRange(accessor, chkGrp, minValue, minInclusive, maxValue, maxInclusive, millis);
      Assert.assertTrue(element.getName() + ".checkMaintainRange()->failed, value=" + val.toString(), chkGrp.isPass());
   }

   public <T extends Comparable<T>> void checkMaintainRangeFail(final DiscreteElement<T> element, final T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      CheckGroup chkGrp = new CheckGroup(Operation.AND, "checkMaintainRangeFailGrp");
      T val = element.checkMaintainRange(accessor, chkGrp, minValue, minInclusive, maxValue, maxInclusive, millis);
      Assert.assertFalse(element.getName() + ".checkMaintainRangeFail()->failed, value=" + val.toString(),
         chkGrp.isPass());
   }

   public <T extends Comparable<T>> void checkMaintainNotRange(final DiscreteElement<T> element, final T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      CheckGroup chkGrp = new CheckGroup(Operation.AND, "checkMaintainRangeGrp");
      T val = element.checkMaintainNotRange(accessor, chkGrp, minValue, minInclusive, maxValue, maxInclusive, millis);
      Assert.assertTrue(element.getName() + ".checkMaintainNotRange()->failed, value=" + val.toString(),
         chkGrp.isPass());
   }

   public <T extends Comparable<T>> void checkMaintainNotRangeFail(final DiscreteElement<T> element, final T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      CheckGroup chkGrp = new CheckGroup(Operation.AND, "checkMaintainRangeGrp");
      T val = element.checkMaintainNotRange(accessor, chkGrp, minValue, minInclusive, maxValue, maxInclusive, millis);
      Assert.assertFalse(element.getName() + ".checkMaintainNotRangeFail()->failed, value=" + val.toString(),
         chkGrp.isPass());
   }

   public <T extends Comparable<T>> void checkRangeFail(final DiscreteElement<T> element, final T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      Assert.assertFalse(element.getName() + ".checkRangeFail()->failed",
         element.checkRange(accessor, null, minValue, minInclusive, maxValue, maxInclusive, millis));
   }

   public <T extends Comparable<T>> void checkMaintain(DiscreteElement<T> element, T value, int millis) throws InterruptedException {
      T result = element.checkMaintain(accessor, null, value, millis);
      Assert.assertEquals(element.getName() + ".checkMaintain()->failed", value, result);
   }

   public <T extends Comparable<T>> void checkMaintainFail(DiscreteElement<T> element, T maintainValue, T discontinuity, int millis) {
      try {
         T result = element.checkMaintain(accessor, null, maintainValue, millis);
         Assert.assertEquals(element.getName() + ".checkMaintainFail()->failed", discontinuity, result);
      } catch (InterruptedException e) {
         Assert.fail("Exception");
      }
   }

   public <T extends Comparable<T>> void checkNot(DiscreteElement<T> element, T value, int millis) {
      CheckGroup grp = new CheckGroup(Operation.AND, "checkNotCheckGroup");
      try {
         boolean c = element.checkNot(accessor, grp, value, millis);
         CheckPoint cp = (CheckPoint) grp.getTestPoints().get(0);
         long elapsedTime = cp.getElpasedTime();
         Assert.assertTrue(element.getName() + String.format(".checkNot()->failed, elapsed time=%d", elapsedTime), c);
         System.out.printf("checkNot->passed, actual %s, expected %s, elapsed=%d\n", cp.getActual(), cp.getExpected(),
            cp.getElpasedTime());
      } catch (InterruptedException e) {
         Assert.fail("Exception");
      }
   }

   public <T extends Comparable<T>> void checkNotFail(DiscreteElement<T> element, T value, int millis) {
      try {
         Assert.assertFalse(element.getName() + " .checkNotFail()->failed",
            element.checkNot(accessor, null, value, millis));
      } catch (InterruptedException e) {
         Assert.fail("Exception");
      }
   }

   public <T extends Comparable<T>> void check(DiscreteElement<T> element, T value) throws InterruptedException {
      CheckGroup grp = new CheckGroup(Operation.AND, "checkCheckGrp");
      boolean c = element.check(accessor, grp, value, 500);
      long time = ((CheckPoint) grp.getTestPoints().get(0)).getElpasedTime();
      Assert.assertTrue(
         element.getName() + String.format(".check()->failed, elapsed=%d, expect=<%s>, actual=<%s>", time, value,
            element.getValue()), c);
   }

   public <T extends Comparable<T>> void checkWaitForValue(DiscreteElement<T> element, T value, int millis) throws InterruptedException {
      T result = element.waitForValue(accessor, value, millis);
      Assert.assertEquals(element.getName() + " .checkWaitForValue()->failed", value, result);
   }

   public <T extends Comparable<T>> void checkList(DiscreteElement<T> element, T[] values, int millis) throws InterruptedException {
      Assert.assertTrue(element.getName() + " .checkList()->failed", element.checkInList(accessor, values, millis));
   }

   public <T extends Comparable<T>> void checkNotInList(DiscreteElement<T> element, T[] values, int millis) throws InterruptedException {
      Assert.assertTrue(element.getName() + " .checkNotInList()->failed",
         element.checkNotInList(accessor, values, millis));
   }

   public <T extends Comparable<T>> void checkNotInListFail(DiscreteElement<T> element, T[] values, int millis) throws InterruptedException {
      CheckGroup chkGrp = new CheckGroup(Operation.AND, "checkNotInListFail");
      element.checkNotInList(accessor, chkGrp, values, millis);
      Assert.assertFalse(element.getName() + " .checkNotInListFail()->failed", chkGrp.isPass());
   }

   public <T extends Comparable<T>> void checkListFail(DiscreteElement<T> element, T[] values, int millis) throws InterruptedException {
      CheckGroup chkGrp = new CheckGroup(Operation.AND, "checkListFail");
      boolean b = element.checkInList(accessor, chkGrp, values, millis);
      CheckPoint cp = (CheckPoint) ((CheckGroup) chkGrp.getTestPoints().get(0)).getTestPoints().get(0);
      Assert.assertFalse(
         element.getName() + String.format(" .checkListFail()->failed, elapsed time=%d", cp.getElpasedTime()), b);
   }

   public <T extends Comparable<T>> void checkMaintainList(DiscreteElement<T> element, T[] values, int millis) throws InterruptedException {
      CheckGroup chkGrp = new CheckGroup(Operation.AND, "checkMaintianList");
      T val = element.checkMaintainList(accessor, chkGrp, values, true, millis);
      Assert.assertTrue(element.getName() + " .checkMaintainList()->failed, value=" + val.toString(), chkGrp.isPass());
   }

   public <T extends Comparable<T>> void checkMaintainListFail(DiscreteElement<T> element, T[] values, int millis) throws InterruptedException {
      CheckGroup chkGrp = new CheckGroup(Operation.AND, "checkMaintainListFail");
      T val = element.checkMaintainList(accessor, chkGrp, values, true, millis);
      Assert.assertFalse(element.getName() + " .checkMaintainListFail()->failed, value=" + val.toString(),
         chkGrp.isPass());
//      printTime();
   }

   public <T extends Comparable<T>> void checkWaitForValueFail(DiscreteElement<T> element, T value, int millis) throws InterruptedException {
      T result = element.waitForValue(accessor, value, millis);
      boolean b = value.equals(result);
      Assert.assertFalse(
         element.getName() + String.format(" .checkWaitForValueFail()->failed, expect=%s, actual=%s", value, result), b);
   }

   public <T extends Comparable<T>> void checkPulse(DiscreteElement<T> element, T pulsedValue, T nonPulsedValue) throws InterruptedException {
      Assert.assertTrue(element.getName() + " .checkPulse()->failed",
         element.checkPulse(accessor, pulsedValue, nonPulsedValue));
   }

   public <T extends Comparable<T>> void checkPulseFail(DiscreteElement<T> element, T pulsedValue, T nonPulsedValue) throws InterruptedException {
      Assert.assertFalse(element.getName() + " .checkPulseFail()->failed",
         element.checkPulse(accessor, pulsedValue, nonPulsedValue));
   }

   public <T extends Comparable<T>> void genericTestCheckNot(DiscreteElement<T> elementReader,DiscreteElement<T> elementWriter, T[] values) throws InterruptedException {
      if (values.length < 2) {
         throw new IllegalArgumentException("array needs atleast two values");
      }
//      System.out.println("genericTestCheckNot()");
      // check pass conditions
      for (int i = values.length - 1; i >= 1; i--) {
         T notValue = values[i];
         T goodValue = values[i - 1];
         System.out.format("\tgoodValue=%s, notValue=%s\n", goodValue, notValue);
         elementWriter.setValue(notValue);
         elementWriter.getMessage().send();
         setAfter(elementWriter, goodValue, 100);
         checkNot(elementReader, notValue, 210);
         check(elementReader, goodValue); // make sure we did not pass until the goodValue was transmitted
      }

      // check fail conditions
      for (T value : values) {
         elementWriter.setValue(value);
         elementWriter.getMessage().send();
         checkNotFail(elementReader, value, 100);
      }
   }

   public <T extends Comparable<T>> void genericCheckMaintain(DiscreteElement<T> elementReader, DiscreteElement<T> elementWriter, T[] values) throws InterruptedException {
      if (values.length < 2) {
         throw new IllegalArgumentException("array needs atleast two values");
      }

      // check pass conditions
      for (T value : values) {
         elementWriter.setValue(value);
         elementWriter.getMessage().send();
         checkMaintain(elementReader, value, 200);
         check(elementReader, value); // make sure we pass for the right reasons e.q. no false positives
      }

      // check fail conditions
      for (int i = values.length - 1; i >= 1; i--) {
         T valueToMaintain = values[i];
         T badValue = values[i - 1];
         elementWriter.setValue(valueToMaintain);
         elementWriter.getMessage().send();
         setAfter(elementWriter, badValue, 100);
         checkMaintainFail(elementReader, valueToMaintain, badValue, 200); // make sure we fail as expected, no false negatives
      }
   }

   public <T extends Comparable<T>> void genericTestCheckWaitForValue(DiscreteElement<T> element, DiscreteElement<T> elementWriter, T[] values, T valueToFInd) throws InterruptedException {
      // check sequence
      element.getMessage().waitForTransmission(accessor);
      ISequenceHandle handle = setSequence(elementWriter, values);
      for (T v : values) {
         System.out.println("checking for value '" + v + "' " + UnitTestSupport.this.accessor.getEnvTime());
         checkWaitForValue(element, v, 60);
      }
      handle.waitForEndSequence(100, TimeUnit.MILLISECONDS);
      setSequence(elementWriter, values);
      checkWaitForValueFail(element, valueToFInd, 200);
   }

   public <T extends Comparable<T>> void genericTestCheckList(DiscreteElement<T> element, DiscreteElement<T> elementWriter, T[] goodValues, T[] badValues) throws InterruptedException {
      // check finding of every item in the list
      for (T v : goodValues) {
         elementWriter.setValue(selectRandom(badValues)); // value will always not be in the list until after 40 ms
         elementWriter.getMessage().send();
         setAfter(elementWriter, v, 40);
         checkList(element, goodValues, 100);
      }

      // check pass
      OTETaskRegistration reg = maintainRandomizedList(elementWriter, goodValues, 2.0);
      checkList(element, goodValues, 1500);
      reg.unregister();
      
      // check failure
      reg = maintainRandomizedList(elementWriter, badValues, 2.0);
      checkListFail(element, goodValues, 1500);
      reg.unregister();
   }

   public <T extends Comparable<T>> void genericTestCheckNotList(DiscreteElement<T> element, DiscreteElement<T> elementWriter, T[] allowedValues, T[] excludeValues) throws InterruptedException {
      // check transition from fail to pass within time period
      for (T v : excludeValues) {
         elementWriter.setValue(selectRandom(allowedValues)); // value will always not be in the list until after 40 ms
         setAfter(elementWriter, v, 40);
         checkNotInList(element, excludeValues, 100);
      }

      // check pass
      OTETaskRegistration handle = maintainRandomizedList(element, allowedValues, 10);
      try {
         checkNotInList(element, excludeValues, 10);
      } finally {
         try {
            handle.unregister();
         } catch (CancellationException ex) {
            // do nothing
//         } catch (ExecutionException ex) {
            throw new RuntimeException("exception while waiting for randomizedList to finish", ex);
         }
      }

      // check failure
      handle = maintainRandomizedList(elementWriter, excludeValues, 100);
      try {
         checkNotInListFail(element, excludeValues, 550);
      } finally {
         try {
            handle.unregister();
         } catch (CancellationException ex) {
            // do nothing
//         } catch (ExecutionException ex) {
            throw new RuntimeException("exception while waiting for randomizedList to finish", ex);
         }
      }
   }

   public <T extends Comparable<T>> void genericTestCheckMaintainList(DiscreteElement<T> element, DiscreteElement<T> elementWriter, T[] values, T badValue) throws InterruptedException {
      // visit each possible value in list, make sure checkMaintainList does not fail
      elementWriter.setValue(values[0]);
      elementWriter.getMessage().send();
      int timeStep = 40;
      for (T val : values) {
         setAfter(elementWriter, val, timeStep);
      }
      checkMaintainList(element, values, 1000);

      // random elements from list
      OTETaskRegistration handle = maintainRandomizedList(element, values, 66.0);
      try {
         checkMaintainList(element, values, 2000);
      } finally {
         try {
            handle.unregister();
         } catch (CancellationException ex) {
            // do nothing
//         } catch (ExecutionException ex) {
            throw new RuntimeException("exception while waiting for randomizedList to finish", ex);
         }
      }

      // check proper failure behavior
      handle = maintainRandomizedList(elementWriter, values, 10);
      try {
//         System.out.println("here we should fail");
//         printTime();
         setAfter(elementWriter, badValue, 1490);//make sure it doesn't align with publish or it may get clobbered
//         printTime();
         checkMaintainListFail(element, values, 2000);
//         printTime();
//         System.out.println("exit check list fail");
//         printTime();
//         System.out.println("do a wait");
//         printDebug();
         checkWaitForValue(element, badValue, 0);
      } finally {
//         printTime();
         try {
            handle.unregister();
         } catch (CancellationException ex) {
            // do nothing
//         } catch (ExecutionException ex) {
            throw new RuntimeException("exception while waiting for randomizedList to finish", ex);
         }
      }
   }

   public <T> T selectRandom(T[] list) {
      return list[Math.abs(rand.nextInt(list.length))];

   }

   public void checkForTransmission(TestMessage msg, int numXmits, int millis) throws InterruptedException {
      Assert.assertTrue(msg.getName() + " failed to transmit " + numXmits + " times in " + millis + "ms",
         msg.checkForTransmissions(accessor, numXmits, millis));
   }

   public void checkForTransmissionFail(TestMessage msg, int numXmits, int millis) throws InterruptedException {
      Assert.assertFalse(msg.getName() + " had at least " + numXmits + " transmissions",
         msg.checkForTransmissions(accessor, numXmits, millis));
   }

   public boolean inRange(int target, int tolerance, int actual) {
      return Math.abs(actual - target) <= tolerance;
   }

   public <T extends Message> T getMessageReader(Class<? extends Message> class1) {
      
      return (T)accessor.getMsgManager().createMessageRequestor("test").getMessageReader(class1);
   }
   
 public <T extends Message> T getMessageWriter(Class<? extends Message> class1) {
      
      return (T)accessor.getMsgManager().createMessageRequestor("test").getMessageWriter(class1);
   }

public void setMasterTestThread() {
   accessor.getTimerCtrl().getScheduler().cancelAndIgnoreWaits(true);
   accessor.getTimerCtrl().getScheduler().cancelAndIgnoreWaits(false);
   accessor.getTimerCtrl().getScheduler().resetClock();
   accessor.getTimerCtrl().getScheduler().setMainThread(Thread.currentThread());
}

public void testWait(int i) {
   accessor.getTimerCtrl().getScheduler().envWait(i);
}

public void printTime() {
   // TODO Auto-generated method stub
   System.out.println(accessor.getTimerCtrl().getScheduler().getTime());
}
}
