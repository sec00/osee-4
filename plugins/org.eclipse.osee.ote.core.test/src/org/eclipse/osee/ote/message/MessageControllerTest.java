package org.eclipse.osee.ote.message;


import java.nio.ByteBuffer;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MessageControllerTest {
   
   private IMessageManager messageManager;
   private IMessageRequestor req; 
   
   @Before
   public void setupTest(){
      messageManager = new MessageController(new BasicClassLocator(this.getClass().getClassLoader()), null);
      req = messageManager.createMessageRequestor("tests");
   }
   
   @After
   public void tearDownTest(){
      req.dispose();
      messageManager.destroy();
   }
   
   @Test
   public void basicSetup(){
      Assert.assertEquals(0, messageManager.getAllMessages().size());
      Assert.assertEquals(0, messageManager.getAllReaders().size());
      Assert.assertEquals(0, messageManager.getAllWriters().size());
   }
   
   @Test
   public void messageChecks(){
      MessageValidator validator = new MessageValidator();
      StringBuilder sb = new StringBuilder();
      boolean result;
      result = validator.validate(new TestMessageOne(), sb);
      Assert.assertTrue(sb.toString(), result);
      result = validator.validate(new TestMessageTwo(), sb);
      Assert.assertTrue(sb.toString(), result);
      result = validator.validate(new TestMessage3(), sb);
      Assert.assertTrue(sb.toString(), result);
      
   }
   
   @Test
   public void basicCreation(){
      
      Message messageReader = req.getMessageReader(TestMessageOne.class);
      Assert.assertNotNull(messageReader);
      Assert.assertEquals(false, messageReader.isWriter());
      Assert.assertNotNull(messageReader.getActiveDataSource());
      
      Message messageWriter = req.getMessageWriter(TestMessageOne.class);
      Assert.assertNotNull(messageWriter);
      Assert.assertEquals(true, messageWriter.isWriter());
      Assert.assertNotNull(messageWriter.getActiveDataSource());
      
      Assert.assertNotEquals(messageReader, messageWriter);
      Assert.assertNotEquals(messageReader.getActiveDataSource(), messageWriter.getActiveDataSource());
   }
   
   @Test
   public void basicMapping(){
      
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth2, TestMessageDataType.eth2));
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth3, TestMessageDataType.eth3));
      
      TestMessageOne msg1 = req.getMessageWriter(TestMessageOne.class);
      TestMessageTwo msg2 = req.getMessageWriter(TestMessageTwo.class);
      
            
      msg1.setMemSource(TestMessageType.eth1);
      
      IntegerElement org1 = msg1.INT1;
      IntegerElement org2 = msg1.INT2;
      IntegerElement org3 = msg1.INT3;
      IntegerElement org4 = msg1.INT4;
      
      msg1.setMemSource(TestMessageType.eth2);
      MessageData data = msg1.getActiveDataSource();
      Assert.assertEquals("TestMessageTwo", data.getName());
      
      Assert.assertEquals(msg2.INT1, msg1.INT1);
      Assert.assertEquals(msg2.INT2, msg1.INT2);
      Assert.assertEquals(msg2.INT3, msg1.INT3);
      Assert.assertEquals(msg2.INT4, msg1.INT4);
      msg1.setMemSource(TestMessageType.eth1);
      Assert.assertNotEquals(msg2.INT1, msg1.INT1);
      Assert.assertNotEquals(msg2.INT2, msg1.INT2);
      Assert.assertNotEquals(msg2.INT3, msg1.INT3);
      Assert.assertNotEquals(msg2.INT4, msg1.INT4);
      
      Assert.assertEquals(org1, msg1.INT1);
      Assert.assertEquals(org2, msg1.INT2);
      Assert.assertEquals(org3, msg1.INT3);
      Assert.assertEquals(org4, msg1.INT4);
   }
   
   @Test
   public void referenceCounting(){
      IMessageRequestor req2 = messageManager.createMessageRequestor("tests");
      IMessageRequestor req3 = messageManager.createMessageRequestor("tests");
      Assert.assertEquals(0, messageManager.getAllMessages().size());
      TestMessageOne msg1 = req.getMessageWriter(TestMessageOne.class);
      Assert.assertEquals(1, messageManager.getReferenceCount(msg1));
      Assert.assertEquals(1, messageManager.getAllMessages().size());
      TestMessageOne msg2 = req2.getMessageWriter(TestMessageOne.class);
      Assert.assertEquals(2, messageManager.getReferenceCount(msg1));
      Assert.assertEquals(1, messageManager.getAllMessages().size());
      TestMessageOne msg3 = req3.getMessageWriter(TestMessageOne.class);
      Assert.assertEquals(3, messageManager.getReferenceCount(msg1));
      Assert.assertEquals(1, messageManager.getAllMessages().size());
      req.remove(msg1);
      Assert.assertEquals(2, messageManager.getReferenceCount(msg1));
      Assert.assertEquals(1, messageManager.getAllMessages().size());
      req3.remove(msg1);
      Assert.assertEquals(1, messageManager.getReferenceCount(msg1));
      Assert.assertEquals(1, messageManager.getAllMessages().size());
      req2.remove(msg1);
      Assert.assertEquals(0, messageManager.getReferenceCount(msg1));
      Assert.assertNull(messageManager.findInstance(TestMessageOne.class, true));
      
      Assert.assertEquals(0, messageManager.getAllMessages().size());
      
      
      msg1 = req.getMessageWriter(TestMessageOne.class);
      Assert.assertEquals(1, messageManager.getReferenceCount(msg1));
      req.dispose();
      Assert.assertEquals(0, messageManager.getReferenceCount(msg1));
      Assert.assertEquals(0, messageManager.getAllMessages().size());
      Assert.assertTrue(msg1.isDestroyed());
   }
   
   @Test
   public void getFromString(){
      TestMessageOne one = (TestMessageOne)req.getMessageReader(TestMessageOne.class.getName());
      TestMessageOne two = (TestMessageOne)req.getMessageWriter(TestMessageOne.class.getName());
      Assert.assertNotNull(one);
      Assert.assertNotNull(two);
      
   }
   
   @Ignore
   @Test
   public void multiElementMapping(){
      
      Assert.fail("implement multi element mapping?");
   }
   
   @Test
   public void noElementMapping(){
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth2, TestMessageDataType.eth2));
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth3, TestMessageDataType.eth3));
      
      TestMessageOne msg1 = req.getMessageWriter(TestMessageOne.class);
      msg1.setMemSource(TestMessageType.eth3);
      boolean gotNonMappingException = false;
      try{
         msg1.INT3.setValue(1);
      } catch (MessageSystemException ex){
         gotNonMappingException = true;
      } catch (Throwable th){
         Assert.fail(th.getMessage());
      }
      Assert.assertTrue("Failed to get the non mapping exception", gotNonMappingException);
   }
   
   @Test
   public void creationListeners(){
      MessageCreationListenerCounter counter = new MessageCreationListenerCounter();
      messageManager.addInstanceRequestListener(counter);
      messageManager.addPostCreateMessageListener(counter);
      messageManager.addPreCreateMessageListener(counter);
   
      req.getMessageReader(TestMessageOne.class);
      Assert.assertEquals(1, counter.preCreate);
      Assert.assertEquals(1, counter.postCreate);
      Assert.assertEquals(1, counter.instanceRequest);
      Assert.assertEquals(1, counter.reader);
      Assert.assertEquals(0, counter.writer);
      req.getMessageReader(TestMessageOne.class);
      Assert.assertEquals(1, counter.preCreate);
      Assert.assertEquals(1, counter.postCreate);
      Assert.assertEquals(2, counter.instanceRequest);
      Assert.assertEquals(1, counter.reader);
      Assert.assertEquals(0, counter.writer);
      req.getMessageReader(TestMessageOne.class);
      Assert.assertEquals(1, counter.preCreate);
      Assert.assertEquals(1, counter.postCreate);
      Assert.assertEquals(3, counter.instanceRequest);
      Assert.assertEquals(1, counter.reader);
      Assert.assertEquals(0, counter.writer);
      req.getMessageWriter(TestMessageOne.class);
      Assert.assertEquals(2, counter.preCreate);
      Assert.assertEquals(2, counter.postCreate);
      Assert.assertEquals(4, counter.instanceRequest);
      Assert.assertEquals(1, counter.reader);
      Assert.assertEquals(1, counter.writer);
   }
   
   @Ignore
   @Test
   public void messagePublishing(){
      
      TestMessageOne msg = req.getMessageWriter(TestMessageOne.class);
      messageManager.publish(msg);

   }
   
   @Test
   public void messageReceiving(){
      TestMessageOne one = req.getMessageReader(TestMessageOne.class);
      UpdateCounter updateCounter = new UpdateCounter();
      one.addListener(updateCounter);
      ByteBuffer buffer = ByteBuffer.allocate(4);
      while(buffer.hasRemaining()){
         buffer.put((byte)0xFF);
      }
      buffer.flip();
      messageManager.update(TestMessageOne.ID, buffer);
      Assert.assertEquals(1, updateCounter.count);
      messageManager.update(TestMessageOne.ID, buffer);
      Assert.assertEquals(2, updateCounter.count);
   }
   
   
   
   private static class BasicClassLocator implements ClassLocator {
      
      private ClassLoader loader;

      public BasicClassLocator(ClassLoader loader) {
         this.loader = loader;
      }

      @Override
      public Class<?> findClass(String name) throws ClassNotFoundException {
         return loader.loadClass(name);
      }
      
   }
   
   private static class MessageCreationListenerCounter implements IMessageCreationListener {

      int preCreate;
      int postCreate;
      int instanceRequest;
      int writer;
      int reader;

      @Override
      public <CLASSTYPE extends Message> void onPreCreate(Class<CLASSTYPE> messageClass, IMessageRequestor requestor, boolean writer) {
         preCreate++;
         if(writer){
            this.writer++;
         } else {
            reader++;
         }
      }

      @Override
      public <CLASSTYPE extends Message> void onPostCreate(Class<CLASSTYPE> messageClass, IMessageRequestor requestor, boolean writer, CLASSTYPE message, Namespace namespace) {
         postCreate++;
      }

      @Override
      public <CLASSTYPE extends Message> void onInstanceRequest(Class<CLASSTYPE> messageClass, CLASSTYPE message, IMessageRequestor requestor, boolean writer) {
         instanceRequest++;
      }
      
   }
   
   private static class UpdateCounter implements IOSEEMessageListener {

      int count = 0;
      
      @Override
      public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
         count++;
      }

      @Override
      public void onInitListener() throws MessageSystemException {
      }
   }

}
