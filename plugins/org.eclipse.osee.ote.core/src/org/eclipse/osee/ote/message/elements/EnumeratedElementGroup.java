package org.eclipse.osee.ote.message.elements;

import java.util.Collection;
import java.util.List;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.IElementVisitor;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingEnumeratedElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EnumeratedElementGroup extends EnumeratedElement implements ElementGroup<EnumeratedElement>{

	private List<EnumeratedElement> elements;
	
   public EnumeratedElementGroup(Message msg, Class enumerationClass){
		super(msg, "", enumerationClass, msg.getDefaultMessageData(), 0, 0, 0);
	}

	@Override
	public int getIntValue() {
		return elements.get(0).getIntValue();
	}

	@Override
	public void set(ITestEnvironmentAccessor accessor, Comparable value) {
		for(EnumeratedElement el:elements){
			el.set(accessor, value);
		}
	}

	@Override
	public void set(Comparable value) {
		for(EnumeratedElement el:elements){
			el.setNoLog(value);
		}
	}

	@Override
	public void setNoLog(Comparable value) {
		for(EnumeratedElement el:elements){
			el.setNoLog(value);
		}
	}

	@Override
	public void setNoLog(ITestEnvironmentAccessor accessor, Comparable value) {
		for(EnumeratedElement el:elements){
			el.setNoLog(value);
		}
	}

	@Override
	public void setAndSend(ITestEnvironmentAccessor accessor, Enum enumeration) {
		for(EnumeratedElement el:elements){
			el.setAndSend(accessor, enumeration);
		}
	}

	@Override
	public Enum waitForNotInList(ITestAccessor accessor, Enum[] list,
			int milliseconds) throws InterruptedException {
		return elements.get(0).waitForNotInList(accessor, list, milliseconds);
	}

	@Override
	public Enum waitForInList(ITestAccessor accessor, Enum[] list,
			int milliseconds) throws InterruptedException {
		return elements.get(0).waitForInList(accessor, list, milliseconds);
	}

	@Override
	public void checkPulse(ITestAccessor accessor, Enum value)
			throws InterruptedException {
		elements.get(0).checkPulse(accessor, value);
	}

	@Override
	public EnumeratedElement switchMessages(Collection messages) {
		return this;
	}

	@Override
	protected Enum toEnum(int intValue) {
		return super.toEnum(intValue);
	}

	@Override
	public Enum getValue() {
		return elements.get(0).getValue();
	}

	@Override
	public Enum valueOf(MemoryResource otherMem) {
		return elements.get(0).valueOf(otherMem);
	}

	@Override
	public void setValue(Enum obj) {
		for(EnumeratedElement el:elements){
			el.setValue(obj);
		}
	}

	@Override
	public Enum[] getEnumValues() {
		return elements.get(0).getEnumValues();
	}

	@Override
	public String toString(Enum obj) {
		return elements.get(0).toString(obj);
	}

	@Override
	public void parseAndSet(ITestEnvironmentAccessor accessor, String value)
			throws IllegalArgumentException {
		for(EnumeratedElement el:elements){
			el.parseAndSet(accessor, value);
		}
	}

	@Override
	public void set(String value) throws IllegalArgumentException {
		for(EnumeratedElement el:elements){
			el.set(value);
		}
	}

	@Override
	public void setbyEnumIndex(int index) throws IllegalArgumentException {
		for(EnumeratedElement el:elements){
			el.setbyEnumIndex(index);
		}
	}

	@Override
	public void visit(IElementVisitor visitor) {
		elements.get(0).visit(visitor);
	}

	@Override
	protected NonMappingEnumeratedElement getNonMappingElement() {
		return super.getNonMappingElement();
	}

	@Override
	public String valueOf() {
		return elements.get(0).valueOf();
	}

	@Override
	public Class getEnumClass() {
		return super.getEnumClass();
	}

	@Override
	public Enum elementMask(Enum value) {
		return elements.get(0).elementMask(value);
	}
	
	@Override
	public void setElementList(List<EnumeratedElement> elements) {
	   this.elements = elements;
	}
	
}
