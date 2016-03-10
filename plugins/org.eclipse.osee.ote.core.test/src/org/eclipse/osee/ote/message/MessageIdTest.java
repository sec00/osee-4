package org.eclipse.osee.ote.message;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageIdTest {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void test() {
      Map<MessageId, Object> map = new HashMap<>();
      
      MessageId id1 = new IntegerMessageId(TestMessageDataType.eth1, 1);
      ModifiableIntegerMessageId idx = new ModifiableIntegerMessageId(TestMessageDataType.eth1, 0);
      
      map.put(id1, 1);
      
      idx.set(1);
      Assert.assertNotNull(map.get(idx));
      
      
      MessageId id2 = new StringMessageId(TestMessageDataType.eth2, "test");
      ModifiableStringMessageId idStringx = new ModifiableStringMessageId(TestMessageDataType.eth2, "");
      
      idStringx.set("test");
      
      map.put(id2, "test");
      
      Assert.assertNotNull(map.get(idStringx));
      
   }

}
