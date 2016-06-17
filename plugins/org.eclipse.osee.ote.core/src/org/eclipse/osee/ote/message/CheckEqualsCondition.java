package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.condition.EqualsCondition;
import org.eclipse.osee.ote.message.elements.DiscreteElement;

/**
 * Used by the {@link MessageCapture} infrastructure to do a {@link EqualsCondition} check.
 * 
 * @author Andrew M. Finkbeiner
 *
 * @param <T>
 */
public class CheckEqualsCondition<T extends Comparable<T>> extends AbstractChecker<T> {

   private DiscreteElement<T> element;
   private Message message;
   private T value;

   public CheckEqualsCondition(Message message, DiscreteElement<T> element, T value, long startTime, long endTime){
      super(startTime, endTime);
      this.message = message;
      this.element = element;
      this.value = value;
   }
   
   public void init(MessageCaptureMessageLookup lookup){
      setMessage(lookup.get(message.getClass().getName()));
      setElement(lookup.getElement(message, element));
      setCondition(new EqualsCondition<>(lookup.getElement(message, element), value));
   }
   
   public void complete(MessageCaptureMessageLookup lookup){
      super.complete(lookup);
      setCheckpoint(new CheckPoint(element.getFullName(), 
            value.toString(),
            getCondition().getLastCheckValue().toString(), 
            isPassed(),
            getCondition().getCheckCount(), 
            getElapsedTime()));
   }
   
  
}
