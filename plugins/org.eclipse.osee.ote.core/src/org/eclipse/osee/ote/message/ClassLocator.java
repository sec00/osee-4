package org.eclipse.osee.ote.message;

public interface ClassLocator {
   Class<?> findClass(String name) throws ClassNotFoundException;
}
