package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.ote.scheduler.Scheduler;
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
      messageManager = new MessageController(new BasicClassLocator(this.getClass().getClassLoader()), null, null);
      req = messageManager.createMessageRequestor("tests");
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
      MyBinaryWorker worker = new MyBinaryWorker();
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
//      recorder.close();
      
      Assert.assertTrue(recorder.getCurrentFileSize() > msg.getDefaultByteSize()*100);
      
      FileChannel file = FileChannel.open(recorder.getDestinationFile().toPath(), StandardOpenOption.READ);
      BinaryMessageDecoder decoder = new BinaryMessageDecoder();
      decoder.setInput(file);
      int numDataSections = decoder.getNumberOfDataSections();
      System.out.printf("%d data sections\n", numDataSections);
      MyBinaryWorker worker = new MyBinaryWorker();
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
      
      @Override
      public void scan(long time, String messageName, ByteBuffer buffer, int length) {
         scancount++;
      }

      @Override
      public void play(long time, String messageName, ByteBuffer buffer, int length) {
         playcount++;
      }
      
   }
   
   private static class FakeTimer implements ITimerControl {
    long step = 0;
      
      @Override
      public void step() {
         step++;
      }
      
      @Override
      public ICancelTimer setTimerFor(ITimeout objToNotify, int milliseconds) {
         return null;
      }
      
      @Override
      public void setRunManager(IRunManager runManager) {
      }
      
      @Override
      public void setCycleCount(int cycle) {
      }
      
      @Override
      public void removeTask(EnvironmentTask task) {
      }
      
      @Override
      public boolean isRealtime() {
         return false;
      }
      
      @Override
      public void incrementCycleCount() {
      }
      
      @Override
      public long getTimeOfDay() {
         return step;
      }
      
      @Override
      public IRunManager getRunManager() {
         return null;
      }
      
      @Override
      public long getEnvTime() {
         return step;
      }
      
      @Override
      public int getCycleCount() {
         return 0;
      }
      
      @Override
      public void envWait(int milliseconds) throws InterruptedException {
      }
      
      @Override
      public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
      }
      
      @Override
      public void dispose() {
      }
      
      @Override
      public void cancelTimers() {
      }
      
      @Override
      public void cancelAllTasks() {
      }
      
      @Override
      public void addTask(EnvironmentTask task, TestEnvironment environment) {
      }

      @Override
      public Scheduler getScheduler() {
         // TODO Auto-generated method stub
         return null;
      }
   }

}
