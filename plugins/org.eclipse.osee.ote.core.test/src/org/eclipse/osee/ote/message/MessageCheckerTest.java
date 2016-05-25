package org.eclipse.osee.ote.message;

import java.io.IOException;

import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageCheckerTest {

   private FakeTimer timer;
   private IMessageManager messageManager;
   private IMessageRequestor req;
   private OTEServerFolderForTest oteServerFolder;
   private OTEApiForTest ote;
   private TestEnvironmentInterfaceForTest env; 
   
   @Before
   public void setUp() throws Exception {
      messageManager = new MessageController(new BasicClassLocator(this.getClass().getClassLoader()), null, null);
      req = messageManager.createMessageRequestor("tests");
      
//      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth2, TestMessageDataType.eth2));
      timer = new FakeTimer();
      oteServerFolder = new OTEServerFolderForTest();
      env = new TestEnvironmentInterfaceForTest(timer);
      ote = new OTEApiForTest(oteServerFolder, env);
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testBasicChecker() throws IOException, InterruptedException {
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
      
      TestMessageOne msg1 = req.getMessageReader(TestMessageOne.class);
      TestMessageOne msg1Writer = req.getMessageWriter(TestMessageOne.class);

      MessageCaptureFilter filter = new MessageCaptureFilter(msg1);
      MessageCapture capture = new MessageCapture(ote, messageManager, new BasicClassLocator(getClass().getClassLoader()));
            
      capture.add(filter);
      
      capture.start();//make a TIME message to start the time
      
      for(int i = 0; i < 100; i++){
         if(i == 10){
            msg1Writer.INT1.setValue(23);
            msg1Writer.INT2.setValue(34);
         } else if (i == 30){
            msg1Writer.INT1.setValue(27);
            msg1Writer.INT2.setValue(32);
         } else if (i == 50){
            msg1Writer.INT1.setValue(223);
            msg1Writer.INT2.setValue(67);
         }
         messageManager.publish(msg1Writer);
         timer.step();
         timer.step();
         timer.step();
         timer.step();
         timer.step();
      }
      
      Thread.sleep(100);
      capture.stop();
      
      //at 245 we get the last transmit of the message of interest
      MessageCaptureDataIterator it = capture.getDataIterator();
      MessageCaptureChecker checker = new MessageCaptureChecker(it);
      checker.add(new CheckEqualsCondition<>(it.getMessageLookup(), msg1, msg1.INT1, 223, 245, 500));
      checker.add(new CheckEqualsCondition<>(it.getMessageLookup(), msg1, msg1.INT1, 23, 0, 200));
      checker.add(new CheckEqualsCondition<>(it.getMessageLookup(), msg1, msg1.INT2, 32, 0, 200));
      checker.check();
      checker.close();
      for(Checker ch:checker.get()){
         Assert.assertTrue(ch.passed());
      }
      it.close();
      
      it = capture.getDataIterator();
      checker = new MessageCaptureChecker(it);
      checker.add(new CheckEqualsCondition<>(it.getMessageLookup(), msg1, msg1.INT1, 34, 245, 500));
      checker.add(new CheckEqualsCondition<>(it.getMessageLookup(), msg1, msg1.INT1, 32, 0, 500));
      checker.add(new CheckEqualsCondition<>(it.getMessageLookup(), msg1, msg1.INT2, 27, 0, 500));
      checker.check();
      checker.close();
//      checker.getLog();
      for(Checker ch:checker.get()){
         Assert.assertFalse(ch.passed());
      }
      it.close();
      
   }
   
}
