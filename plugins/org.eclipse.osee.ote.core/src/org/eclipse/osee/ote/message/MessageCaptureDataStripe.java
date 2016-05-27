package org.eclipse.osee.ote.message;

/**
 * This class contains utility methods to query the current state of data in the {@link MessageCaptureDataIterator}.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageCaptureDataStripe {

   private long time;
   /**
    * A shared map that contains the local objects that are updated by the {@link MessageCaptureDataIterator}.
    */
   private MessageCaptureMessageLookup messageLookup;
   private Message lastUpdatedMessage;
   private boolean isInitialValue = false;

   public MessageCaptureDataStripe(MessageCaptureMessageLookup messageLookup) {
      this.messageLookup = messageLookup;
   }

   void setTime(long time) {
      this.time = time;
   }

   /**
    * 
    * @return the current data stripe system time
    */
   public long getTime() {
      return time;
   }

   public MessageCaptureMessageLookup getLookup(){
      return messageLookup;
   }
   
   /**
    * The message that was updated last or null if it is not a message available in the message lookup.
    * 
    * @return last message or null
    */
   public Message lastUpdate(){
      return lastUpdatedMessage;
   }
   
   void setLastUpdatedMessage(Message message){
      lastUpdatedMessage = message;
   }

   void setIsInitialValue(boolean isInitialValue) {
      this.isInitialValue = isInitialValue;
   }
   
   public boolean isInitialValue(){
      return isInitialValue;
   }
   
}
