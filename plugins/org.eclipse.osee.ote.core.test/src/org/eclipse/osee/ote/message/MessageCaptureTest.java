package org.eclipse.osee.ote.message;

import java.io.IOException;

import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageCaptureTest {

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
   public void testGoPath() throws IOException, InterruptedException {
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
      
      TestMessageOne msg1 = req.getMessageReader(TestMessageOne.class);
      TestMessageOne msg1Writer = req.getMessageWriter(TestMessageOne.class);

      MessageCaptureFilter filter = new MessageCaptureFilter(msg1);
      MessageCapture capture = new MessageCapture(ote, messageManager, new BasicClassLocator(getClass().getClassLoader()));
            
      capture.add(filter);
      
      capture.start();
      
      for(int i = 0; i < 100; i++){
         messageManager.publish(msg1Writer);
         timer.step();
      }
      
      Thread.sleep(100);
      capture.stop();
      
      
   }

   @Test
   public void testSignalSequence() throws IOException, InterruptedException {
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
      
      TestMessageOne msg1 = req.getMessageReader(TestMessageOne.class);
      TestMessageOne msg1Writer = req.getMessageWriter(TestMessageOne.class);

      MessageCaptureFilter filter = new MessageCaptureFilter(msg1);
      MessageCapture capture = new MessageCapture(ote, messageManager, new BasicClassLocator(getClass().getClassLoader()));
            
      capture.add(filter);
      
      capture.start();
      
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
      
      MessageCaptureDataIterator it = capture.getDataIterator();
      int count = 0;
      while(it.hasNext()){
         MessageCaptureDataStripe stripe = it.next();
         System.out.printf("%d INT1[%d] INT2[%d]\n", stripe.getTime(), stripe.getElement(msg1, msg1.INT1), stripe.getElement(msg1, msg1.INT2));
      }
      it.close();
      
//      SequenceChecker checker = new SequenceChecker();
//      checker.add(new DataStripe().add(new IntegerElementCheck(msg1, msg1.INT1, 23)).add(new IntegerElementCheck(msg1, msg1.INT2, 34)));
//      checker.add(new DataStripe().add(new IntegerElementCheck(msg1, msg1.INT1, 27)).add(new IntegerElementCheck(msg1, msg1.INT2, 32)));
//      checker.add(new DataStripe().add(new IntegerElementCheck(msg1, msg1.INT1, 223)).add(new IntegerElementCheck(msg1, msg1.INT2, 67)));
//      OTECheck.check(capture, checker);
//      
//      
//      it = capture.getDataIterator();
      
      
   }
   
}
