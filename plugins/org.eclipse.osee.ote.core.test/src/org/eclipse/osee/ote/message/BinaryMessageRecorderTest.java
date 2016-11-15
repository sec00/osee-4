package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BinaryMessageRecorderTest {

   private File tempFile;
   private FakeTimer timer;
   private IMessageManager messageManager;
   private IMessageRequestor req; 
   
   @Before
   public void setUp() throws Exception {
      tempFile = File.createTempFile("binrec", "test.bin");
      timer = new FakeTimer();
      messageManager = new MessageController(new BasicClassLocator(this.getClass().getClassLoader()), null, null, Arrays.asList(TestMessageDataType.eth1));
      req = messageManager.createRequestor("tests");
      messageManager.registerWriter(new BasicWriter(TestMessageIOType.eth1, TestMessageDataType.eth1));
   }

   @After
   public void tearDown() throws Exception {
      tempFile.delete();
   }

   @Test
   public void testCreation() throws IOException {
      
      BinaryMessageRecorder recorder = BinaryMessageRecorder.create(tempFile, timer);
      recorder.addMessage(req.getMessageReader(TestMessageOne.class));
      recorder.start("test");

      TestMessageOne msg = req.getMessageWriter(TestMessageOne.class);
      
      for(int i = 0; i < 100; i++){
         messageManager.publish(msg);
         timer.step();
      }
     
      recorder.close();
      
      Assert.assertTrue(recorder.getCurrentFileSize() > msg.getDefaultByteSize()*100);
      
      FileChannel file = FileChannel.open(recorder.getDestinationFile().toPath(), StandardOpenOption.READ);
      BinaryMessageDecoder decoder = new BinaryMessageDecoder();
      decoder.setInput(file);
      int numDataSections = decoder.getNumberOfDataSections();
      MyBinaryWorker worker = new MyBinaryWorker(TestMessageOne.class.getName());
      for(int i = 0; i < numDataSections; i++){
         int frames = decoder.transitionToSection(i);
         for(int j = 0; j < frames; j++) {
            decoder.playFrame(j, worker);
         }
      }
      Assert.assertEquals(100, worker.playcount);
   }
   
   @Test
   public void testScanWhileRecording() throws IOException {
      
      BinaryMessageRecorder recorder = BinaryMessageRecorder.create(tempFile, timer);
      recorder.addMessage(req.getMessageReader(TestMessageOne.class));
      recorder.start("test");

      TestMessageOne msg = req.getMessageWriter(TestMessageOne.class);
      
      for(int i = 0; i < 100; i++){
         messageManager.publish(msg);
         timer.step();
      }
      
      try {
         Thread.sleep(150);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      
      Thread thread = new Thread(new Runnable(){

         @Override
         public void run() {
            for(int i = 0; i < 100; i++){
               messageManager.publish(msg);
               timer.step();
               try {
                  Thread.sleep(5);
               } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }            
         }         
      });
      thread.start();
      try {
         Thread.sleep(150);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      
      Assert.assertTrue(recorder.getCurrentFileSize() > msg.getDefaultByteSize()*100);
      
      FileChannel file = FileChannel.open(recorder.getDestinationFile().toPath(), StandardOpenOption.READ);
      BinaryMessageDecoder decoder = new BinaryMessageDecoder();
      decoder.setInput(file);
      int numDataSections = decoder.getNumberOfDataSections();
      System.out.printf("%d data sections\n", numDataSections);
      MyBinaryWorker worker = new MyBinaryWorker(TestMessageOne.class.getName());
      for(int i = 0; i < numDataSections; i++){
         int frames = decoder.transitionToSection(i);
         System.out.printf("framecount[%d][%d]\n", numDataSections, frames);
         for(int j = 0; j < frames; j++) {
            decoder.playFrame(j, worker);
         }
      }
      Assert.assertTrue(worker.playcount > 100);
      System.out.printf("num of messages[%d]\n", worker.playcount);
      recorder.close();
   }
   
   private static class MyBinaryWorker implements BinaryDecoderWorker {

      int playcount = 0;
      int scancount = 0;
      private String className;
      
      public MyBinaryWorker(String name) {
         this.className = name;
      }

      @Override
      public void scan(long time, String messageName, ByteBuffer buffer, int length) {
         scancount++;
      }

      @Override
      public void play(long time, String messageName, ByteBuffer buffer, int length) {
         if(time > -1 && className.equals(messageName)){
            playcount++;
         } 
      }
      
   }
   
 

}
