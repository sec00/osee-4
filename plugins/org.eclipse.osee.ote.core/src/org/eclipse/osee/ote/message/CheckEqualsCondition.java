package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.condition.EqualsCondition;
import org.eclipse.osee.ote.message.elements.DiscreteElement;

public class CheckEqualsCondition<T extends Comparable<T>> extends AbstractChecker {

   public CheckEqualsCondition(MessageCaptureMessageLookup lookup, Message message, DiscreteElement<T> element, T value, long startTime, long endTime){
      super(lookup.get(message.getClass().getName()), startTime, endTime);
      this.condition = new EqualsCondition<>(lookup.getElement(message, element), value);
   }
   
}
