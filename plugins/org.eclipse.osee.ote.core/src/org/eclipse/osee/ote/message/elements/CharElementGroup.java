package org.eclipse.osee.ote.message.elements;

import java.util.Collection;
import java.util.List;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.CharElement;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.IElementVisitor;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingCharElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

public class CharElementGroup extends CharElement implements ElementGroup<CharElement> {

   private List<CharElement> elements;

   public CharElementGroup(Message msg){
      super(msg, "", msg.getDefaultMessageData(), 0, 0, 0);
   }

   @Override
   public void setElementList(List<CharElement> elements) {
      this.elements = elements;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#switchMessages(java.util.Collection)
    */
   @Override
   public CharElement switchMessages(Collection<? extends Message> messages) {
      return this;
   }

   @Override
   public void checkForwarding(ITestAccessor accessor, CharElement cause, Character value) throws InterruptedException {
      elements.get(0).checkForwarding(accessor, cause, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#checkNot(org.eclipse.osee.ote.message.interfaces.ITestAccessor, java.lang.String, int)
    */
   @Override
   public boolean checkNot(ITestAccessor accessor, String value, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkNot(accessor, value, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#checkNot(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.String, int)
    */
   @Override
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, String value,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkNot(accessor, checkGroup, value, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#check(org.eclipse.osee.ote.message.interfaces.ITestAccessor, java.lang.String, int)
    */
   @Override
   public boolean check(ITestAccessor accessor, String value, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).check(accessor, value, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#check(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.String, int)
    */
   @Override
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, String value,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).check(accessor, checkGroup, value, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#checkNot(org.eclipse.osee.ote.message.interfaces.ITestAccessor, java.lang.String)
    */
   @Override
   public boolean checkNot(ITestAccessor accessor, String value) {
      // TODO Auto-generated method stub
      return elements.get(0).checkNot(accessor, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#checkNot(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.String)
    */
   @Override
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, String value) {
      // TODO Auto-generated method stub
      return elements.get(0).checkNot(accessor, checkGroup, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#check(org.eclipse.osee.ote.message.interfaces.ITestAccessor, java.lang.String)
    */
   @Override
   public boolean check(ITestAccessor accessor, String value) {
      // TODO Auto-generated method stub
      return elements.get(0).check(accessor, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#check(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.String)
    */
   @Override
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, String value) {
      // TODO Auto-generated method stub
      return elements.get(0).check(accessor, checkGroup, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#getString(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, int)
    */
   @Override
   public String getString(ITestEnvironmentAccessor accessor, int stringLength) {
      // TODO Auto-generated method stub
      return elements.get(0).getString(accessor, stringLength);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#parseAndSet(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) {
      for(CharElement el: elements) {
         el.parseAndSet(accessor, value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#set(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void set(ITestEnvironmentAccessor a, String value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.set(a, value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#setValue(java.lang.String)
    */
   @Override
   public void setValue(String value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setValue(value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#setAndSend(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, String value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setAndSend(accessor, value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#setNoLog(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void setNoLog(ITestEnvironmentAccessor accessor, String value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setNoLog(accessor, value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#setAndSend(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.Character)
    */
   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, Character value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setAndSend(accessor, value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#setValue(java.lang.Character)
    */
   @Override
   public void setValue(Character value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setValue(value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#getValue()
    */
   @Override
   public Character getValue() {
      // TODO Auto-generated method stub
      return elements.get(0).getValue();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#valueOf(org.eclipse.osee.ote.message.data.MemoryResource)
    */
   @Override
   public Character valueOf(MemoryResource otherMem) {
      // TODO Auto-generated method stub
      return elements.get(0).valueOf(otherMem);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#toString(java.lang.Character)
    */
   @Override
   public String toString(Character obj) {
      // TODO Auto-generated method stub
      return elements.get(0).toString(obj);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#visit(org.eclipse.osee.ote.message.elements.IElementVisitor)
    */
   @Override
   public void visit(IElementVisitor visitor) {
      // TODO Auto-generated method stub
      elements.get(0).visit(visitor);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#getNonMappingElement()
    */
   @Override
   protected NonMappingCharElement getNonMappingElement() {
      return super.getNonMappingElement();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.CharElement#elementMask(java.lang.Character)
    */
   @Override
   public Character elementMask(Character value) {
      // TODO Auto-generated method stub
      return elements.get(0).elementMask(value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#valueOf()
    */
   @Override
   public String valueOf() {
      // TODO Auto-generated method stub
      return elements.get(0).valueOf();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#set(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.Comparable)
    */
   @Override
   public void set(ITestEnvironmentAccessor accessor, Character value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.set(accessor, value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#check(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable)
    */
   @Override
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, Character value) {
      // TODO Auto-generated method stub
      return elements.get(0).check(accessor, checkGroup, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkNT(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable)
    */
   @Override
   public boolean checkNT(ITestAccessor accessor, CheckGroup checkGroup, Character value) {
      // TODO Auto-generated method stub
      return elements.get(0).checkNT(accessor, checkGroup, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkNotNT(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable)
    */
   @Override
   public boolean checkNotNT(ITestAccessor accessor, CheckGroup checkGroup, Character value) {
      // TODO Auto-generated method stub
      return elements.get(0).checkNotNT(accessor, checkGroup, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkRange(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable, boolean, java.lang.Comparable, boolean)
    */
   @Override
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, Character minValue,
         boolean minInclusive, Character maxValue, boolean maxInclusive) {
      // TODO Auto-generated method stub
      return elements.get(0).checkRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkRangeNT(org.eclipse.osee.ote.message.interfaces.ITestAccessor, java.lang.Comparable, boolean, java.lang.Comparable, boolean)
    */
   @Override
   public boolean checkRangeNT(ITestAccessor accessor, Character minValue, boolean minInclusive,
         Character maxValue, boolean maxInclusive) {
      // TODO Auto-generated method stub
      return elements.get(0).checkRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkRangeNT(org.eclipse.osee.ote.message.interfaces.ITestAccessor, java.lang.Comparable, boolean, java.lang.Comparable, boolean, int)
    */
   @Override
   public boolean checkRangeNT(ITestAccessor accessor, Character minValue, boolean minInclusive,
         Character maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive, millis);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkNot(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable)
    */
   @Override
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, Character value) {
      // TODO Auto-generated method stub
      return elements.get(0).checkNot(accessor, checkGroup, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#waitForValue(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.Comparable, int)
    */
   @Override
   public Character waitForValue(ITestEnvironmentAccessor accessor, Character value,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).waitForValue(accessor, value, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#waitForNotValue(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.Comparable, int)
    */
   @Override
   public Character waitForNotValue(ITestEnvironmentAccessor accessor, Character value,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).waitForNotValue(accessor, value, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#waitForRange(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.Comparable, boolean, java.lang.Comparable, boolean, int)
    */
   @Override
   public Character waitForRange(ITestEnvironmentAccessor accessor, Character minValue,
         boolean minInclusive, Character maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).waitForRange(accessor, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#waitForNotRange(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.Comparable, boolean, java.lang.Comparable, boolean, int)
    */
   @Override
   public Character waitForNotRange(ITestEnvironmentAccessor accessor, Character minValue,
         boolean minInclusive, Character maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).waitForNotRange(accessor, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#check(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable, int)
    */
   @Override
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, Character value,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).check(accessor, checkGroup, value, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkRange(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable, boolean, java.lang.Comparable, boolean, int)
    */
   @Override
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, Character minValue,
         boolean minInclusive, Character maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive,
                                        milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkNot(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable, int)
    */
   @Override
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, Character value,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkNot(accessor, checkGroup, value, milliseconds);
   }


   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkNotRange(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable, boolean, java.lang.Comparable, boolean, int)
    */
   @Override
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, Character minValue,
         boolean minInclusive, Character maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkNotRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive,
                                           milliseconds);
   }

   @Override
   public boolean checkNotRangeNT(ITestAccessor accessor, Character minValue, boolean minInclusive,
         Character maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return elements.get(0).checkNotRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   @Override
   public Character checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, Character value,
         int milliseconds) throws InterruptedException {
      return elements.get(0).checkMaintain(accessor, checkGroup, value, milliseconds);
   }

   @Override
   public Character checkMaintainNT(ITestAccessor accessor, Character value, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkMaintainNT(accessor, value, milliseconds);
   }

   @Override
   public Character checkMaintainNotNT(ITestAccessor accessor, Character value, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkMaintainNotNT(accessor, value, milliseconds);
   }

   @Override
   public Character checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup,
         Character value, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkMaintainNot(accessor, checkGroup, value, milliseconds);
   }

   @Override
   public Character checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup,
         Character minValue, boolean minInclusive, Character maxValue, boolean maxInclusive,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkMaintainRange(accessor, checkGroup, minValue, minInclusive, maxValue,
                                                maxInclusive, milliseconds);
   }

   @Override
   public Character checkMaintainRangeNT(ITestAccessor accessor, Character minValue,
         boolean minInclusive, Character maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkMaintainRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive,
                                                  milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkMaintainNotRange(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable, boolean, java.lang.Comparable, boolean, int)
    */
   @Override
   public Character checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup,
         Character minValue, boolean minInclusive, Character maxValue, boolean maxInclusive,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkMaintainNotRange(accessor, checkGroup, minValue, minInclusive, maxValue,
                                                   maxInclusive, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkMaintainNotRangeNT(org.eclipse.osee.ote.message.interfaces.ITestAccessor, java.lang.Comparable, boolean, java.lang.Comparable, boolean, int)
    */
   @Override
   public Character checkMaintainNotRangeNT(ITestAccessor accessor, Character minValue,
         boolean minInclusive, Character maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkMaintainNotRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive,
                                                     milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkPulse(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, java.lang.Comparable, java.lang.Comparable, int)
    */
   @Override
   public boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup, Character pulsedValue,
         Character nonPulsedValue, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkPulse(accessor, checkGroup, pulsedValue, nonPulsedValue, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#toString()
    */
   @Override
   public String toString() {
      // TODO Auto-generated method stub
      return elements.get(0).toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#compareTo(org.eclipse.osee.ote.message.elements.DiscreteElement)
    */
   @Override
   public int compareTo(DiscreteElement<Character> o) {
      // TODO Auto-generated method stub
      return elements.get(0).compareTo(o);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkList(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, boolean, T[], int)
    */
   @Override
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean isInList,
         Character[] list, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkList(accessor, checkGroup, isInList, list, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkList(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, boolean, T[])
    */
   @Override
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean wantInList,
         Character[] list) {
      // TODO Auto-generated method stub
      return elements.get(0).checkList(accessor, checkGroup, wantInList, list);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#waitForList(org.eclipse.osee.ote.message.interfaces.ITestAccessor, T[], boolean, int)
    */
   @Override
   public Character waitForList(ITestAccessor accessor, Character[] list, boolean isInList,
         int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).waitForList(accessor, list, isInList, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#checkMaintainList(org.eclipse.osee.ote.message.interfaces.ITestAccessor, org.eclipse.osee.ote.core.testPoint.CheckGroup, T[], boolean, int)
    */
   @Override
   public Character checkMaintainList(ITestAccessor accessor, CheckGroup checkGroup,
         Character[] list, boolean isInList, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      return elements.get(0).checkMaintainList(accessor, checkGroup, list, isInList, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#toggle(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.Comparable, java.lang.Comparable, int)
    */
   @Override
   public synchronized void toggle(ITestEnvironmentAccessor accessor, Character value1,
         Character value2, int milliseconds) throws InterruptedException {
      // TODO Auto-generated method stub
      elements.get(0).toggle(accessor, value1, value2, milliseconds);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#get()
    */
   @Override
   public Character get() {
      return elements.get(0).getNoLog();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#get(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor)
    */
   @Override
   public Character get(ITestEnvironmentAccessor accessor) {
      // TODO Auto-generated method stub
      return elements.get(0).get(accessor);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#getNoLog()
    */
   @Override
   public Character getNoLog() {
      // TODO Auto-generated method stub
      return elements.get(0).getNoLog();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#set(java.lang.Comparable)
    */
   @Override
   public void set(Character value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setNoLog(value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#setNoLog(java.lang.Comparable)
    */
   @Override
   public void setNoLog(Character value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setNoLog(value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.DiscreteElement#setNoLog(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.Comparable)
    */
   @Override
   public void setNoLog(ITestEnvironmentAccessor accessor, Character value) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setNoLog(value);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getMsb()
    */
   @Override
   public int getMsb() {
      // TODO Auto-generated method stub
      return elements.get(0).getMsb();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getLsb()
    */
   @Override
   public int getLsb() {
      // TODO Auto-generated method stub
      return elements.get(0).getLsb();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getByteOffset()
    */
   @Override
   public int getByteOffset() {
      // TODO Auto-generated method stub
      return elements.get(0).getByteOffset();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getMsgData()
    */
   @Override
   public MessageData getMsgData() {
      // TODO Auto-generated method stub
      return elements.get(0).getMsgData();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getBitLength()
    */
   @Override
   public int getBitLength() {
      // TODO Auto-generated method stub
      return elements.get(0).getBitLength();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getStartingBit()
    */
   @Override
   public int getStartingBit() {
      // TODO Auto-generated method stub
      return elements.get(0).getStartingBit();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getFullName()
    */
   @Override
   public String getFullName() {
      // TODO Auto-generated method stub
      return elements.get(0).getFullName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getName()
    */
   @Override
   public String getName() {
      // TODO Auto-generated method stub
      return elements.get(0).getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getDescriptiveName()
    */
   @Override
   public String getDescriptiveName() {
      // TODO Auto-generated method stub
      return elements.get(0).getDescriptiveName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getMessage()
    */
   @Override
   public Message getMessage() {
      // TODO Auto-generated method stub
      return elements.get(0).getMessage();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getElementName()
    */
   @Override
   public String getElementName() {
      // TODO Auto-generated method stub
      return elements.get(0).getElementName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#isTimedOut()
    */
   @Override
   public boolean isTimedOut() {
      // TODO Auto-generated method stub
      return elements.get(0).isTimedOut();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#setTimeout(boolean)
    */
   @Override
   public void setTimeout(boolean timeout) {
      // TODO Auto-generated method stub
      for(CharElement el: elements) {
         el.setTimeout(timeout);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getType()
    */
   @Override
   public DataType getType() {
      // TODO Auto-generated method stub
      return elements.get(0).getType();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getOriginalLsb()
    */
   @Override
   public int getOriginalLsb() {
      // TODO Auto-generated method stub
      return elements.get(0).getOriginalLsb();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#getOriginalMsb()
    */
   @Override
   public int getOriginalMsb() {
      // TODO Auto-generated method stub
      return elements.get(0).getOriginalMsb();
   }

}
