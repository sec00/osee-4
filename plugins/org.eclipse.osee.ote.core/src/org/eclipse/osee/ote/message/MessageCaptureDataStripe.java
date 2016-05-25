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
   
}
