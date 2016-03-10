package org.eclipse.osee.ote.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.Namespace;

public class MessageController implements IMessageManager {

   private final MessageCollection messageCollection;
   private final ClassLocator classLocator;
   private final LegacyMessageMapper mapper;
   private final HashMap<Message, HashSet<IMessageRequestor>> requestorReferenceMap = new HashMap<Message, HashSet<IMessageRequestor>>(200);
   private final Map<IOType, CopyOnWriteNoIteratorList<MessageDataWriter>> messageDataWriters = new ConcurrentHashMap<IOType, CopyOnWriteNoIteratorList<MessageDataWriter>>();
   private final Map<IOType, MessagePublishingHandler> messagePublishingHandlers = new ConcurrentHashMap<IOType, MessagePublishingHandler>();
   private final Map<MessageId, MessageData> idToDataMap = new ConcurrentHashMap<MessageId, MessageData>(400);
   
   private final Set<DataType> availableDataTypes = new HashSet<>();
   
   private final PeriodicPublishMap periodicPublish;
   
   private final Set<Integer> printIdMessage = new HashSet<>();
   
   private final List<MessageDataReceiver> messageDataReceivers = new CopyOnWriteArrayList<>();
   
   private final  List<IMessageCreationListener> preCreation = new ArrayList<IMessageCreationListener>();
   private final  List<IMessageCreationListener> postCreation = new ArrayList<IMessageCreationListener>();
   private final  List<IMessageCreationListener> instanceRequestListeners = new ArrayList<IMessageCreationListener>();
   private MessagePublishingHandler defaultpublisher = new MessagePublishingHandlerDefault();
   private boolean debug = false;
   private MessageControllerConsoleCommands commands;
   private boolean useSpecialMapping;
 
   //ITimerControl should become Scheduler
   public MessageController(ClassLocator classLocator, ITimerControl timerControl){
      this.classLocator = classLocator;
      this.messageCollection = new MessageCollection(); 
      this.mapper = new LegacyMessageMapperService();
      this.periodicPublish = new PeriodicPublishMap(this, timerControl);      
      
      commands = new MessageControllerConsoleCommands(this);
      
      useSpecialMapping = Boolean.parseBoolean(System.getProperty("ote.signal.mapping", "false"));
   }
   
   
   
   Map<IOType, CopyOnWriteNoIteratorList<MessageDataWriter>> getDataWriters(){
      return messageDataWriters;
   }
   
   Map<IOType, MessagePublishingHandler> getMessagePublishers(){
      return messagePublishingHandlers;
   }
   
   
   
   @Override
   public void destroy() {
      commands.dispose();
   }
   
   public void addMessagePublishingHandler(MessagePublishingHandler handler){
      messagePublishingHandlers.put(handler.getType(), handler);
   }
   
   public void removeMessagePublishingHandler(MessagePublishingHandler handler){
      messagePublishingHandlers.remove(handler.getType());
   }

   List<IMessageCreationListener> getPreCreationListeners(){
      return preCreation;
   }
   List<IMessageCreationListener> getPostCreationListeners(){
      return postCreation;
   }
   
   List<IMessageCreationListener> getInstanceRequestListeners(){
      return instanceRequestListeners;
   }
 
   List<MessageDataReceiver> getMessageDataReceivers(){
      return messageDataReceivers;
   }

   private <CLASSTYPE extends Message> void notifyPreCreateMessage(Class<CLASSTYPE> messageClass, IMessageRequestor requestor, boolean writer){
      for (IMessageCreationListener listener : preCreation) {
         listener.onPreCreate(messageClass, requestor, writer);
      }
   }
   
   @SuppressWarnings("unchecked")
   private <CLASSTYPE extends Message> void notifyPostCreateMessage(Class<CLASSTYPE> messageClass, IMessageRequestor requestor, boolean writer, Message message){
      Namespace namespace = new Namespace(message.getDefaultMessageData().getType().name());
      for (IMessageCreationListener listener : postCreation) {
         listener.onPostCreate(messageClass, requestor, writer, (CLASSTYPE)message, namespace);
      }
   }
   
   private void setupMessageMapping(IMessageRequestor requestor, Message message){

      mapper.addMessage(message);
      Map<? extends DataType, Class<? extends Message>[]> types;
      MessageAssociationLookup lookup = ServiceUtility.getService(MessageAssociationLookup.class);
     
      if(lookup != null && useSpecialMapping){
         types = lookup.getAssociatedMessages((Class<Message>) message.getClass());
      } else {
         types = message.getAssociatedMessages();
      }
      
      for(DataType type: types.keySet()){
         if (!availableDataTypes.isEmpty() && isPhysicalTypeAvailable(type)) {
            Class<? extends Message>[] messages = types.get(type);
            for(Class<? extends Message> messageClass:messages){
               Message otherMessage;
               if(message.isWriter()){
                  otherMessage = requestor.getMessageWriter(messageClass);
               } else {
                  otherMessage = requestor.getMessageReader(messageClass);
               }
               mapper.addMessageTypeAssociation(message, type, otherMessage);
            }
            message.setMemSource(type);
         }
      }
   }
   
   private <CLASSTYPE extends Message> CLASSTYPE createMessage(Class<CLASSTYPE> messageClass, IMessageRequestor requestor, boolean writer, boolean singleton) throws TestException {
      try {
         notifyPreCreateMessage(messageClass, requestor, writer);
         CLASSTYPE message = messageClass.newInstance();
         //setup the message services
         message.setWriter(writer);
         message.setMapper(mapper);
         message.setMessageManager(this);
         if(singleton){
            messageCollection.add(message);
         }
         setupMessageMapping(requestor, message);
         if(writer){
            setupMessageWriter(message);
         } else {
            setupMessageReader(message);
         }
         notifyPostCreateMessage(messageClass, requestor, writer, message);
         //message sending is disabled until all post create message listeners have been called
         message.setTurnOn();
         return message;
      } catch (InstantiationException e) {
         throw new TestException("Failed message createion", Level.SEVERE, e);
      } catch (IllegalAccessException e) {
         throw new TestException("Failed message createion", Level.SEVERE, e);
      }
   }

   private void setupMessageReader(Message message) {
      //determine the MessageId for the update lookup
      registerMessage(message);
   }

   private void registerMessage(Message message) {
      if(message.getMessageId() == null){
         System.out.println(message.getDefaultMessageData().getClass().getName() + " is not setting the message id");
      } else {
         idToDataMap.put( message.getMessageId(), message.getDefaultMessageData());
      }
   }

   private void setupMessageWriter(Message message) {
      if(message.getRate() != 0.0){
         PeriodicPublishTask task = periodicPublish.get(message.getRate(), 1);
         task.put(message);
      }
      //move functionality to Message creation listener --- post create
//      for(MessageWriterSetupHandler handler:messageSetupHandlers){
//         handler.setup(message);
//      }
      //add the new periodic publish
//      if (message.getRate() != 0.0) {
//         addMessageToRateTask((LbaMessage) message, message.getRate());
//      } else if (message.isScheduled()) {
//         log(Level.INFO, message.getMessageName() + " has attempted to be scheduled at 0 Hz!!!");
//      }
   }
   
   public void schedulePublish(Message message){
      if(message.getRate() != 0.0){
         PeriodicPublishTask task = periodicPublish.get(message.getRate(), 1);
         task.put(message);
      }
   }
   
   public void unschedulePublish(Message message){
      if(message.getRate() != 0.0){
         PeriodicPublishTask task = periodicPublish.get(message.getRate(), 1);
         task.remove(message);
      }
   }

   @SuppressWarnings("unchecked")
   private <CLASSTYPE extends Message> CLASSTYPE getMessageReader(IMessageRequestor req, Class<CLASSTYPE> messageClass) throws TestException {
      Message msg = messageCollection.findReader(messageClass);
      if(msg == null){
         msg = createMessage(messageClass, req, false, true);
      }
      addRequestorReference(req, msg);
      for (IMessageCreationListener listener : instanceRequestListeners) {
         listener.onInstanceRequest(messageClass, (CLASSTYPE)msg, req, false);
      }
      return (CLASSTYPE)msg;
   }
   
   @SuppressWarnings("unchecked")
   private <CLASSTYPE extends Message> CLASSTYPE getMessageWriter(IMessageRequestor req, Class<CLASSTYPE> messageClass) throws TestException {
      Message msg = messageCollection.findWriter(messageClass);
      if(msg == null){
         msg = createMessage(messageClass, req, true, true);
      }
      addRequestorReference(req, msg);
      for (IMessageCreationListener listener : instanceRequestListeners) {
         listener.onInstanceRequest(messageClass, (CLASSTYPE)msg, req, true);
      }
      return (CLASSTYPE)msg;
   }
   
   private boolean addRequestorReference(IMessageRequestor requestor, Message msg) {
      HashSet<IMessageRequestor> list = requestorReferenceMap.get(msg);
      if (list == null) {
         list = new HashSet<IMessageRequestor>(24);
         requestorReferenceMap.put(msg, list);
      }
      return list.add(requestor);
   }

   synchronized boolean removeRequestorReference(IMessageRequestor requestor, Message msg) {
      HashSet<IMessageRequestor> list = requestorReferenceMap.get(msg);
      if (list != null) {
         boolean result = list.remove(requestor);
         if (list.isEmpty()) {
            requestorReferenceMap.remove(msg);
            if (!msg.isDestroyed()) {
               if(!msg.isWriter()){
                  idToDataMap.remove(msg.getMessageId());
               }
               messageCollection.remove(msg);
            } else {
               OseeLog.log(MessageController.class, Level.WARNING,
                     String.format("%s is getting removed twice.", msg.getMessageName()), new Exception());
            }
         }
         return result;
      }
      return false;
   }

   @Override
   public <CLASSTYPE extends Message> int getReferenceCount(CLASSTYPE classtype) {
      if( requestorReferenceMap.containsKey(classtype)) {
         return requestorReferenceMap.get(classtype).size();
      } else {
         return 0;
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public <CLASSTYPE extends Message> CLASSTYPE findInstance(Class<CLASSTYPE> clazz, boolean writer) {
      if(writer){
         return (CLASSTYPE) messageCollection.findWriter(clazz);
      } else {
         return (CLASSTYPE) messageCollection.findReader(clazz);
      }
   }

   @Override
   public Collection<Message> getAllMessages() {
      return messageCollection.getAllMessages();
   }

   @Override
   public Collection<Message> getAllReaders() {
      return messageCollection.getAllReaders();
   }

   @Override
   public Collection<Message> getAllWriters() {
      return messageCollection.getAllWriters();
   }

   @Override
   public Collection<Message> getAllReaders(DataType type) {
      return messageCollection.getAllReaders(type);
   }

   @Override
   public Collection<Message> getAllWriters(DataType type) {
      return messageCollection.getAllWriters(type);
   }

   @Override
   public void init() throws Exception {
   }

   @Override
   public void publishMessages(boolean publish) {
   }

   @Override
   public boolean isPhysicalTypeAvailable(DataType physicalType) {
      return availableDataTypes.contains(physicalType);
   }

   @Override
   public IMessageRequestor createMessageRequestor(String name) {
      return new MessageRequestorImpl(name, this);
   }
   
   public Class<? extends Message> getMessageClass(String msgClass) throws ClassNotFoundException {
      return classLocator.findClass(msgClass).asSubclass(Message.class);
   }
   
   public void registerWriter(MessageDataWriter writer){
      CopyOnWriteNoIteratorList<MessageDataWriter> list = messageDataWriters.get(writer.getIOType());
      if(list == null){
         list = new CopyOnWriteNoIteratorList<>(MessageDataWriter.class);
         messageDataWriters.put(writer.getIOType(), list);
      }
      if(!list.contains(writer)){
         list.add(writer);
      }
      //TODO fix this, we should have this as part of the ENV configuration... or io configuration
      availableDataTypes.add(writer.getDataType());
   }
   
   public void unregisterWriter(MessageDataWriter writer){
      CopyOnWriteNoIteratorList<MessageDataWriter> list = messageDataWriters.get(writer.getIOType());
      if(list != null){
         list.remove(writer);
         if(list.length() == 0){
            messageDataWriters.remove(writer.getIOType());
         }
      }
   }
   
   public MessageDataUpdater registerDataReceiver(MessageDataReceiver receiver){
      messageDataReceivers.add(receiver);
      return new MessageDataUpdaterImpl();
   }
   
   public void unregisterDataReceiver(MessageDataReceiver receiver){
      messageDataReceivers.remove(receiver);
   }
   
   @Override
   public void update(MessageId id, ByteBuffer data) {
      MessageData messageData = getMessageDataReader(id);
      if(messageData != null){
         messageData.copyData(data);
         messageData.incrementActivityCount();
         notifyListenersOfUpdate(messageData);
      } else {
         if(debug){
            if(!printIdMessage.contains(id.hashCode())){
               printIdMessage.add(id.hashCode());
               System.out.printf("update[%s] - no destination\n", id);
            }
         }
      }
   }

   @Override
   //find all messages that have this is their active data source and notify their listeners
   public void notifyListenersOfUpdate(MessageData messageData){
      CopyOnWriteNoIteratorList<Message> msgs = mapper.getMessages(messageData);
      if(msgs != null){
         Message[] msgsArr = msgs.get();
         for(int i = 0; i < msgsArr.length; i++){
            msgsArr[i].notifyListeners(messageData, messageData.getType());
         }
      }
   }
   
   @Override
   public void addPostCreateMessageListener(IMessageCreationListener listener) {
      postCreation.add(listener);
   }

   @Override
   public void addPreCreateMessageListener(IMessageCreationListener listener) {
      preCreation.add(listener);
   }

   @Override
   public void addInstanceRequestListener(IMessageCreationListener listener) {
      instanceRequestListeners.add(listener);
   }

   private MessageData getMessageDataReader(MessageId id) {
      return idToDataMap.get(id);
   }

   @Override
   public void write(Message msg, DestinationInfo info) {
      MessageData data = msg.getDefaultMessageData();
      if (data.isWriter() && data.shouldSendData()) {
         CopyOnWriteNoIteratorList<MessageDataWriter> writers = messageDataWriters.get(data.getIOType());
         if(writers != null){
            MessageDataWriter[] writersArray = writers.get();
            data.performOverride();
            data.notifyPreSendListeners();
            for(int i = 0; i < writersArray.length; i++){
               writersArray[i].publishAndSend(data, info);
            }
            data.incrementSentCount();
            data.notifyPostSendListeners();
            //notify the writer listeners
            msg.notifyListeners(data, data.getType());
            //publish back to any readers
            if(data.isWrapbackEnabled()){
               update(msg.getMessageId(), data.getMem().getBuffer());
            }
         }
      }
   }
   
   @Override
   public void publish(Message msg) {
      if(!msg.isTurnedOff() && !msg.isDestroyed()){
         Message[] messages = mapper.getMessages(msg, msg.getMemType()).get();
         for(int i = 0; i < messages.length; i++){
            MessagePublishingHandler publisher = messagePublishingHandlers.get(messages[i].getDefaultMessageData().getIOType());
            if(publisher != null){
               publisher.publish(this, messages[i]);
            } else {
               defaultpublisher.publish(this, messages[i]);
            }
         }
      }
   }
   
   @Override
   public void publish(Message msg, PublishInfo info) {
      if(!msg.isTurnedOff() && !msg.isDestroyed()){
         Message[] messages = mapper.getMessages(msg, msg.getMemType()).get();
         for(int i = 0; i < messages.length; i++){
            MessagePublishingHandler publisher = messagePublishingHandlers.get(messages[i].getDefaultMessageData().getIOType());
            if(publisher != null){
               publisher.publish(this, messages[i], info);
            } else {
               defaultpublisher.publish(this, messages[i]);
            }
         }
      }
   }
   
   @Override
   public Set<DataType> getAvailableDataTypes() {
      return availableDataTypes;
   }

   @Override
   public void removePostCreateMessageListener(IMessageCreationListener listener) {
      postCreation.remove(listener);
   }

   @Override
   public void changeMessageRate(Message message, double newRate, double rate) {
      if(rate != 0.0){
         PeriodicPublishTask task = periodicPublish.get(rate, 1);
         task.remove(message);
      }
      if(newRate != 0.0){
         PeriodicPublishTask task = periodicPublish.get(newRate, 1);
         task.put(message);
      }
   }
   
//   @Override
//   public void publishAtFrameCompletion(Message msg) {
//      if(!msg.isTurnedOff()){
//         Message[] messages = mapper.getMessages(msg, msg.getMemType()).get();
//         for(int i = 0; i < messages.length; i++){
//            MessagePublishingHandler publisher = messagePublishingHandlers.get(messages[i].getDefaultMessageData().getType());
//            if(publisher != null){
//               publisher.publish(this, messages[i]);
//            } else {
//               defaultpublisher.publish(this, messages[i]);
//            }
//         }
//      }
//   }
   
   private static class MessageRequestorImpl implements IMessageRequestor {

      private final MessageController messageManager;
      private final HashSet<Message> messagesToDecrementReferenceCount = new HashSet<Message>();
      private final String name;

      MessageRequestorImpl(String name, MessageController messageManager) {
         this.name = name;
         this.messageManager = messageManager;
      }

      @Override
      public synchronized void dispose() {
         for (Message msg : messagesToDecrementReferenceCount) {
            try {
               messageManager.removeRequestorReference(this, msg);
            } catch (IllegalStateException ex){//we don't care if the message manager is disposed, it means we're shutting down
            } catch (Exception e) {
               OseeLog.log(MessageRequestorImpl.class, Level.SEVERE, "exception while removing requester reference for " +  msg.getName(), e);
            }
         }
         messagesToDecrementReferenceCount.clear();
      }

      public String toString()
      {
         return name;
      }

      @Override
      public Message getMessageWriter(String msgClass) throws TestException {
         Message msg = null;
         try {
            msg = getMessageWriter(messageManager.getMessageClass(msgClass));
         } catch (ClassCastException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         } catch (ClassNotFoundException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         }
         return msg;
      }
      
      @Override
      public synchronized <CLASSTYPE extends Message> CLASSTYPE getMessageWriter(Class<CLASSTYPE> type) throws TestException {
         CLASSTYPE msg = messageManager.getMessageWriter(this, type);
         messagesToDecrementReferenceCount.add(msg);
         return msg;
      }

      @Override
      public Message getMessageReader(String msgClass) throws TestException {
         Message msg = null;
         try {
            msg = getMessageReader(messageManager.getMessageClass(msgClass));
         } catch (ClassCastException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         } catch (ClassNotFoundException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         }
         return msg;
      }

      @Override
      public synchronized <CLASSTYPE extends Message> CLASSTYPE getMessageReader(Class<CLASSTYPE> type) throws TestException {
         CLASSTYPE msg = messageManager.getMessageReader(this, type);
         messagesToDecrementReferenceCount.add(msg);
         return msg;
      }

      @Override
      public synchronized void remove(Message message) throws TestException {
         if( messagesToDecrementReferenceCount.contains(message)){
            messageManager.removeRequestorReference(this, message);
            messagesToDecrementReferenceCount.remove(message);
         }
      }

      /**
       * @return the name
       */
      public String getName() {
         return name;
      }

      @Override
      public <CLASSTYPE extends Message> CLASSTYPE createMessageWriter(Class<CLASSTYPE> type) throws TestException {
         return messageManager.createMessage(type, this, true, false);
      }
      
   }
   
   private static class MessageCollection {

      private Map<Class<? extends Message>, Message> readerLookup;
      private Map<Class<? extends Message>, Message> writerLookup;
      
      public MessageCollection(){
         readerLookup = new HashMap<Class<? extends Message>, Message>();
         writerLookup = new HashMap<Class<? extends Message>, Message>();
      }
      
      public void add(Message message){
         if(message.isWriter()){
            writerLookup.put(message.getClass(), message);
         } else {
            readerLookup.put(message.getClass(), message);
         }
      }
      
      public void remove(Message message){
         if(message.isWriter()){
            writerLookup.remove(message.getClass());
         } else {
            readerLookup.remove(message.getClass());
         }
         message.destroy();
      }
      
      public Message findReader(Class<? extends Message> clazz){
         return readerLookup.get(clazz);
      }
      
      public Message findWriter(Class<? extends Message> clazz){
         return writerLookup.get(clazz);
      }
      
      public Collection<Message> getAllMessages() {
         ArrayList<Message> totalList = new ArrayList<Message>();
         totalList.addAll(getAllReaders());
         totalList.addAll(getAllWriters());
         return totalList;
      }

      public Collection<Message> getAllReaders() {
         return readerLookup.values();
      }

      public Collection<Message> getAllReaders(DataType type) {
         ArrayList<Message> filterList = new ArrayList<Message>();
         for(Message msg:getAllReaders()){
            if(msg.getActiveDataSource().getType().equals(type)){
               filterList.add(msg);
            }
         }
         return filterList;
      }

      public Collection<Message> getAllWriters() {
         return writerLookup.values();
      }

      public Collection<Message> getAllWriters(DataType type) {
         ArrayList<Message> filterList = new ArrayList<Message>();
         for(Message msg:getAllWriters()){
            if(msg.getActiveDataSource().getType().equals(type)){
               filterList.add(msg);
            }
         }
         return filterList;
      }
   }
   
   private class MessageDataUpdaterImpl implements MessageDataUpdater {

      @Override
      public void update(MessageId id, ByteBuffer data) {
         MessageController.this.update(id, data);
      }
      
   }

}
