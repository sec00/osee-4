package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

public class BinaryRecorderFilterCallback implements BinaryRecorderCallback {

   private MessageCaptureFilter filter;

   public BinaryRecorderFilterCallback(MessageCaptureFilter filter) {
      this.filter = filter;
   }
   
   @Override
   public void onDataAvailable(MessageData data, DataType type) {
      filter.isDone();
   }

}
