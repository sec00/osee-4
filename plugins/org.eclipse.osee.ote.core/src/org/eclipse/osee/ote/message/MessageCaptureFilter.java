package org.eclipse.osee.ote.message;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ote.scheduler.Scheduler;

/**
 * This class contains messages that will be recorded when added to a {@link MessageCapture} and it defines the criteria by which a capture is complete.  
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageCaptureFilter {

   private HashSet<Message> messages;
   private Scheduler scheduler;
   private long startTime;
   
   /**
    * 
    * @param messages The list of messages that will be captured.
    */
   public MessageCaptureFilter(Message...messages){
      this.messages = new HashSet<>();
      for(Message msg:messages){
         this.messages.add(msg);
      }
   }
   
   final void start(Scheduler scheduler){
      this.scheduler = scheduler;
      this.startTime = scheduler.getTime();
   }
   
   /**
    * 
    * @return The elapsed environment time since the start of the recording.
    */
   public final long getElapsedTime(){
      if(scheduler == null){
         return 0;
      }
      return scheduler.getTime() - startTime;
   }

   /**
    * This method will be called every time that a message that is part of this filter is update.  Override this method to signal that the recording can
    * stop for this filter.
    * 
    * @return true if this filter no longer needs to capture data.
    */
   public boolean isDone(){
      return false;
   }
   
   /**
    * 
    * @return The set of messages that will be captured.
    */
   public final Set<Message> getMessages(){
      return messages;
   }
}
