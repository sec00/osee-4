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
package org.eclipse.osee.ote.message.interfaces;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;

import org.eclipse.osee.ote.message.DestinationInfo;
import org.eclipse.osee.ote.message.IMemSourceChangeListener;
import org.eclipse.osee.ote.message.IMessageCreationListener;
import org.eclipse.osee.ote.message.IMessageDisposeListener;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageDataReceiver;
import org.eclipse.osee.ote.message.MessageDataUpdater;
import org.eclipse.osee.ote.message.MessageDataWriter;
import org.eclipse.osee.ote.message.MessageId;
import org.eclipse.osee.ote.message.MessagePublishingHandler;
import org.eclipse.osee.ote.message.MessageRemoveHandler;
import org.eclipse.osee.ote.message.PublishInfo;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IMessageManager {
   void addInstanceRequestListener(IMessageCreationListener listener);

//   <CLASSTYPE extends Message> CLASSTYPE createMessage(Class<CLASSTYPE> messageClass) throws TestException;

   public void addMessageRemoveHandler(MessageRemoveHandler messageRemoveHandler);

   public void removeMessageRemoveHandler(MessageRemoveHandler messageRemoveHandler);
   
   public void addMessagePublishingHandler(MessagePublishingHandler handler);
   
   void addPostCreateMessageListener(IMessageCreationListener listener);

   void addPreCreateMessageListener(IMessageCreationListener listener);

   void changeMessageRate(Message message, double newRate, double rate);

   IMessageRequestor createMessageRequestor(String name);

   void destroy();

   <CLASSTYPE extends Message> CLASSTYPE findInstance(Class<CLASSTYPE> clazz, boolean writer);

   Collection<Message> getAllMessages();

   Collection<Message> getAllReaders();

   Collection<Message> getAllReaders(DataType type);

   Collection<Message> getAllWriters();
   
   Collection<Message> getAllWriters(DataType type);

   Set<DataType> getAvailableDataTypes();
   
   Class<? extends Message> getMessageClass(String msgClass) throws ClassNotFoundException;
   
   <CLASSTYPE extends Message> int getReferenceCount(CLASSTYPE classtype);
   
   void init() throws Exception;
   
//   void publishAtFrameCompletion(Message msg);

   boolean isPhysicalTypeAvailable(DataType physicalType);
   
   /*
    * If using update is not feasible then the receiver of the message can update the 
    * data itself and call this to do all the listener notification. 
    */
   public void notifyListenersOfUpdate(MessageData messageData);

   void publish(Message msg);
   
   void publish(Message msg, PublishInfo info);
   
   void publishMessages(boolean publish);
   
   public MessageDataUpdater registerDataReceiver(MessageDataReceiver receiver);
   
   public void registerWriter(MessageDataWriter writer);
   public void removeMessagePublishingHandler(MessagePublishingHandler handler);

   void removePostCreateMessageListener(IMessageCreationListener listener);
   
   public void schedulePublish(Message message);

   public void unregisterDataReceiver(MessageDataReceiver receiver);

   public void unregisterWriter(MessageDataWriter writer);
   
   public void unschedulePublish(Message message);
   
   void update(MessageId id, ByteBuffer data);

   void write(Message message, DestinationInfo object);

   void addMessageListener(Message message, IOSEEMessageListener listener);

   boolean removeMessageListener(Message message, IOSEEMessageListener listener);

   void notifyPreMemSourceChangeListeners(Message message, DataType oldMemType, DataType type);

   void notifyPostMemSourceChangeListeners(Message message, DataType oldMemType, DataType type);

   void addPreMemSourceChangeListener(Message message, IMemSourceChangeListener listener);

   void addPostMemSourceChangeListener(Message message, IMemSourceChangeListener listener);

   void removePreMemSourceChangeListener(Message message, IMemSourceChangeListener listener);

   void removePostMemSourceChangeListener(Message message, IMemSourceChangeListener listener);

   void addSchedulingChangeListener(Message message, IMessageScheduleChangeListener listener);

   void removeSchedulingChangeListener(Message message, IMessageScheduleChangeListener listener);

   void notifySchedulingChangeListeners(Message message, boolean b);

   void notifySchedulingChangeListeners(Message message, double oldRate, double newRate);

   void removePostMessageDisposeListener(Message message, IMessageDisposeListener listener);

   void addPostMessageDisposeListener(Message message, IMessageDisposeListener listener);

   void removePreMessageDisposeListener(Message message, IMessageDisposeListener listener);

   void addPreMessageDisposeListener(Message message, IMessageDisposeListener listener);

   void notifyPreDestroyListeners(Message message);

   void notifyPostDestroyListeners(Message message);

   void addMessageListenerRemovable(Message message, IOSEEMessageListener listener);

   void removeMessageListenerRemovable(Message message, IOSEEMessageListener listener);

   boolean containsListener(Message message, IOSEEMessageListener listener);
   
   void clearRemovableListeners();

   IOSEEMessageListener findMessageListenerType(Message message, Class clazz);

   void clearRemovableListeners(Message message);

   boolean containsListenerType(Message message, Class<? extends IOSEEMessageListener> listenerType);

}
