package org.eclipse.osee.ote.message;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.CharElement;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.ElementGroup;
import org.eclipse.osee.ote.message.elements.EmptyEnum_Element;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.FixedPointElement;
import org.eclipse.osee.ote.message.elements.Float32Element;
import org.eclipse.osee.ote.message.elements.Float64Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.LongIntegerElement;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingCharElement;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingEmptyEnumElement;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingEnumeratedElement;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingFixedPointElement;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingFloat32Element;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingFloat64Element;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingIntegerElement;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingLongIntegerElement;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingRecordElement;
import org.eclipse.osee.ote.message.enums.DataType;

class LegacyMessageMapperService implements LegacyMessageMapper {

   
   private ConcurrentHashMap<Message, MessageStorageLookup> messageLookup;
   private ConcurrentHashMap<MessageData, MessageDataStorageLookup> messageDataLookup;
   private List<ElementNoMappingProvider<?>> nonMappingProviders;
   
   public LegacyMessageMapperService(){
      messageLookup = new ConcurrentHashMap<Message, MessageStorageLookup>();
      messageDataLookup = new ConcurrentHashMap<MessageData, MessageDataStorageLookup>();
      nonMappingProviders = new CopyOnWriteArrayList<>();
      nonMappingProviders.add(new GenericNonMappingProvider<IntegerElement>(IntegerElement.class) {
         @Override
         public IntegerElement getNonMappingElement(IntegerElement elementGettingReplaced) {
            return new NonMappingIntegerElement(elementGettingReplaced);
         }
      });
      nonMappingProviders.add(new GenericNonMappingProvider<CharElement>(CharElement.class) {
         @Override
         public CharElement getNonMappingElement(CharElement elementGettingReplaced) {
            return new NonMappingCharElement(elementGettingReplaced);
         }
      });
      nonMappingProviders.add(new GenericNonMappingProvider<EmptyEnum_Element>(EmptyEnum_Element.class) {
         @Override
         public EmptyEnum_Element getNonMappingElement(EmptyEnum_Element elementGettingReplaced) {
            return new NonMappingEmptyEnumElement(elementGettingReplaced);
         }
      });
      nonMappingProviders.add(new GenericNonMappingProvider<FixedPointElement>(FixedPointElement.class) {
         @Override
         public FixedPointElement getNonMappingElement(FixedPointElement elementGettingReplaced) {
            return new NonMappingFixedPointElement(elementGettingReplaced);
         }
      });
      nonMappingProviders.add(new GenericNonMappingProvider<Float32Element>(Float32Element.class) {
         @Override
         public Float32Element getNonMappingElement(Float32Element elementGettingReplaced) {
            return new NonMappingFloat32Element(elementGettingReplaced);
         }
      });
      nonMappingProviders.add(new GenericNonMappingProvider<Float64Element>(Float64Element.class) {
         @Override
         public Float64Element getNonMappingElement(Float64Element elementGettingReplaced) {
            return new NonMappingFloat64Element(elementGettingReplaced);
         }
      });
      nonMappingProviders.add(new GenericNonMappingProvider<LongIntegerElement>(LongIntegerElement.class) {
         @Override
         public LongIntegerElement getNonMappingElement(LongIntegerElement elementGettingReplaced) {
            return new NonMappingLongIntegerElement(elementGettingReplaced);
         }
      });
      nonMappingProviders.add(new GenericNonMappingProvider<RecordElement>(RecordElement.class) {
         @Override
         public RecordElement getNonMappingElement(RecordElement elementGettingReplaced) {
            return new NonMappingRecordElement(elementGettingReplaced);
         }
      });
      nonMappingProviders.add(new EnumeratedElementNonMappingProvider(EnumeratedElement.class));
   }
   
   @Override
   public Set<DataType> getAvailableDataTypes(Message message) {
      return getMessageStorage(message).getAvailableDataTypes();
   }


   private MessageStorageLookup getMessageStorage(Message message){
      MessageStorageLookup lookup = messageLookup.get(message);
      if(lookup == null){
         lookup = new MessageStorageLookup();
         messageLookup.put(message, lookup);
      }
      return lookup;
   }
   
   private MessageDataStorageLookup getMessageDataStorage(MessageData data){
      MessageDataStorageLookup lookup = messageDataLookup.get(data);
      if(lookup == null){
         lookup = new MessageDataStorageLookup();
         messageDataLookup.put(data, lookup);
      }
      return lookup;
   }
   
   @Override
   public CopyOnWriteNoIteratorList<Message> getMessages(Message message) {
      
      return null;
   }

   @Override
   public CopyOnWriteNoIteratorList<Message> getMessages(MessageData messageData) {
      MessageDataStorageLookup lookup = messageDataLookup.get(messageData);
      if(lookup == null){
//         return null;
         System.out.println("no messages for " + messageData.getName());
         return new CopyOnWriteNoIteratorList<>(Message.class);
      }
      return lookup.getMessages();
   }

   @Override
   public MessageData getMessageData(Message message, DataType type) {
      MessageStorageLookup storage = getMessageStorage(message);
      return storage.getMessageData(type);
   }


   @Override
   public void addMessageTypeAssociation(Message message, DataType memType, Message messageToBeAdded) {
      MessageStorageLookup storage = getMessageStorage(message);
      storage.add(memType, messageToBeAdded);
      MessageDataStorageLookup dataStorage = getMessageDataStorage(messageToBeAdded.getDefaultMessageData());
      dataStorage.add(message);
   }
   
   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public void updatePublicFieldReferences(Message message, DataType type) {
      if(message.getDefaultMessageData().getType().equals(type)){
         HashMap<String, Element> elementMap = message.getElementMap();
         List<Field> masterMessage = new ArrayList<Field>();
         Field[] fields = message.getClass().getFields();
         for(Field field:fields){
            Class clazz = field.getType();
            if(Element.class.isAssignableFrom(clazz)){
               masterMessage.add(field);
            }
         }
         for(Field targetField:masterMessage){
            try{
               targetField.setAccessible(true);
               targetField.set(message,  elementMap.get(targetField.getName()));
            } catch (IllegalAccessException ex){
               ex.printStackTrace();
            } finally {
               targetField.setAccessible(false);
            }
         }
      } else {
         MessageStorageLookup storage = getMessageStorage(message);
         Message[] messages = storage.getMessages(type).get();

         List<Field> masterMessage = new ArrayList<Field>();
         Field[] fields = message.getClass().getFields();
         for(Field field:fields){
            Class clazz = field.getType();
            if(Element.class.isAssignableFrom(clazz)){
               masterMessage.add(field);
            }
         }
         Map<String, List<Element>> mappedElements = new HashMap<String, List<Element>>();
         if(messages != null){
            for(int i = 0; i < messages.length; i++){
               Message mappedMessage = messages[i];
               fields = message.getClass().getFields();
               for(Element field:mappedMessage.getElements()){
                  List<Element> elements = mappedElements.get(field.getName());
                  if(elements == null){
                     elements = new ArrayList<>();
                     mappedElements.put(field.getName(), elements);
                  }
                  elements.add(field);
               }
            }
         }

         for(Field targetField:masterMessage){
            try{
               targetField.setAccessible(true);
               List<Element> assigneTo = mappedElements.get(targetField.getName());
               if(assigneTo == null) {
                  targetField.set(message, getNoMapping(targetField.getType(), (Element)targetField.get(message)));
               } else if(assigneTo.size() == 1){
                  targetField.set(message,  assigneTo.get(0));
               } else if (assigneTo.size() > 1){
                  ElementGroup group = createElementGroup(message, targetField.getType());
                  if( group == null ) {
                     OseeLog.log(getClass(), Level.SEVERE, message.getMessageName() + "." + targetField.getName() +  " must implement type group for " + targetField.getType().getName());
                  } else {
                     group.setElementList(assigneTo);
                     targetField.set(message, group);
                  }
               } else {
                  OseeLog.log(getClass(), Level.SEVERE, String.format("Unexpected Error, %s should have been found but wasn't.", targetField.getName()));
               }
            } catch (IllegalAccessException ex){
               System.out.println("Failed to map " +targetField.getName());
               ex.printStackTrace();
            } finally {
               targetField.setAccessible(false);
            }
         }
      }
   }

   @SuppressWarnings("rawtypes")
   private ElementGroup createElementGroup(Message message, Class<?> type) {
      return null;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private Object getNoMapping(Class<?> type, Element object) {
      for(ElementNoMappingProvider provider:nonMappingProviders){
         if(provider.providesType(object.getClass())){
            return provider.getNonMappingElement(object);
         }
      }
      throw new IllegalArgumentException(String.format("no mapping type found for %s", type.getName()));
   }

   @Override
   public void cleanup(MessageData messageData) {
      // TODO Auto-generated method stub

   }
   
   private static class MessageStorageLookup {
      private Map<DataType, Pair<CopyOnWriteNoIteratorList<Message>, MessageData>> dataLookup;
      private List<MessageData> allDatas;
      
      public MessageStorageLookup(){
         dataLookup = new HashMap<DataType, Pair<CopyOnWriteNoIteratorList<Message>, MessageData>>();
         allDatas = new ArrayList<>();
      }
      
      public CopyOnWriteNoIteratorList<Message> getMessages(DataType type) {
         Pair<CopyOnWriteNoIteratorList<Message>, MessageData> pair = dataLookup.get(type);
         if(pair != null){
            return pair.getFirst();
         } else {
            return null;
         }
      }

      MessageData getMessageData(DataType type) {
         Pair<CopyOnWriteNoIteratorList<Message>, MessageData> pair = dataLookup.get(type);
         if(pair != null){
            return pair.getSecond();
         } else {
            return null;
         }
      }

      Set<DataType> getAvailableDataTypes() {
         return dataLookup.keySet();
      }
      
      void add(DataType type, Message messageToBeAdded){
         Pair<CopyOnWriteNoIteratorList<Message>, MessageData> pair = dataLookup.get(type);
         if(pair == null){
            CopyOnWriteNoIteratorList<Message> messages = new CopyOnWriteNoIteratorList<Message>(Message.class);
            pair = new Pair<CopyOnWriteNoIteratorList<Message>, MessageData>(messages, messageToBeAdded.getDefaultMessageData());
            dataLookup.put(type, pair);
         } 
         if(!pair.getFirst().contains(messageToBeAdded)){
            pair.getFirst().add(messageToBeAdded);
            MessageData existingData = pair.getSecond();
            if(existingData == null){
               pair.setSecond(messageToBeAdded.getDefaultMessageData());
            } else if(!existingData.equals(messageToBeAdded)){
               MessageDataContainer container = null;
               if(existingData instanceof MessageDataContainer){
                  container = (MessageDataContainer)existingData;
               } else {
                  container = new MessageDataContainer();
                  container.add(existingData);
               }
               container.add(messageToBeAdded.getDefaultMessageData());            
            }
         }
         allDatas.add(messageToBeAdded.getDefaultMessageData());
      }

      public void remove(DataType type, Message message) {
         Pair<CopyOnWriteNoIteratorList<Message>, MessageData> pair = dataLookup.get(type);
         if(pair != null){
            pair.getFirst().remove(message);
            if(pair.getSecond() instanceof MessageDataContainer){
               MessageDataContainer container = (MessageDataContainer)pair.getSecond();
               container.remove(message.getDefaultMessageData());
            }
            if(pair.getFirst().length() == 0){
               pair.setSecond(null);
               dataLookup.remove(type);
            }
         } 
         allDatas.remove(message.getDefaultMessageData());
      }

      public List<MessageData> getAllMessageDatas() {
         return allDatas;
      }      
   }
   
   private static abstract class GenericNonMappingProvider<T> implements ElementNoMappingProvider<T> {

      private Class<T> clazz;

      public GenericNonMappingProvider(Class<T> clazz) {
         this.clazz = clazz;
      }
      
      @Override
      public boolean providesType(Class<?> clazz) {
         return clazz.isAssignableFrom(this.clazz);
      }
   }
   
   @SuppressWarnings("rawtypes")
   private static class EnumeratedElementNonMappingProvider implements ElementNoMappingProvider<EnumeratedElement> {

      private Class<EnumeratedElement> clazz;

      public EnumeratedElementNonMappingProvider(Class<EnumeratedElement> clazz) {
         this.clazz = clazz;
      }
      
      @Override
      public boolean providesType(Class<?> clazz) {
         return clazz.isAssignableFrom(this.clazz);
      }

      @SuppressWarnings("unchecked")
      @Override
      public EnumeratedElement getNonMappingElement(EnumeratedElement elementGettingReplaced) {
         return new NonMappingEnumeratedElement<>(elementGettingReplaced);
      }
   }
   
   private static class MessageDataStorageLookup {

      private CopyOnWriteNoIteratorList<Message> messages = new CopyOnWriteNoIteratorList<>(Message.class);
      
      public void add(Message message) {
         messages.add(message);
      }

      public CopyOnWriteNoIteratorList<Message> getMessages() {
         return messages;
      }
      
   }

   @Override
   public CopyOnWriteNoIteratorList<Message> getMessages(Message message, DataType type) {
      MessageStorageLookup storage = getMessageStorage(message);
      return storage.getMessages(type);
   }

   @Override
   public List<MessageData> getAllMessageDatas(Message message) {
      return getMessageStorage(message).getAllMessageDatas();
   }

   @Override
   public void addMessage(Message message) {
      MessageStorageLookup messageStorageLookup = messageLookup.get(message);
      if(messageStorageLookup == null){
         messageStorageLookup = new MessageStorageLookup();
         messageStorageLookup.add(message.getDefaultMessageData().getType(), message);
         messageLookup.put(message, messageStorageLookup);
      }
      MessageDataStorageLookup messageDataStorageLookup = messageDataLookup.get(message.getDefaultMessageData());
      if(messageDataStorageLookup == null){
         messageDataStorageLookup = new MessageDataStorageLookup();
         messageDataStorageLookup.add(message);
         messageDataLookup.put(message.getDefaultMessageData(), messageDataStorageLookup);
      }
   }
   
   @Override
   public void removeMessage(Message message) {
      MessageDataStorageLookup messageDataStorageLookup = messageDataLookup.get(message.getDefaultMessageData());
      if(messageDataStorageLookup != null){
         Message[] msgs = messageDataStorageLookup.getMessages().get();
         for(int i = 0; i < msgs.length; i++){
            MessageStorageLookup messageStorageLookup = messageLookup.get(message);
            if(messageStorageLookup != null){
               messageStorageLookup.remove(message.getDefaultMessageData().getType(), message);
            }
         }
            
      }
      messageLookup.remove(message);
      messageDataLookup.remove(message.getDefaultMessageData());
   }



}
