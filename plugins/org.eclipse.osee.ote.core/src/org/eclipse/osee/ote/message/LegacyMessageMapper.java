package org.eclipse.osee.ote.message;

import java.util.List;
import java.util.Set;

import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

public interface LegacyMessageMapper {
   Set<DataType> getAvailableDataTypes(  Message message);

   CopyOnWriteNoIteratorList<Message> getMessages(Message message, DataType type);
   CopyOnWriteNoIteratorList<Message> getMessages(Message message);
   CopyOnWriteNoIteratorList<Message> getMessages(MessageData messageData);
   
   MessageData getMessageData(Message message, DataType type);
   List<MessageData> getAllMessageDatas(Message message);

   void addMessageTypeAssociation(Message message, DataType memType, Message messageToBeAdded);
   void updatePublicFieldReferences(Message message, DataType type);
   void cleanup(MessageData messageData);

   void addMessage(Message message);
   void removeMessage(Message message);
   
}
