package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * This class is used to walk through an OTEBinaryRecording file one message transmission at a time so that data can be analyzed.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageCaptureDataIterator {

   private BinaryMessageDecoder decoder;
   private FileChannel fileChannel;
   private int numDataSections;
   private int currentDataSection;
   private int frames;
   private int currentFrame;
   private BinaryWorker worker;
   private MessageCaptureDataStripe myMessageCapture;
   private MessageCaptureMessageLookup messageLookup;

   /**
    * 
    * @param file - a file that is an OTEBinaryRecording
    * @param classLocator - a class locater so that Message classes specified in the recording can be instantiated
    * @throws IOException
    */
   public MessageCaptureDataIterator(File file, ClassLocator classLocator) throws IOException{
      decoder = new BinaryMessageDecoder();
      fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
      decoder.setInput(fileChannel);
      messageLookup = new MessageCaptureMessageLookup();
      myMessageCapture = new MessageCaptureDataStripe(messageLookup);
      numDataSections = decoder.getNumberOfDataSections();
      currentDataSection = 0;
      if(numDataSections > 0){
         frames = decoder.transitionToSection(0);
         currentFrame = 0;
      } else {
         frames = 0;
      }
      worker = new BinaryWorker();
      Collection<String> messages = decoder.getMessages();
      for(String messageClass:messages){
         Class<? extends Message> clazz;
         try {
            clazz = classLocator.findClass(messageClass).asSubclass(Message.class);
            if(clazz != null){
               messageLookup.put(messageClass, (Message)clazz.newInstance());
            }
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
         } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      
   }
   
   /**
    * 
    * @return true if you can call next() and get more data
    */
   public boolean hasNext(){
      return (currentDataSection+1 < numDataSections) || currentFrame < frames;
   }
   
   /**
    * Each call to next walks through the next transmission of a single message in the capture set.  It is not an entire frame of messages but simply one transmission
    * of one message.  This gives you a view of the data that allows you to check the state of a series of messages as they relate to each other.  There is a single time 
    * associated with the capture so that you can see that at that time what the current state of all signals of interest are.
    *     
    * @return MessageCaptureDataStripe
    * @throws IOException
    */
   public MessageCaptureDataStripe next() throws IOException{
      if(!hasNext()){
         throw new NoSuchElementException();
      }
      if(currentFrame >= frames){
         currentDataSection++;
         frames = decoder.transitionToSection(currentDataSection);
         currentFrame = 0;
      }
      decoder.playFrame(currentFrame, worker);
      currentFrame++;
      return myMessageCapture;
   }
   
   /**
    * Cleans up resources that are used by this iterator.
    * 
    * @throws IOException
    */
   public void close() throws IOException{
      fileChannel.close();
   }
   
   public MessageCaptureMessageLookup getMessageLookup(){
      return messageLookup;
   }
   
   /**
    * Callback that actually copies the recorded data into the local instance of a message object.
    * 
    * @author Andrew M. Finkbeiner
    *
    */
   private class BinaryWorker implements BinaryDecoderWorker {

      @Override
      public void scan(long time, String messageName, ByteBuffer buffer, int length) {
         
      }

      @Override
      public void play(long time, String messageName, ByteBuffer buffer, int length) {
         myMessageCapture.setTime(time);
         Message message = messageLookup.get(messageName);
         if(message != null){
            message.setData(buffer, length);
            message.setActivityCount(message.getActivityCount()+1);
         }
      }
      
   }
}
