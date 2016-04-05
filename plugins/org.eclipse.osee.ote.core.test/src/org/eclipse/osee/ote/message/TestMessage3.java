package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.IntegerElement;

public class TestMessage3 extends Message {

   public static final MessageId ID = new TestMessageId(TestMessageDataType.eth3, 1);
   
   
   public IntegerElement INT1;
   public IntegerElement INT2;
//   public IntegerElement INT3;
   public IntegerElement INT4;
   
   
   public TestMessage3() {
      super(ID, "TestMessage3", new MessageData("TestMessage3", "TestMessage3", 4, 0, TestMessageDataType.eth3, null));
      
      INT1 = new IntegerElement(this, "INT1", getDefaultMessageData(), 0, 0, 7);
      INT2 = new IntegerElement(this, "INT2", getDefaultMessageData(), 1, 0, 7);
//      INT3 = new IntegerElement(this, "INT3", getDefaultMessageData(), 2, 0, 7);
      INT4 = new IntegerElement(this, "INT4", getDefaultMessageData(), 3, 0, 7);
      addElements(INT1, INT2, INT4);
   }

}
