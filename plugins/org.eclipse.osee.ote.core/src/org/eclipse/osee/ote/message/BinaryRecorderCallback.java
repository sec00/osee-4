package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

public interface BinaryRecorderCallback {

   void onDataAvailable(MessageData data, DataType type);
   
}
