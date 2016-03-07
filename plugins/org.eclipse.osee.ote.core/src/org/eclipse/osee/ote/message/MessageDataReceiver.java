package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.enums.DataType;

public interface MessageDataReceiver {
   
   IOType getIOType();
   DataType getDataType();

}
