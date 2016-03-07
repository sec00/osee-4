package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

public interface MessageDataWriter {
   
   IOType getIOType();
   DataType getDataType();

   void publishAndSend(MessageData data, DestinationInfo info);
   void publish(MessageData data);
   void send();

}
