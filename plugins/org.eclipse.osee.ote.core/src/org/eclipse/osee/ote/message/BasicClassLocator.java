package org.eclipse.osee.ote.message;

public class BasicClassLocator implements ClassLocator {

   private ClassLoader loader;

   public BasicClassLocator(ClassLoader loader) {
      this.loader = loader;
   }

   @Override
   public Class<?> findClass(String name) throws ClassNotFoundException {
      return loader.loadClass(name);
   }

}