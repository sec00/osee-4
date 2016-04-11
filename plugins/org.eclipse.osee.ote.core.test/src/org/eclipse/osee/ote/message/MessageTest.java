package org.eclipse.osee.ote.message;

import java.util.Collection;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageTest {

   private MessageController messageManager;
   private IMessageRequestor req;

   @Before
   public void setUp() throws Exception {
      messageManager = new MessageController(null, null, null);
      req = messageManager.createMessageRequestor("tests");
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void dataWithNew() {
      TestMessageOne msg = new TestMessageOne();
      MessageData data = msg.getActiveDataSource();
      Assert.assertNotNull(data);
      Assert.assertEquals(msg.getMessageName(), data.getName());
   }
   
   @Test
   public void dataWithMapping(){
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth2, TestMessageDataType.eth2));
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth3, TestMessageDataType.eth3));
      
      TestMessageOne msg1 = req.getMessageWriter(TestMessageOne.class);
      TestMessageTwo msg2 = req.getMessageWriter(TestMessageTwo.class);
      TestMessage3 msg3 = req.getMessageWriter(TestMessage3.class);
      
      msg1.setMemSource(TestMessageDataType.eth2);
      
      Assert.assertEquals(msg2.getDefaultMessageData(), msg1.getActiveDataSource());
      Assert.assertTrue(msg2.getDefaultMessageData().getMessages().contains(msg1));
      // should have one for self and one for TestMessageOne which it is mapped to
      Assert.assertEquals(2, msg2.getDefaultMessageData().getMessages().size());
      
      msg1.setMemSource(TestMessageDataType.eth3);

      Assert.assertEquals(msg3.getDefaultMessageData(), msg1.getActiveDataSource());
      Assert.assertTrue(msg3.getDefaultMessageData().getMessages().contains(msg1));
      // should have one for self and one for TestMessageOne which it is mapped to
      Assert.assertEquals(2, msg3.getDefaultMessageData().getMessages().size());
      
      Assert.assertEquals(msg2.getDefaultMessageData(), msg1.getMemSource(TestMessageDataType.eth2).iterator().next());
      Assert.assertEquals(msg3.getDefaultMessageData(), msg1.getMemSource(TestMessageDataType.eth3).iterator().next());
      
      Assert.assertEquals(msg2.getDefaultMessageData(), msg2.getActiveDataSource());
      Assert.assertEquals(msg3.getDefaultMessageData(), msg3.getActiveDataSource());

   }
   
   @Test
   public void findElements(){
      
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth2, TestMessageDataType.eth2));
      
      TestMessageOne msg1 = req.getMessageWriter(TestMessageOne.class);
      TestMessageTwo msg2 = req.getMessageWriter(TestMessageTwo.class);
      
      msg1.setMemSource(TestMessageDataType.eth1);
      Assert.assertEquals(msg1.INT1, msg1.getElement("INT1"));
      Assert.assertEquals(msg1.INT2, msg1.getElement("INT2"));
      Assert.assertEquals(msg1.INT3, msg1.getElement("INT3"));
      Assert.assertEquals(msg1.INT4, msg1.getElement("INT4"));

      msg1.setMemSource(TestMessageDataType.eth2);

      Assert.assertEquals(msg2.INT1, msg1.getElement("INT1"));
      Assert.assertEquals(msg2.INT2, msg1.getElement("INT2"));
      Assert.assertEquals(msg2.INT3, msg1.getElement("INT3"));
      Assert.assertEquals(msg2.INT4, msg1.getElement("INT4"));
      
   }
   
   @Test
   public void getElements(){
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth2, TestMessageDataType.eth2));
      
      TestMessageOne msg1 = req.getMessageWriter(TestMessageOne.class);
      TestMessageTwo msg2 = req.getMessageWriter(TestMessageTwo.class);
      
      
      msg1.setMemSource(TestMessageDataType.eth1);
      Collection<Element> elements = msg1.getElements(TestMessageDataType.eth1);
      Assert.assertEquals(4, elements.size());
      Assert.assertTrue(elements.contains(msg1.INT1));
      Assert.assertTrue(elements.contains(msg1.INT2));
      Assert.assertTrue(elements.contains(msg1.INT3));
      Assert.assertTrue(elements.contains(msg1.INT4));
      Assert.assertTrue(!elements.contains(msg2.INT1));
      Assert.assertTrue(!elements.contains(msg2.INT2));
      Assert.assertTrue(!elements.contains(msg2.INT3));
      Assert.assertTrue(!elements.contains(msg2.INT4));

      elements = msg1.getElements(TestMessageDataType.eth2);
      Assert.assertEquals(4, elements.size());
      Assert.assertTrue(!elements.contains(msg1.INT1));
      Assert.assertTrue(!elements.contains(msg1.INT2));
      Assert.assertTrue(!elements.contains(msg1.INT3));
      Assert.assertTrue(!elements.contains(msg1.INT4));
      Assert.assertTrue(elements.contains(msg2.INT1));
      Assert.assertTrue(elements.contains(msg2.INT2));
      Assert.assertTrue(elements.contains(msg2.INT3));
      Assert.assertTrue(elements.contains(msg2.INT4));
   }
   
   

}
