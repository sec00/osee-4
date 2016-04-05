package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

public class BasicWriter implements MessageDataWriter {

      private IOType type;
      private DataType dataType;

      public BasicWriter(IOType type, DataType dataType){
         this.type = type;
         this.dataType = dataType;
      }
      
      @Override
      public IOType getIOType() {
         return type;
      }

      @Override
      public void publish(MessageData data) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void send() {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void publishAndSend(MessageData data, DestinationInfo info) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public DataType getDataType() {
         return dataType;
      }
      
   }