package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

public abstract class OTEMessageListener<T extends Message> implements IOSEEMessageListener {

   private T message;

   public OTEMessageListener(T message) {
      this.message = message;
   }

   public abstract void newData(T message);

   @Override
   public final void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
      if(message == null){
         newData(message);
      } else if(message.getMemType().equals(type)){
         newData(message);
      }
   }

   @Override
   public void onInitListener() throws MessageSystemException {

   }
   
   public final void register(){
      if(message != null){
         message.addListener(this);
      }
   }
   
   public final void unregister(){
      if(message != null) {
         message.removeListener(this);
      }
   }

}
