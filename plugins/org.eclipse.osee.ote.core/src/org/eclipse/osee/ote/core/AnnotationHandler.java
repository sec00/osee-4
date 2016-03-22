package org.eclipse.osee.ote.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

interface AnnotationHandler<T extends Annotation> {

	void process(T annotation, Object object, Field field) throws Exception;

   void process(Annotation annotation, Object object);

   void process(Annotation annotation, Object object, Method method);

}
