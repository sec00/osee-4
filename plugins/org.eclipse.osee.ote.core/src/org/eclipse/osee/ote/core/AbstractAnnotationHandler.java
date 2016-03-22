package org.eclipse.osee.ote.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class AbstractAnnotationHandler<T extends Annotation> implements AnnotationHandler<T> {

   protected void injectToFields(Object object, Field field, Object toInject) throws Exception {
      boolean wasAccessible = field.isAccessible();
      field.setAccessible(true);
      try {
         field.set(object, toInject);
      } catch (Error e) {
         throw new Exception("Problems injecting dependencies in "+ field.getName(), e);
      } finally {
         field.setAccessible(wasAccessible);
      }
   }   

}
