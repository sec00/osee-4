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
import org.eclipse.osee.ote.message.IMessageCreationListener;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageDataReceiver;
import org.eclipse.osee.ote.message.MessageDataUpdater;
import org.eclipse.osee.ote.message.MessageDataWriter;
import org.eclipse.osee.ote.message.MessageId;
import org.eclipse.osee.ote.message.MessagePublishingHandler;
import org.eclipse.osee.ote.message.PublishInfo;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IMessageManager {
   void destroy();

//   <CLASSTYPE extends Message> CLASSTYPE createMessage(Class<CLASSTYPE> messageClass) throws TestException;

   Class<? extends Message> getMessageClass(String msgClass) throws ClassNotFoundException;
   
   <CLASSTYPE extends Message> int getReferenceCount(CLASSTYPE classtype);

   <CLASSTYPE extends Message> CLASSTYPE findInstance(Class<CLASSTYPE> clazz, boolean writer);

   Collection<Message> getAllMessages();

   Collection<Message> getAllReaders();

   Collection<Message> getAllWriters();

   Collection<Message> getAllReaders(DataType type);

   Collection<Message> getAllWriters(DataType type);

   void init() throws Exception;

   void publishMessages(boolean publish);

   boolean isPhysicalTypeAvailable(DataType physicalType);
   
   Set<DataType> getAvailableDataTypes();

   IMessageRequestor createMessageRequestor(String name);
   
   void update(MessageId id, ByteBuffer data);
   
   void publish(Message msg);
   
   void publish(Message msg, PublishInfo info);
   
//   void publishAtFrameCompletion(Message msg);

   void write(Message message, DestinationInfo object);
   
   /*
    * If using update is not feasible then the receiver of the message can update the 
    * data itself and call this to do all the listener notification. 
    */
   public void notifyListenersOfUpdate(MessageData messageData);

   public void registerWriter(MessageDataWriter writer);
   
   public void unregisterWriter(MessageDataWriter writer);
   
   public MessageDataUpdater registerDataReceiver(MessageDataReceiver receiver);
   
   public void unregisterDataReceiver(MessageDataReceiver receiver);
   
   public void addMessagePublishingHandler(MessagePublishingHandler handler);
   public void removeMessagePublishingHandler(MessagePublishingHandler handler);

   void addPostCreateMessageListener(IMessageCreationListener listener);
   
   void removePostCreateMessageListener(IMessageCreationListener listener);

   void addPreCreateMessageListener(IMessageCreationListener listener);

   void addInstanceRequestListener(IMessageCreationListener listener);
   
   public void schedulePublish(Message message);
   
   public void unschedulePublish(Message message);

   void changeMessageRate(Message message, double newRate, double rate);


}
