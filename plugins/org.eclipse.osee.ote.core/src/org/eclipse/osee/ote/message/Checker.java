package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

public interface Checker {

   /**
    * This will be called on each new message transmission.  If the time value is -1 then it is an initial value in the recording, the value of the message at the time 
    * the recording was started, not necessarily default values and not the result of a message transmission.  The Activity count of a message will be set to -1 to indicate 
    * the data from the initial value was set so that you can find out what message was updated.
    * 
    * 
    * @param stripe
    */
   //TODO CHANGE THE API TO PASS IN THE NAME OF THE UPDATED MESSAGE
   void check(MessageCaptureDataStripe stripe);

   /**
    * This is called on each checker before iteration of the data so that it can set up internal state such as element and message values.
    * 
    * @param lookup
    */
   void init(MessageCaptureMessageLookup lookup);
   
   /**
    * This is called after all the data has been iterated.  This is useful for post processing such as a final determination on the checker or generation
    * of log data.
    * 
    * @param lookup
    */
   public void complete(MessageCaptureMessageLookup lookup);
   
   public void logToOutfile(ITestAccessor accessor, CheckGroup checkGroup);
   
   public void logToOutfile(ITestAccessor accessor);
   
   boolean passed();

}
