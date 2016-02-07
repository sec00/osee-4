package org.eclipse.osee.ote.message.elements;

import java.util.Collection;
import java.util.List;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RealElement;

public class RealElementGroup extends RealElement implements ElementGroup<RealElement> {

	@Override
	public void set(ITestEnvironmentAccessor accessor, Double value) {
		for(RealElement el:elements){
			el.set(accessor, value);
		}
	}

	@Override
	public Double get() {
		return elements.get(0).getNoLog();
	}

	@Override
	public Double get(ITestEnvironmentAccessor accessor) {
		return elements.get(0).get(accessor);
	}

	@Override
	public Double getNoLog() {
		return elements.get(0).getNoLog();
	}

	@Override
	public void set(Double value) {
		for(RealElement el:elements){
			el.setNoLog(value);
		}
	}

	@Override
	public void setNoLog(Double value) {
		for(RealElement el:elements){
			el.setNoLog(value);
		}
	}

	@Override
	public void setNoLog(ITestEnvironmentAccessor accessor, Double value) {
		for(RealElement el:elements){
			el.setNoLog(value);
		}	
	}

	private List<RealElement> elements;
	
	public RealElementGroup(Message msg){
		super(msg, "", msg.getDefaultMessageData(), 0, 0, 0);
	}

	@Override
	public void set(ITestEnvironmentAccessor accessor, double value) {
		for(RealElement el:elements){
			el.set(accessor, value);
		}		
	}

	@Override
	public void setAndSend(ITestEnvironmentAccessor accessor, double value) {
		for(RealElement el:elements){
			el.setAndSend(accessor, value);
		}
	}

	@Override
	public void setHex(long hex) {
		for(RealElement el:elements){
			el.setHex(hex);
		}
	}

	@Override
	protected double toDouble(long value) {
		return 0;
	}

	@Override
	protected long toLong(double value) {
		return 0;
	}

	@Override
	public void setValue(Double obj) {
		for(RealElement el:elements){
			el.setValue(obj);
		}
	}

	@Override
	public Double getValue() {
		return elements.get(0).getValue();
	}

   public void setDouble(double value){
      for(RealElement el:elements){
         el.setDouble(value);
      }
   }
   
   public double getDouble(){
      return elements.get(0).getDouble();
   }
	
	@Override
	public Double valueOf(MemoryResource mem) {
		return elements.get(0).valueOf(mem);
	}

	@Override
	protected Element getNonMappingElement() {
		return this;
	}

	@SuppressWarnings("rawtypes")
   @Override
	public RealElement switchMessages(Collection messages) {
		return this;
	}

   @Override
   public void setElementList(List<RealElement> elements) {
      this.elements = elements;
   }
	
}
