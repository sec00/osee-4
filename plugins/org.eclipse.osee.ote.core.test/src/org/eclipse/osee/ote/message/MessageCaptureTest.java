package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.IOException;

import org.eclipse.osee.ote.message.condition.EqualsCondition;
import org.eclipse.osee.ote.message.condition.ICondition;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.junit.After;
import org.junit.Assert;
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
      req = messageManager.createRequestor("tests");
      
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
      MessageCapture capture = new MessageCapture(new File(System.getProperty("user.dir")), timer, new BasicClassLocator(getClass().getClassLoader()));
            
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
      MessageCapture capture = new MessageCapture(new File(System.getProperty("user.dir")), timer, new BasicClassLocator(getClass().getClassLoader()));
            
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
//      int count = 0;
//      while(it.hasNext()){
//         MessageCaptureDataStripe stripe = it.next();
//         System.out.printf("%d INT1[%d] INT2[%d]\n", stripe.getTime(), stripe.getLookup().getElement(msg1, msg1.INT1), stripe.getLookup().getElement(msg1, msg1.INT2));
//      }
//      it.close();
      
      boolean passed = false;
      IntegerElement el = it.getMessageLookup().getElement(msg1, msg1.INT1);
      ICondition condition = new EqualsCondition<>(el, 27);
      while(it.hasNext()){
         MessageCaptureDataStripe stripe = it.next();
         if(condition.check()){
            passed = true;
            System.out.printf("%d time passed\n", stripe.getTime());
            break;
         }
         System.out.printf("%d INT1[%d] INT2[%d]\n", stripe.getTime(), stripe.getLookup().getValue(msg1, msg1.INT1), stripe.getLookup().getValue(msg1, msg1.INT2));
      }
      it.close();
      Assert.assertTrue(passed);
      
      
      it = capture.getDataIterator();
      Checker check = new CheckEqualsCondition<>(msg1, msg1.INT1, 27, 0, 500);
      check.init(it.getMessageLookup());
      while(it.hasNext()){
         MessageCaptureDataStripe stripe = it.next();
         check.check(stripe);
      }
      check.complete(it.getMessageLookup());
      it.close();
      Assert.assertTrue(check.passed());
      
      
      it = capture.getDataIterator();
      check = new CheckEqualsCondition<>(msg1, msg1.INT1, 27, 250, 500);
      check.init(it.getMessageLookup());
      while(it.hasNext()){
         MessageCaptureDataStripe stripe = it.next();
         System.out.printf("%d %d\n", stripe.getLookup().getValue(msg1, msg1.INT1), stripe.getTime());
         if(stripe.getTime() == 250){
            System.out.println("df");
         }
         check.check(stripe);
      }
      check.complete(it.getMessageLookup());
      it.close();
      Assert.assertFalse(check.passed());
      

      //at 245 we get the last transmit of the message of interest
      it = capture.getDataIterator();
      MessageCaptureChecker checker = new MessageCaptureChecker(it);
      checker.add(new CheckEqualsCondition<>(msg1, msg1.INT1, 27, 245, 500));
      checker.check();
      checker.close();
      for(Checker ch:checker.get()){
         Assert.assertTrue(ch.passed());
      }
      
      
   }
   
}
