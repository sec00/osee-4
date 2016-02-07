package org.eclipse.osee.ote.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.message.data.MessageData;

public class MessageDataContainer extends MessageData {

   List<MessageData> messageDatas;
   
   public MessageDataContainer() {
      super(new byte[0], 0, 0); 
      messageDatas = new ArrayList<MessageData>();
   }

   void add(MessageData existingData) {
      if(!messageDatas.contains(existingData)){
         messageDatas.add(existingData);
      }
   }

   public void remove(MessageData defaultMessageData) {
      messageDatas.remove(defaultMessageData);
   }

}
