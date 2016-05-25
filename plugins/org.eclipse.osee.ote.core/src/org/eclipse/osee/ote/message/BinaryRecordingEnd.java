package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.LongIntegerElement;

public class BinaryRecordingEnd extends Message {

   public static final MessageId ID = new IntegerMessageId(BasicMessageTypes.RECORDER_BYTES, 2);
   
   public LongIntegerElement TIME;
   
   public BinaryRecordingEnd() {
      super(ID, BinaryRecordingEnd.class.getSimpleName(), new MessageData("","",8,0,BasicMessageTypes.RECORDER_BYTES,BasicIOTypes.none));
      TIME = new LongIntegerElement(this, "TIME", getDefaultMessageData(), 0, 64);
      addElements(TIME);
   }

}
