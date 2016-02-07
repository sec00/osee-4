package org.eclipse.osee.ote.message;

public interface ElementNoMappingProvider<T> {
   
   boolean providesType(Class<?> clazz);
   T getNonMappingElement(T elementGettingReplaced);
   
}
