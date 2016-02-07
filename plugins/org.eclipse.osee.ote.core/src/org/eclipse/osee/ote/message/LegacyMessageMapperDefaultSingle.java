package org.eclipse.osee.ote.message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

class LegacyMessageMapperDefaultSingle implements LegacyMessageMapper {

   private Set<DataType> set;
   private CopyOnWriteNoIteratorList<Message> messages;
   private List<MessageData> datas;
   
   @Override
   public Set<DataType> getAvailableDataTypes(Message message) {
      if(set == null){
         set = new HashSet<DataType>();
         set.add(message.getDefaultMessageData().getType());
      }
      return set;
   }



   @Override
   public CopyOnWriteNoIteratorList<Message> getMessages(Message message) {
      if(messages == null){
         messages = new CopyOnWriteNoIteratorList<Message>(Message.class);
      }
      return messages;
   }

   @Override
   public CopyOnWriteNoIteratorList<Message> getMessages(MessageData messageData) {
      if(messages == null){
         messages = new CopyOnWriteNoIteratorList<Message>(Message.class);
      }
      return messages;
   }

   @Override
   public MessageData getMessageData(Message message, DataType type) {
      if(message.getDefaultMessageData().getType().equals(type)){
         return message.getDefaultMessageData();
      } else {
         return null;
      }
   }

  

   @Override
   public void addMessageTypeAssociation(Message message, DataType memType, Message messageToBeAdded) {

   }

   @Override
   public void updatePublicFieldReferences(Message message, DataType type) {

   }

   @Override
   public void cleanup(MessageData messageData) {
      datas.clear();
      datas=null;
      set.clear();
      set=null;
   }



   @Override
   public CopyOnWriteNoIteratorList<Message> getMessages(Message message, DataType type) {
      // TODO Auto-generated method stub
      return null;
   }



   @Override
   public List<MessageData> getAllMessageDatas(Message message) {
      // TODO Auto-generated method stub
      return null;
   }



   @Override
   public void addMessage(Message message) {
      // TODO Auto-generated method stub
      
   }



   @Override
   public void removeMessage(Message message) {
      // TODO Auto-generated method stub
      
   }

}
