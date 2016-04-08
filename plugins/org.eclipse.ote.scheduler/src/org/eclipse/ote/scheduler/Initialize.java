package org.eclipse.ote.scheduler;

@FunctionalInterface
public interface Initialize<T> {
   void initialize(T obj);
}
