package org.eclipse.osee.ote.message;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.enums.DataType;

public class TestMessageOne extends Message {

   public static final MessageId ID = new TestMessageId(TestMessageDataType.eth1, 1);
   
   public IntegerElement INT1;
   public IntegerElement INT2;
   public IntegerElement INT3;
   public IntegerElement INT4;
   
   
   public TestMessageOne() {
      super(ID, "TestMessageOne", new MessageData("TestMessageOne", "TestMessageOne", 16, 0, TestMessageDataType.eth1, null));
      
      INT1 = new IntegerElement(this, "INT1", getDefaultMessageData(), 0, 0, 7);
      INT2 = new IntegerElement(this, "INT2", getDefaultMessageData(), 1, 0, 7);
      INT3 = new IntegerElement(this, "INT3", getDefaultMessageData(), 2, 0, 7);
      INT4 = new IntegerElement(this, "INT4", getDefaultMessageData(), 3, 0, 7);
      addElements(INT1, INT2, INT3, INT4);
   }
   
   public Map<DataType, Class<? extends Message>[]> getAssociatedMessages(){
      Map<DataType, Class<? extends Message>[]> o = new LinkedHashMap<DataType, Class<? extends Message>[]>();
      o.put(TestMessageDataType.eth2, new Class[]{TestMessageTwo.class});
      o.put(TestMessageDataType.eth3, new Class[]{TestMessage3.class});
      return o;
   }

}
