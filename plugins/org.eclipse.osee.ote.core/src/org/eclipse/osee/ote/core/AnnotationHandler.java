package org.eclipse.osee.ote.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

interface AnnotationHandler<T extends Annotation> {

	void process(T annotation, Object object, Field field) throws Exception;

   void process(T annotation, Object object);

   void process(T annotation, Object object, Method method);

}
