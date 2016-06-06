package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.condition.PulseCondition;
import org.eclipse.osee.ote.message.elements.DiscreteElement;

public class CheckPulseCondition<T extends Comparable<T>> extends AbstractChecker<T> {

   private DiscreteElement<T> element;
   private Message message;
   private T pulsedValue;
   private T nonPulsedValue;
   private int pulses;
   private PulseCondition<T> pulseCondition;

   public CheckPulseCondition(Message message, DiscreteElement<T> element, T pulsedValue, T nonPulsedValue, int pulses, long startTime, long endTime){
      super(startTime, endTime);
      this.message = message;
      this.element = element;
      this.pulsedValue = pulsedValue;
      this.nonPulsedValue = nonPulsedValue;
      this.pulses = pulses;
   }
   
   public CheckPulseCondition(Message message, DiscreteElement<T> element, T pulsedValue, T nonPulsedValue, long startTime, long endTime){
      super(startTime, endTime);
      this.message = message;
      this.element = element;
      this.pulsedValue = pulsedValue;
      this.nonPulsedValue = nonPulsedValue;
      this.pulses = 2;
   }
   
   public void init(MessageCaptureMessageLookup lookup){
      setMessage(lookup.get(message.getClass().getName()));
      setElement(lookup.getElement(message, element));
      pulseCondition = new PulseCondition<>(lookup.getElement(message, element), pulsedValue, nonPulsedValue, pulses);
      setCondition(pulseCondition);
   }
   
   public void complete(MessageCaptureMessageLookup lookup){
      super.complete(lookup);
      setCheckpoint(
            new CheckPoint(element.getFullName(), pulsedValue.toString() + " FOR " + pulses + " PULSES",
               getCondition().getLastCheckValue().toString() + " FOR " + pulseCondition.getPulses() + " PULSES", isPassed(),
               getElapsedTime()));
   }
   
  
}
