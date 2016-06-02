package org.eclipse.osee.ote.message.elements;

import java.util.Collection;
import java.util.List;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;

public class IntegerElementGroup extends IntegerElement implements ElementGroup<IntegerElement> {

	@Override
	public void set(ITestEnvironmentAccessor accessor, Integer value) {
		for(IntegerElement el:elements){
			el.set(accessor, value);
		}
	}

	@Override
	public Integer get() {
		return elements.get(0).getNoLog();
	}

	@Override
	public Integer get(ITestEnvironmentAccessor accessor) {
		return elements.get(0).get(accessor);
	}

	@Override
	public Integer getNoLog() {
		return elements.get(0).getNoLog();
	}

	@Override
	public void set(Integer value) {
		for(IntegerElement el:elements){
			el.setNoLog(value);
		}
	}

	@Override
	public void setNoLog(Integer value) {
		for(IntegerElement el:elements){
			el.setNoLog(value);
		}
	}

	@Override
	public void setNoLog(ITestEnvironmentAccessor accessor, Integer value) {
		for(IntegerElement el:elements){
			el.setNoLog(value);
		}	
	}

	private List<IntegerElement> elements;
	
	public IntegerElementGroup(Message msg){
		super(msg, "", msg.getDefaultMessageData(), 0, 0, 0);
	}

	@Override
	public void setAndSend(ITestEnvironmentAccessor accessor, int value) {
		for(IntegerElement el:elements){
			el.setAndSend(accessor, value);
		}
	}

	@Override
	public void setValue(Integer obj) {
		for(IntegerElement el:elements){
			el.setValue(obj);
		}
	}

	@Override
	public Integer getValue() {
		return elements.get(0).getValue();
	}

	@Override
	public Integer valueOf(MemoryResource mem) {
		return elements.get(0).valueOf(mem);
	}

	@Override
	protected Element getNonMappingElement() {
		return this;
	}

   @Override
   public void setElementList(List<IntegerElement> elements) {
      this.elements = elements;
   }
	
}
