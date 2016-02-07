package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

public interface MessageDataWriter {
//   /**
//    * This method should be implemented as a synchronous one-shot write
//    */
//   void write(IDestination destination, ISource source, DataStoreItem data);
//
//   /**
//    * This method can be synchronous or asynchronous, there is no guarantee that when it
//    * returns the message data has been sent.  Of course it should be sent very shortly after
//    */
//   void write(IDestination destination, ISource source, MessageData data);
//
//   /**
//    * This method can be synchronous or asynchronous, there is no guarantee that when it
//    * returns the message data's have been sent.  It is expected that the sender will aggregate the
//    * list of messages if possible.
//    */
//   void write(IDestination destination, ISource source, Collection<MessageData> data);


//   boolean accept(String topic);
   
//   String getNamespace();
   
   IOType getDataType();
   DataType getType();

   void publishAndSend(MessageData data, DestinationInfo info);
   void publish(MessageData data);
   void send();

}
