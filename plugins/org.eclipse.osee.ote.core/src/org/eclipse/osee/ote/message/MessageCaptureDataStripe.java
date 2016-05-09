package org.eclipse.osee.ote.message;

import java.util.HashMap;

import org.eclipse.osee.ote.message.elements.IntegerElement;

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
   private HashMap<String, Message> messageMap;

   public MessageCaptureDataStripe(HashMap<String, Message> messageMap) {
      this.messageMap = messageMap;
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

   /**
    * 
    * @param message
    * @param element
    * @return
    */
   public int getElement(Message message, IntegerElement element) {
      Message localMessageObject = messageMap.get(message.getClass().getName());
      IntegerElement el = (IntegerElement)localMessageObject.getElement(element.getElementPath());
      return el.getInt();
   }

}
