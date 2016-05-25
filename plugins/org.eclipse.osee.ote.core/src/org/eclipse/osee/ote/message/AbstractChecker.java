package org.eclipse.osee.ote.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.message.condition.IDiscreteElementCondition;


public class AbstractChecker implements Checker {

   private final long startTime;
   private final long endTime;
   protected boolean passed;

   private long messageActivity = -1;
   private boolean firstTime = true;
   private long baseTime;
   protected IDiscreteElementCondition condition;
   private Message message;
   
   private List<Long> times;
   private List<Object> values; 
   
   public AbstractChecker(Message message, long startTime, long endTime) {
      this.startTime = startTime;
      this.endTime = endTime;
      this.message = message;
      times = new ArrayList<>();
      values = new ArrayList<>();
   }
   
   /**
    * Check the data.  If no recorded transmissions happened during the recording then there will never be a check.
    */
   @Override
   public void check(MessageCaptureDataStripe stripe) {
      if(firstTime ){
         firstTime = false;
         this.baseTime = stripe.getTime();
      }
      if(shouldCheck(stripe)){
         if(hasMessageActivity(message)){
            passed = condition.checkAndIncrement();   
            values.add(condition.getLastCheckValue());
            times.add(stripe.getTime());
         } else {
            passed = condition.check();
         }
      }
   }
   
   private boolean shouldCheck(MessageCaptureDataStripe stripe) {
      return !passed && isInTimeRange(stripe.getTime());
   }
   
   private boolean hasMessageActivity(Message message) {
      boolean hasActivity = message.getActivityCount() != messageActivity;
      messageActivity = message.getActivityCount();
      return hasActivity;
   }

   private final boolean isInTimeRange(long time) {
      long currentDelta = time - baseTime;
      if(currentDelta >= startTime && currentDelta <= endTime){
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean passed() {
      return passed;
   }

}
