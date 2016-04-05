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
package org.eclipse.osee.ote.message.listener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.WaitOnCondition;
import org.eclipse.osee.ote.message.condition.ICondition;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.MsgWaitResult;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IOSEEMessageReaderListener;
import org.eclipse.osee.ote.message.interfaces.IOSEEMessageWriterListener;
import org.eclipse.osee.ote.properties.OtePropertiesCore;
import org.eclipse.ote.scheduler.Scheduler;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class MessageSystemListener implements IOSEEMessageReaderListener, IOSEEMessageWriterListener, ITimeout {
   
   private static final Benchmark tbm = new Benchmark("Total Message System Listener", 2500);
   private static final boolean debugTime = OtePropertiesCore.timeDebug.getBooleanValue();
   private static final long debugTimeout = OtePropertiesCore.timeDebugTimeout.getLongValue();
   
   private static Scheduler scheduler;

   private volatile boolean disposed = false;
   private volatile boolean isTimedOut = false;

   private final CopyOnWriteNoIteratorList<IOSEEMessageListener> fastListeners = new CopyOnWriteNoIteratorList<IOSEEMessageListener>(IOSEEMessageListener.class);
   private final WeakReference<Message> message;

   private int masterMessageCount = 0;
   private int messageCount = 0;


   /**
    * This class takes in a message in the constructor so that it can tell the message to update when it recieves new
    * data.
    */
   public MessageSystemListener(Message msg) {
      super();
      this.message = new WeakReference<Message>(msg);
   }

   /**
    * Adds the listener as a "fast" listener.
    * 
    * @see MessageSystemListener#addListener(IOSEEMessageListener, SPEED)
    */
   private boolean addListener(IOSEEMessageListener listener) {
      return addListener(listener, SPEED.FAST);
   }

   /**
    * Registers a listener for the message. If the listener will not respond quickly (for example, if the listener is
    * going to make RMI calls, or other network activites which it will wait for the remote side to respond), then it
    * should identify itself as a slow listener by passing "false" for isFastListener. "Slow" listeners will be notified
    * by a separate thread, thereby not forcing other listener notifications to be delayed, and subsequent messages from
    * being processed.
    * 
    * @param listener - The listener to be added
    * @param listenerSpeed -
    * @return Returns boolean success indication.
    */
   private boolean addListener(IOSEEMessageListener listener, SPEED listenerSpeed) {
      if (!fastListeners.contains(listener)) {
         fastListeners.add(listener);
      }
      return true;
   }

   private void clearListeners() {
      this.fastListeners.clear();
   }

   /**
    * Convience method.
    * 
    * @return Returns presence boolean indication.
    * @see #containsListener(IOSEEMessageListener, SPEED)
    */
   private boolean containsListener(final IOSEEMessageListener listener) {
      return containsListener(listener, SPEED.FAST);

   }

   /**
    * Checks to see if the specified listener is registered
    * 
    * @return true if the listener is register false otherwise
    */
   private boolean containsListener(final IOSEEMessageListener listener, final SPEED listenerSpeed) {
      return fastListeners.contains(listener);
   }
   
   public void dispose() {
      this.disposed = true;
      this.clearListeners();
   }

   /**
    * returns the number of received messages since the last call to waitForData
    */
   private synchronized int getLocalMessageCount() {
      return messageCount;
   }

   private synchronized int getMasterMessageCount() {
      return masterMessageCount;
   }

   private Collection<IOSEEMessageListener> getRegisteredFastListeners() {
      return fastListeners.fillCollection(new ArrayList<IOSEEMessageListener>(fastListeners.length()));
   }

   /**
    * return whether new data has been received since the last call to waitForData
    */
   @Override
   public boolean isTimedOut() {
      return this.isTimedOut;
   }

   @Override
   public synchronized void onDataAvailable(final MessageData data, DataType type) throws MessageSystemException {
      if(disposed) return;
      tbm.startSample();
      if (message.get().getMemType() == type) {
         messageCount++;
         masterMessageCount++;
         notifyAll();
      }

      long start = 0, elapsed;
      IOSEEMessageListener[] ref = fastListeners.get();
      for (int i = 0; i < ref.length; i++) {
         IOSEEMessageListener listener = ref[i];
         if(debugTime){
            start = System.nanoTime();
         }
         listener.onDataAvailable(data, type);
         if(debugTime){
            elapsed = System.nanoTime() - start;
            if(elapsed > debugTimeout){
               Locale.setDefault(Locale.US);
               System.out.printf("%s %s SLOW %,d\n", message.get().getName(), listener.getClass().getName(), elapsed);
            }
         }
      }
      tbm.endSample();
   }

   @Override
   public synchronized void onInitListener() throws MessageSystemException {
      if(disposed) return;
      IOSEEMessageListener[] ref = fastListeners.get();
      for (int i = 0; i < ref.length; i++) {
         IOSEEMessageListener listener = ref[i];
         listener.onInitListener();
      }
   }

   private boolean removeListener(IOSEEMessageListener listener) {
      return fastListeners.remove(listener);
   }

   private boolean removeListener(IOSEEMessageListener listener, SPEED listenerSpeed) {
      return fastListeners.remove(listener);
   }

   @Override
   public void setTimeout(boolean timeout) {
      this.isTimedOut = timeout;
   }

   public MsgWaitResult waitForCondition(ITestEnvironmentAccessor accessor, ICondition condition, boolean maintain, int milliseconds) throws InterruptedException {
//      if(scheduler == null){
//         scheduler = ServiceUtility.getService(Scheduler.class);
//      }
      //TODO figure out how to pass the scheduler along
      WaitOnCondition waitOnCondition = new WaitOnCondition(ServiceUtility.getService(Scheduler.class), condition, maintain, Collections.singletonList(this.message.get()), (long)milliseconds);
      return waitOnCondition.startWaiting();
   }

   private synchronized boolean waitForData() throws InterruptedException {
      
      messageCount = 0;
      if (this.isTimedOut) {
         return true;
      }
      while (messageCount == 0 && !isTimedOut) {
         wait(); // the test environment will notify us after a specified time out
      }
      return isTimedOut;
   }
}
