package org.eclipse.osee.ote.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.condition.IDiscreteElementCondition;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.save.ElementSave;


public abstract class AbstractChecker<T extends Comparable<T>> implements Checker {

   private final long startTime;
   private final long endTime;
   protected boolean passed;
   private long elapsedTime = 0;

   private long messageActivity = -1;
   private boolean firstTime = true;
   private long baseTime;
   private IDiscreteElementCondition<T> condition;
   private Message message;
   private DiscreteElement<T> element;
   
   private List<Long> times;
   private List<Double> values;
   private boolean lastCheckResult = false;
   private CheckPoint checkPoint;
   private List<String> infos; 
   
   public AbstractChecker(long startTime, long endTime) {
      this.startTime = startTime;
      this.endTime = endTime;
      times = new ArrayList<>();
      values = new ArrayList<>();
      infos = new ArrayList<>();
   }
   
   protected void setMessage(Message message){
      this.message = message;
   }
   
   protected void setElement(DiscreteElement<T> element) {
      this.element = element;
   }
   
   protected void setCondition(IDiscreteElementCondition<T> condition) {
      this.condition = condition;
   }
   
   protected IDiscreteElementCondition<T> getCondition(){
      return condition;
   }
   
   protected void setCheckpoint(CheckPoint checkPoint) {
      this.checkPoint = checkPoint;
   }

   public void logToOutfile(ITestAccessor accessor, CheckGroup checkGroup){
      if (checkGroup == null) {
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), getCheckPoint());
      } else {
         checkGroup.add(getCheckPoint());
      }
   }
   
   public void logToOutfile(ITestAccessor accessor){
      logToOutfile(accessor, null);
   }
   
   public ElementSave getElementSave(){
      ElementSave elementSave = new ElementSave();
      elementSave.setPath(new ElementPath(element.getElementPath()).encode());
      List<Double> saveTime = elementSave.getData().getTime();
      for(Long value:this.times){
         saveTime.add(value.doubleValue());
      }
      List<Double> saveValues = elementSave.getData().getValue();
      for(Double value:this.values){
         saveValues.add(value.doubleValue());
      }
      List<String> infoValues = elementSave.getData().getInfo();
      for(String value:this.infos){
         infoValues.add(value);
      }
      return elementSave;
   }
   
   public CheckPoint getCheckPoint(){
      return checkPoint;
   }
   
   
   /**
    * Set the element, message, and condition that are being checked.
    */
   public abstract void init(MessageCaptureMessageLookup lookup);
   
   public void complete(MessageCaptureMessageLookup lookup){
      if(condition.getCheckCount() <= 0){
         if(lastCheckResult){
            passed = true;
         }
      }
   }
   
   protected long getElapsedTime() {
      return elapsedTime;
   }

   protected boolean isPassed() {
      return passed;
   }
   
   /**
    * Check the data. 
    */
   @Override
   public void check(MessageCaptureDataStripe stripe) {
      if(passed){
         return;
      }
      if(isStartTimeMessage(stripe)){
         firstTime = false;
         this.baseTime = stripe.getTime();
         System.out.println("base time = " + this.baseTime);
      }
      if(stripe.lastUpdate() == message){
         if(stripe.isInitialValue()){
            lastCheckResult = condition.check();  
            elapsedTime = 0;//stripe.getTime() - baseTime;
         } else if (isBeforeTime(stripe.getTime())){
            lastCheckResult = condition.check();
            elapsedTime = stripe.getTime() - baseTime;
         } else if (isAfterTime(stripe.getTime())){
            
         } else if(isInTimeRange(stripe.getTime())){ 
            if(stripe.getTime() > startTime && lastCheckResult){
               passed = true;
            } else { 
               lastCheckResult = false;
               passed = condition.checkAndIncrement();  
               elapsedTime = stripe.getTime() - baseTime;
            } 
         } 
         values.add(element.getDouble());
         infos.add(element.toString());
         times.add(stripe.getTime());
      }
   }
   
   private boolean isStartTimeMessage(MessageCaptureDataStripe stripe) {
      return firstTime && stripe.getTime() > -1;
   }

   private final boolean isInTimeRange(long time) {
      long currentDelta = time - baseTime;
      if(currentDelta >= startTime && currentDelta <= endTime){
         return true;
      } else {
         return false;
      }
   }
   
   private final boolean isBeforeTime(long time) {
      long currentDelta = time - baseTime;
      if(currentDelta < startTime){
         return true;
      } else {
         return false;
      }
   }
   
   private final boolean isAfterTime(long time) {
      long currentDelta = time - baseTime;
      if(currentDelta > endTime){
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
