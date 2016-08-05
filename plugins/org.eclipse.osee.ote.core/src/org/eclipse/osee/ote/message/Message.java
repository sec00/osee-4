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
package org.eclipse.osee.ote.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.condition.ICondition;
import org.eclipse.osee.ote.message.condition.NoTransmissionCondition;
import org.eclipse.osee.ote.message.condition.TransmissionCountCondition;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.MsgWaitResult;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;
import org.eclipse.osee.ote.message.listener.MessageSystemListener;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.ote.scheduler.Scheduler;
import org.w3c.dom.Document;

/**
 * @author Andrew M. Finkbeiner
 */
public class Message implements Xmlizable, XmlizableStream {
  
   private static final double doubleTolerance = 0.000001;
   private static final int TransmissionTimeoutDefault = 15000;
   
   private final LinkedHashMap<String, Element> elementMap;
   //need to rework the waitforData notification to remove the listnerHandlers
   @Deprecated
   private final MessageSystemListener listenerHandler;
   @Deprecated
   private final MessageSystemListener removableListenerHandler;
   private final Set<DataType> memTypeActive = new HashSet<DataType>();

   @JsonProperty
   private final String name;
   private final boolean isScheduledFromStart;
   private final double defaultRate;
   private final int defaultByteSize;
   private final int defaultOffset;

   private final int phase;
   private volatile boolean destroyed = false;

   private volatile boolean isTurnedOff = false;
   private boolean regularUnscheduleCalled = false;
   private DataType currentMemType;
   private double rate;
   private IMessageManager messageManager;
   private IMessageRequestor messageRequestor = null;
   private LegacyMessageMapper mapper;
   private MessageData defaultMessageData;

   
   private MessageId id;
   
   public Message(MessageId id, String name, MessageData data) {
      this.id = id;
      this.defaultMessageData = data;
      this.currentMemType = data.getType();
      listenerHandler = new MessageSystemListener(this);
      this.name = name;
      this.defaultByteSize = data.getDefaultDataByteSize();
      this.defaultOffset = 0;
      elementMap = new LinkedHashMap<String, Element>(20);
      this.phase = 0;
      this.rate = 0.0;
      this.defaultRate = rate;
      this.isScheduledFromStart = false;
      GCHelper.getGCHelper().addRefWatch(this);
      this.removableListenerHandler = new MessageSystemListener(this);
      setMapper(new LegacyMessageMapperDefaultSingle());
   }
   
   public Message(String name, int defaultByteSize, int defaultOffset, boolean isScheduled, int phase, double rate) {
      listenerHandler = new MessageSystemListener(this);
      this.name = name;
      this.defaultByteSize = defaultByteSize;
      this.defaultOffset = defaultOffset;
      elementMap = new LinkedHashMap<String, Element>(20);
      this.phase = phase;
      this.rate = rate;
      this.defaultRate = rate;
      this.isScheduledFromStart = isScheduled;
      GCHelper.getGCHelper().addRefWatch(this);
      this.removableListenerHandler = new MessageSystemListener(this);
   }
   
   public void addElement(Element element) {
      checkState();
      elementMap.put(element.getName(), element);
   }
   
   public void addElements(Element... elements) {
      checkState();
      for (Element element : elements) {
         elementMap.put(element.getElementName(), element);
         element.addPath(this.getClass().getName());
      }
   }
   
   public void addListener(IOSEEMessageListener listener) {
      if(messageManager != null){
         messageManager.addMessageListener(this, listener);
      }
   }

   public void addMessageTypeAssociation(DataType memType, Message messageToBeAdded) {
      mapper.addMessageTypeAssociation(this, memType, messageToBeAdded);
   }

   public void addPostMemSourceChangeListener(IMemSourceChangeListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.addPostMemSourceChangeListener(this, listener);
      }
   }

   public void addPostMessageDisposeListener(IMessageDisposeListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.addPostMessageDisposeListener(this, listener);
      }
   }

   public void addPreMemSourceChangeListener(IMemSourceChangeListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.addPreMemSourceChangeListener(this, listener);
      }
   }

   public void addPreMessageDisposeListener(IMessageDisposeListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.addPreMessageDisposeListener(this, listener);
      }
   }

   /**
    * Adds listener to the list of listeners removed at the end of every script.
    * 
    * @param listener the removable listern to add.
    */
   public void addRemovableListener(IOSEEMessageListener listener) {
      if(messageManager != null){
         messageManager.addMessageListenerRemovable(this, listener);
      }
   }

   public void addSchedulingChangeListener(IMessageScheduleChangeListener listener) {
      checkState();
      if(messageManager !=null){
         messageManager.addSchedulingChangeListener(this, listener);
      }
   }

   public void addSendListener(IMessageSendListener listener) {
      getActiveDataSource().addSendListener(listener);
   }

   /**
    * Changes the rate a message is being published at. NOTE: This is only going to be allowed to be used on periodic
    * message and users are not allowed to set rate to zero.
    * 
    * @param newRate - hz
    */
   public void changeRate(double newRate) {
      if (Math.abs(newRate - 0.0) < doubleTolerance) { //newRate == 0.0
         throw new IllegalArgumentException(
            "Cannot change message rate to zero (" + getName() + ")!\n\tUse unschedule() to do that!");
      }
      if (Math.abs(newRate - rate) > doubleTolerance) { //newRate != rate
         if(messageManager != null){
            messageManager.changeMessageRate(this, newRate, rate);
         }
         double oldRate = rate;
         rate = newRate;
         if(messageManager != null){
            messageManager.notifySchedulingChangeListeners(this, oldRate, newRate);
         }
      }
   }

   /**
    * Changes the rate back to the default rate.
    * 
    */
   public void changeRateToDefault(ITestEnvironmentMessageSystemAccessor accessor) {
      //      accessor.getMsgManager().changeMessageRate(this, defaultRate, rate);
      double oldRate = getRate();
      rate = defaultRate;
      if(messageManager != null){
         messageManager.notifySchedulingChangeListeners(this, oldRate, defaultRate);
      }
   }

   /**
    * Verifies that the message is not sent within the time specified.
    * 
    * @param milliseconds the amount to time (in milliseconds) to check
    * @return if the check passed
    */
   public boolean checkForNoTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int milliseconds) throws InterruptedException {
      checkState();
      accessor.getLogger().methodCalledOnObject(accessor, getMessageName(),
         new MethodFormatter().add(milliseconds));
      NoTransmissionCondition c = new NoTransmissionCondition();
      MsgWaitResult result = waitForCondition(accessor, c, true, milliseconds);
      CheckPoint passFail =
         new CheckPoint(this.name, Integer.toString(0), Integer.toString(result.getXmitCount()),
            result.isPassed(), result.getXmitCount(), result.getElapsedTime());
      accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestScript().getTestCase(), passFail);
      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }

   /**
    * Verifies that the message is sent at least once using the default message timeout. DO NOT override this method in
    * production code.
    * 
    * @return if the check passed
    */
   public boolean checkForTransmission(ITestAccessor accessor) throws InterruptedException {
      return checkForTransmission(accessor, TransmissionTimeoutDefault);
   }

   /**
    * Verifies that the message is sent at least once within the time specified. DO NOT override this method in
    * production code.
    * 
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    */
   public boolean checkForTransmission(ITestAccessor accessor, int milliseconds) throws InterruptedException {
      return checkForTransmissions(accessor, 1, milliseconds);
   }

   /**
    * Verifies that the message is sent at least "numTransmission" times within the default message timeout. DO NOT
    * override this method in production code.
    * 
    * @param numTransmissions the number of transmissions to look for
    * @return if the check passed
    */
   public boolean checkForTransmissions(ITestAccessor accessor, int numTransmissions) throws InterruptedException {
      return checkForTransmissions(accessor, numTransmissions, TransmissionTimeoutDefault);
   }

   /**
    * Verifies that the message is sent at least "numTransmission" times within the time specified.
    * 
    * @param numTransmissions the number of transmission to look for
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    */
   public boolean checkForTransmissions(ITestAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      checkState();
      accessor.getLogger().methodCalledOnObject(accessor, getMessageName(),
         new MethodFormatter().add(numTransmissions).add(milliseconds));
      TransmissionCountCondition c = new TransmissionCountCondition(numTransmissions);
      MsgWaitResult result = waitForCondition(accessor, c, false, milliseconds);
      CheckPoint passFail =
         new CheckPoint(this.name, Integer.toString(numTransmissions), Integer.toString(result.getXmitCount()),
            result.isPassed(), result.getXmitCount(), result.getElapsedTime());
      accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }
   
   /**
    * Removes all the listeners from the RemovableListenerHandler. This method is meant to be called upon script
    * completion but can be used by anyone. Other listeners can be removed using the traditional removeListener call.
    */
   public void clearRemovableListeners() {
      if(messageManager != null){
         messageManager.clearRemovableListeners(this);
      }
   }
   
   public boolean containsSendListener(IMessageSendListener listener) {
      return getActiveDataSource().containsSendListener(listener);
   }
   
   public boolean containsListener(IOSEEMessageListener listener){
      if(messageManager != null){
         return messageManager.containsListener(this, listener);
      }
      return false;
   }
   
   public boolean containsListenerType(Class<? extends IOSEEMessageListener> listenerType){
      if(messageManager != null){
         return messageManager.containsListenerType(this, listenerType);
      }
      return false;
   }

   public void destroy() {
      turnOff();
      if(messageManager != null){
         messageManager.notifyPreDestroyListeners(this);
      }

      mapper.removeMessage(this);
      destroyed = true;
      defaultMessageData.dispose();

      elementMap.clear();

      if (messageRequestor != null) {
         messageRequestor.dispose();
      }
      if(messageManager != null){
         messageManager.notifyPostDestroyListeners(this);
      }
   }
   
   @SuppressWarnings("rawtypes")
   public IOSEEMessageListener findMessageListenerType(Class clazz) {
      if(messageManager != null){
         return messageManager.findMessageListenerType(this, clazz);
      }
      return null;
   }

   public MessageData getActiveDataSource() {
      if(mapper != null){
         return mapper.getMessageData(this, currentMemType);
      } else {
         return getDefaultMessageData();
      }
   }

   public MessageData getActiveDataSource(DataType type) {
      return mapper.getMessageData(this, type);
   }

   public long getActivityCount() {
      return getActiveDataSource().getActivityCount();
   }

   /**
    * @return the memToDataMap
    */
   public Collection<MessageData> getAllData() {
      checkState();
      
      return (Collection<MessageData>)mapper.getAllMessageDatas(this);
   }

   public void getAllElements(Collection<Element> elements) {
      checkState();
      IMessageHeader header = getActiveDataSource().getMsgHeader();
      if (header != null) {
         Collections.addAll(elements, header.getElements());
      }
      elements.addAll(elementMap.values());

   }

   public Map<DataType, Class<? extends Message>[]> getAssociatedMessages() {
      return new HashMap<DataType, Class<? extends Message>[]>();
   }

   public Set<DataType> getAvailableMemTypes() {
      checkState();
      return mapper.getAvailableDataTypes(this);
   }

   public int getBitOffset() {
      return 0;
   }

   public Element getBodyOrHeaderElement(String elementName) {
      return getBodyOrHeaderElement(elementName, currentMemType);
   }

   public Element getBodyOrHeaderElement(String elementName, DataType type) {
      CopyOnWriteNoIteratorList<Message> messages = mapper.getMessages(this, type);
      return findElement(messages, elementName);
   }

   public byte[] getData() {
      checkState();
      return getActiveDataSource().toByteArray();
   }

   public int getDefaultByteSize() {
      return defaultByteSize;
   }

   /**
    * @return the defaultMessageData
    */
   public MessageData getDefaultMessageData() {
      return defaultMessageData;
   }

   public int getDefaultOffset() {
      return defaultOffset;
   }

   public Element getElement(List<Object> elementPath) {
      CopyOnWriteNoIteratorList<Message> messages = mapper.getMessages(this, currentMemType);
      return findElement(messages, elementPath);
   }

   public Element getElement(List<Object> elementPath, DataType type) {
      CopyOnWriteNoIteratorList<Message> messages = mapper.getMessages(this, type);
      return findElement(messages, elementPath);
   }

   /**
    * @param elementName the name of the element as defined in the message ( All caps ).
    * @return the element associated with the given name
    * @throws IllegalArgumentException if an element doesn't exist with given name. Use {@link #hasElement(String)} with
    * any use of this function.
    */
   public Element getElement(String elementName) {
      return getElement(elementName, currentMemType);
   }

   public <E extends Element> E getElement(String elementName, Class<E> clazz) {
      checkState();
      return clazz.cast(getElement(elementName, currentMemType));
   }

   /**
    * @return the element associated with the given name
    * @throws IllegalArgumentException if an element doesn't exist with given name. Use {@link #hasElement(String)} with
    * any use of this function.
    */
   public Element getElement(String elementName, DataType type) {
      CopyOnWriteNoIteratorList<Message> messages = mapper.getMessages(this, type);
      return findElement(messages, elementName);
   }
   
   public Element getElementByPath(ElementPath path) {
      return getElementByPath(path, currentMemType);
   }

   public ListIterator<Element> getElementIterator(Element elemnt) {
	   ArrayList<Element> list = new ArrayList<Element>(elementMap.values());
	   int index = list.indexOf(elemnt);
	   if (index >= 0) {
		   return list.listIterator(index);		   
	   }
	   return null;
   }

   /**
    * @return HashMap<String, Element>
    */
   public HashMap<String, Element> getElementMap() {
      checkState();
      return elementMap;
   }

   /**
    * Gets a list of all the message's data elements.
    * <br>
    * This returns ALL the elements, which may not be mapped to the
    * active data type and/or may be non-mapping elements.
    * 
    * Use {@link #getElements(DataType)} to get mapped elements
    * 
    * @return a collection of {@link Element}s
    */
   public Collection<Element> getElements() {
      checkState();
      return elementMap.values();
   }

  
   /**
    * @return a collection of mapped {@link Element}s for the specified DataType
    */
   public Collection<Element> getElements(DataType type) {
      Message[] messages = mapper.getMessages(this, type).get();
      ArrayList<Element> elements = new ArrayList<Element>();
      for(int i = 0; i < messages.length; i++){
         elements.addAll(messages[i].getLocalElements());
      }
      return elements;
   }

   public IMessageHeader[] getHeaders() {
      final Collection<MessageData> dataSources = getMemSource(getMemType());
      if (dataSources.size() > 0) {
         final IMessageHeader[] headers = new IMessageHeader[dataSources.size()];
         int i = 0;
         for (MessageData dataSrc : dataSources) {
            headers[i] = dataSrc.getMsgHeader();
            i++;
         }
         return headers;
      } else {
         return new IMessageHeader[0];
      }
   }

   /**
    * Returns the number of byte words in the header of this message.
    * 
    * @return the number of bytes in the header
    */
   public int getHeaderSize() {
      final IMessageHeader hdr =  mapper.getMessageData(this, currentMemType).getMsgHeader();
      if (hdr != null) {
         return hdr.getHeaderSize();
      }
      return 0;
   }

//   /**
//    * This is the turnOn being called from the method register in MessageCollection. Messages shouldn't be scheduled at
//    * this point b/c the control message hasn't gone out yet. Messages can't go out until the control message goes out
//    * the first time so that collisions in the box are avoided.
//    */
//   public void whenBeingRegisteredTurnOn() {
//      isTurnedOff = false;
//   }

   public int getHeaderSize(DataType type) {
      return mapper.getMessageData(this, currentMemType).getMsgHeader().getHeaderSize();
   }

   @Deprecated
   public MessageSystemListener getListener() {
      return listenerHandler;
   }
   
   @Deprecated
   public MessageSystemListener getRemoveableListener() {
      return listenerHandler;
   }

   /**
    * @return Returns size value.
    */
   public int getMaxDataSize() {
      checkState();
      return getMaxDataSize(currentMemType);
   }

   public int getMaxDataSize(DataType type) {
      return mapper.getMessageData(this, type).getPayloadSize();
   }

   public MessageData getMemoryResource() {
      return mapper.getMessageData(this, currentMemType);
   }

   //get rid of collection return
   public Collection<MessageData> getMemSource(DataType type) {
      MessageData data = mapper.getMessageData(this, type);
      List<MessageData> list =  new ArrayList<MessageData>();
      if(data != null){
         list.add(data);
      }
      return list;
   }

   public boolean getMemSource(DataType type, Collection<MessageData> listToAddto) {
      MessageData messageData = mapper.getMessageData(this, currentMemType);
      if(messageData!=null){
         return listToAddto.add(messageData);
      }
      return false;
   }

   public DataType getMemType() {
      return currentMemType;
   }

   public MessageData getMessageData(DataType type) {
      checkState();
      return mapper.getMessageData(this, type);
   }

   public MessageId getMessageId() {
      return id;
   }

   /**
    * @return Returns the messageName.
    */
   public String getMessageName() {
      return name;
   }

   /**
    * returns a {@link MessageState} object that represents this message's state. The state is intended to be used in
    * synchronizing a remote instance of this message
    * 
    * @return Returns MessageState object reference.
    */
   public MessageState getMessageState() {
      checkState();
      MessageMode mode = isWriter() ? MessageMode.WRITER : MessageMode.READER;
      return new MessageState(currentMemType, getData(), mapper.getAvailableDataTypes(this), mode);
   }

   /*
    * public HashMap getTypeToMessageData(){ return typeToMessageData; }
    */
   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * Returns the number of byte words in the payload of this message.
    * 
    * @return number of bytes in the message payload
    */
   public int getPayloadSize() {
      return mapper.getMessageData(this, currentMemType).getPayloadSize();
   }

   public int getPayloadSize(DataType type) {
      return mapper.getMessageData(this, type).getPayloadSize();
   }

   /**
    * @return - int - phase of message
    */
   public int getPhase() {
      return phase;
   }

   /**
    * @return - double - rate of message
    */
   public double getRate() {
      return rate;
   }

   public long getSentCount() {
      return getActiveDataSource().getSentCount();
   }

   @JsonProperty
   public String getType() {
       return getMemType().name();
   }
   
   public String getTypeName() {
      return getName();
   }

   public boolean hasElement(List<Object> elementPath) {
      return getElement(elementPath) != null;
   }

   /**
    * @return true if the Message contains an element with the given name, false otherwise
    */
   public boolean hasElement(String elementName) {
      checkState();
      return elementMap.containsKey(elementName);
   }

   public boolean isDestroyed() {
      return destroyed;
   }

   /**
    * This variable reflects whether unsubscribe has been called on the message. The main purpose of this is to preserve
    * if an unschedule is called on a message from a constructor.
    * 
    * @return Returns the regularUnscheduleCalled.
    */
   public boolean isRegularUnscheduleCalled() {
      return regularUnscheduleCalled;
   }

   public boolean isMemTypeActive(DataType type) {
      checkState();
      return memTypeActive.contains(type);
   }

   /**
    * Returns if the message is scheduled or not.
    */
   @Deprecated
   public boolean isScheduled() {
      return mapper.getMessageData(this, currentMemType).isScheduled();
   }

   /**
    * This variable reflects whether a message is defined to start out being scheduled.
    * 
    * @return Returns the isScheduledFromStart.
    */
   public boolean isScheduledFromStart() {
      return isScheduledFromStart;
   }

   /**
    * Returns if the message is turned off.
    */
   public boolean isTurnedOff() {
      return isTurnedOff;
   }

   public boolean isValidElement(Element currentElement, Element proposedElement) {
      return true;
   }

   public boolean isWriter() {
      checkState();
      return defaultMessageData.isWriter();
   }

   /**
    * Notifies all registered listeners of an update.
    * <P>
    * <B>NOTE: </B>Should only be called from sub classes of {@link MessageData}
    * 
    * @param data the Message Data object that has been updated
    * @param type the memtype of the message data object
    */
   public void notifyListeners(final MessageData data, final DataType type) {
//      checkState();
      if(messageManager != null){
         messageManager.notifyListenersOfUpdate(data);
      }
//      this.listenerHandler.onDataAvailable(data, type);
//      this.removableListenerHandler.onDataAvailable(data, type);
   }

   /**
    * Remove a message listener if it exists.
    * 
    * @param listener
    * @return true if a listener was removed
    */
   public boolean removeListener(IOSEEMessageListener listener) {
      if(messageManager != null){
         return messageManager.removeMessageListener(this, listener);
      }
      return false;
   }

   public void removePostMemSourceChangeListener(IMemSourceChangeListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.removePostMemSourceChangeListener(this, listener);
      }
   }

   public void removePostMessageDisposeListener(IMessageDisposeListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.removePostMessageDisposeListener(this, listener);
      }
   }

   public void removePreMemSourceChangeListener(IMemSourceChangeListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.removePreMemSourceChangeListener(this, listener);
      }
   }

   public void removePreMessageDisposeListener(IMessageDisposeListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.removePreMessageDisposeListener(this, listener);
      }
   }

   /**
    * Attemps to remove the specified listener from the list of REMOVABLE listeners. This will NOT remove any listener
    * added using the addListener() call, only those added using the addRemovableListener() call will be removed.
    * 
    * @param listener The removable listener to remove
    */
   public void removeRemovableListener(IOSEEMessageListener listener) {
      checkState();
      if(messageManager != null){
         messageManager.removeMessageListenerRemovable(this, listener);
      }
   }

   public void removeSchedulingChangeListener(IMessageScheduleChangeListener listener) {
      checkState();
      if(messageManager !=null){
         messageManager.removeSchedulingChangeListener(this, listener);
      }
   }

   public void removeSendListener(IMessageSendListener listener) {
      getActiveDataSource().removeSendListener(listener);
   }

   /**
    * This is called at the end of a script run to reset the "hard" unschedule variable that is used to preserve
    * unschedules called in constructors.
    */
   public void resetScheduling() {
      regularUnscheduleCalled = false;

   }

   /**
    * This method schedules the message. There is also some code that allows the scheduled state to be updated in
    * Message Watch.
    */
   public void schedule() {
      checkState();
      if (!isTurnedOff) {
         setSchedule(true);
         regularUnscheduleCalled = false;
         if(messageManager != null){
            messageManager.schedulePublish(this);
            messageManager.notifySchedulingChangeListeners(this, true);
         }
      }
   }

   /*
    * protected static final ThreadLocal current = new ThreadLocal() { protected Object initialValue() { return new
    * MemMessageHolder(); } };
    */
   public void send() throws MessageSystemException {
      if(messageManager != null){
         messageManager.publish(this);
      } else {
         OseeLog.log(getClass(), Level.WARNING, String.format("Unable to send [%s] because message manager has not been set", getName()));
      }
   }

   public void send(DataType type) throws MessageSystemException {
      checkState();
      if (!isTurnedOff) {
         Message[] messages = mapper.getMessages(this, type).get();
         for(int i = 0; i < messages.length; i++){
            messages[i].send();
         }
      } else {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING,
            this.getMessageName() + " has attempted a send(), but is currently turned off.");
      }
   }

   public void send(PublishInfo info) throws MessageSystemException {
      if(messageManager != null){
         messageManager.publish(this, info);
      } else {
         OseeLog.log(getClass(), Level.WARNING, String.format("Unable to send [%s] because message manager has not been set", getName()));
      }
   }

   public void sendWithLog(ITestAccessor accessor) {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, getMessageName(), new MethodFormatter(), this);
      }
      send();
      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
   }
   
   public void setActivityCount(long activityCount) {
      getActiveDataSource().setActivityCount(activityCount);
   }

   public void setBackingBuffer(byte[] data) {
      MessageData messageData = mapper.getMessageData(this, currentMemType);
      messageData.setNewBackingBuffer(data);
   }

   public void setData(byte[] data) {
      MessageData messageData = mapper.getMessageData(this, currentMemType);
      messageData.setFromByteArray(data);
   }

   public void setData(byte[] data, int length) {
      MessageData messageData = mapper.getMessageData(this, currentMemType);
      messageData.setFromByteArray(data, length);
   }

   public void setData(ByteBuffer data, int length) {
      MessageData messageData = mapper.getMessageData(this, currentMemType);
      messageData.setFromByteArray(data, length);
   }
   
   public boolean setMemSource(DataType type) {
      checkState();
      
      DataType oldMemType = getMemType();
      if(messageManager != null){
         messageManager.notifyPreMemSourceChangeListeners(this, oldMemType, type);
      }
      mapper.updatePublicFieldReferences(this, type);
      setCurrentMemType(type);
      if(messageManager != null){
         messageManager.notifyPostMemSourceChangeListeners(this, oldMemType, type);
      }
      return true;
   }

   public boolean setMemSource(ITestEnvironmentAccessor accessor, DataType type) {
      return setMemSource(type);
   }

   public void setMemTypeActive(DataType type) {
      checkState();
      memTypeActive.add(type);
      if(messageManager != null){
         messageManager.notifyPostMemSourceChangeListeners(this, currentMemType, currentMemType);
      }
   }

   public void setMemTypeInactive(DataType type) {
      checkState();
      memTypeActive.add(type);
      if(messageManager != null){
         messageManager.notifyPostMemSourceChangeListeners(this, currentMemType, currentMemType);
      }
   }

   public void setMessageId(MessageId id) {
      this.id = id;
   }

   /**
    * restores the state of this message. The state is intended to come from a remote instance of this message.
    */
   public void setMessageState(final MessageState state) {
      checkState();
      setMemSource(state.getCurrentMemType());
      setData(state.getData());
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public org.w3c.dom.Element toXml(Document doc) {
      org.w3c.dom.Element rootElement = doc.createElement("Message");
      rootElement.appendChild(Jaxp.createElement(doc, "Name", name));
      rootElement.appendChild(Jaxp.createElement(doc, "Type", getMemType().name()));
      return rootElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("Message");
      XMLStreamWriterUtil.writeElement(writer, "Name", name);
      XMLStreamWriterUtil.writeElement(writer, "Type", getMemType().name());
      writer.writeEndElement();
   }

   /**
    * Turning off a message causes sends to be short-circuited and the message to be unscheduled.
    */
   public void turnOff() {
      checkState();
      isTurnedOff = true;
      unschedule();
   }

   /**
    * Turning on message allows sends to work again & reschedules message if that is the default state defined by the
    * message constructor call.
    */
   public void turnOn() {
      checkState();
      isTurnedOff = false;
      if (isScheduledFromStart()) {
         schedule();
      }
   }

   /**
    * This method unschedules the message. The variable regularUnscheduledCalled is used to preserve unschedules that
    * are called in constructors, which is before the control message goes out for the first time.
    */
   public void unschedule() {
      checkState();
      setSchedule(false);
      regularUnscheduleCalled = true;
      if(messageManager != null){
         messageManager.unschedulePublish(this);
         messageManager.notifySchedulingChangeListeners(this, false);
      }
   }

   public MsgWaitResult waitForCondition(ITestEnvironmentAccessor accessor, ICondition condition, boolean maintain, int milliseconds) throws InterruptedException {
      checkState();
      WaitOnCondition waitOnCondition = new WaitOnCondition(ServiceUtility.getService(Scheduler.class), condition, maintain, Collections.singletonList(this), (long)milliseconds);
      return waitOnCondition.startWaiting();
//      return listenerHandler.waitForCondition(accessor, condition, maintain, milliseconds);
   }

   /**
    * Waits until message is sent at least once within the default message timeout.
    * 
    * @return if the check passed
    */
   public boolean waitForTransmission(ITestEnvironmentMessageSystemAccessor accessor) throws InterruptedException {
      return waitForTransmission(accessor, TransmissionTimeoutDefault);
   }

   /**
    * Waits until message is sent at least once within the time specified.
    * 
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    */
   public boolean waitForTransmission(ITestEnvironmentMessageSystemAccessor accessor, int milliseconds) throws InterruptedException {
      return waitForTransmissions(accessor, 1, milliseconds);
   }

   /**
    * Waits until message is sent at least "numTransmission" times within the default message timeout.
    * 
    * @param numTransmissions the number of transmissions to look for
    * @return if the check passed
    */
   public boolean waitForTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions) throws InterruptedException {
      return waitForTransmissions(accessor, numTransmissions, TransmissionTimeoutDefault);
   }

   /**
    * Waits until message is sent at least "numTransmission" times within the time specified.
    * 
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    */
   public boolean waitForTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      checkState();
      accessor.getLogger().methodCalledOnObject(accessor, getMessageName(),
         new MethodFormatter().add(numTransmissions).add(milliseconds), this);
      boolean pass = waitForTransmissionsNoLog(accessor, numTransmissions, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return pass;
   }

   public boolean waitForTransmissionsNoLog(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      checkState();
      if (accessor == null) {
         throw new IllegalArgumentException("environment accessor parameter cannot be null");
      }
      TransmissionCountCondition c = new TransmissionCountCondition(numTransmissions);
      MsgWaitResult result = waitForCondition(accessor, c, false, milliseconds);
      return result.isPassed();
   }

   public void zeroize() {
      checkState();
      for (DataType memType : mapper.getAvailableDataTypes(this)) {
         for (Element el : getElements(memType)) {
            el.zeroize();
         }
      }
   }

   protected void checkState() throws IllegalStateException {
      if (isDestroyed()) {
         throw new IllegalStateException(getName() + " is destroyed");
      }
   }

   /**
    * @param currentMemType the currentMemType to set
    */
   protected void setCurrentMemType(DataType currentMemType) {
      checkState();
      this.currentMemType = currentMemType;
   }


   /**
    * @param defaultMessageData the defaultMessageData to set
    */
   protected void setDefaultMessageData(MessageData defaultMessageData) {
      checkState();
      this.defaultMessageData = defaultMessageData;
      setMapper(new LegacyMessageMapperDefaultSingle());
   }

   void setMapper(LegacyMessageMapper mapper){
      this.mapper = mapper;
      this.defaultMessageData.setMapper(mapper);
   }

   void setMessageManager(IMessageManager messageManager){
      this.messageManager = messageManager;
   }

   void setTurnOn(){
      this.isTurnedOff = false;
   }

   void setWriter(boolean isWriter){
      getActiveDataSource().setWriter(isWriter);
   }

   /**
    * DO NOT USE THIS.
    * 
    * @param messages
    * @param elementPath
    * @return
    */
   private Element findElement(CopyOnWriteNoIteratorList<Message> messages, List<Object> elementPath){
      Element el = null;
      RecordElement rel = null;
      if (elementPath.size() == 1) {
         el = elementMap.get(elementPath.get(0));
      } else {
         String string = (String) elementPath.get(1);
         if (string.startsWith("HEADER(")) {
            Element[] elements = getActiveDataSource(currentMemType).getMsgHeader().getElements();
            for (Element element : elements) {
               if (element.getName().equals(elementPath.get(2))) {
                  return element;
               }
            }
            return null;
         } else {
            el = this.elementMap.get(string);
            if (el instanceof RecordElement) {
               rel = (RecordElement) el;
            }
         }
         for (int i = 2; i < elementPath.size(); i++) {
            if (elementPath.get(i) instanceof String) {
               String name = (String) elementPath.get(i);
               el = rel.getElementMap().get(name);
               if (el instanceof RecordElement) {
                  rel = (RecordElement) el;
               }
            } else if (elementPath.get(i) instanceof Integer) {
               Integer index = (Integer) elementPath.get(i);
               rel = rel.get(index);
               el = rel;
            }
         }
      }
      return el;
   }

   private Element findElement(CopyOnWriteNoIteratorList<Message> messages, String elementName){
      Message[] messagesArr = messages.get();
      Element el = null;
      for(int i = 0; i < messagesArr.length; i++){
         el = messagesArr[i].getElementMap().get(elementName);
         if(el != null){
            return el;
         }
      }
      return null;
   }
   
   private Element getElementByPath(ElementPath path, DataType type) {
      if (path.isHeaderElement()) {
         Element[] elements = getActiveDataSource(type).getMsgHeader().getElements();
         for (Element element : elements) {
            if (element.getName().equals(path.getElementName())) {
               return element;
            }
         }
         return null;
      }
      return getElement(path.getList(), type);
   }

   private Collection<Element> getLocalElements() {
      return elementMap.values();
   }
   
   private void setSchedule(boolean newValue) {
      mapper.getMessageData(this, currentMemType).setScheduled(newValue);
   }
   
   /**
    * The ability to directly access the listeners is going away.
    * 
    * @return
    */
   @Deprecated
   public List<IOSEEMessageListener> getListeners(){
      if(messageManager != null){
         return messageManager.getMessageListeners(this);
      }
      return new ArrayList<>();
   }
   
}