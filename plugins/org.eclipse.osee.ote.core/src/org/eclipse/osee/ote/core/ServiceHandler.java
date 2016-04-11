package org.eclipse.osee.ote.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ServiceHandler extends AbstractAnnotationHandler<Service> {

   @Override
   public void process(Service annotation, Object object, Field field) throws Exception {
      Object obj = ServiceUtility.getService(field.getType());
      injectToFields(object, field, obj);
   }

   @Override
   public void process(Service annotation, Object object) {
      
   }

   @Override
   public void process(Service annotation, Object object, Method method) {
      
   }

}
