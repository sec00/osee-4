package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;

public interface MessageDataWriter {
   
   IOType getIOType();
   void publishAndSend(MessageData data, DestinationInfo info);
   void publish(MessageData data);
   void send();

}
