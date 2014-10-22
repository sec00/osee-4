package org.eclipse.osee.ote.io.internal;

import org.eclipse.osee.ote.io.SystemOutput;
import org.eclipse.osee.ote.io.SystemOutputListener;

public class SystemOutputImpl implements SystemOutput {

   @Override
   public void addListener(SystemOutputListener listener) {
      Activator.getDefault().addListener(listener);
   }

   @Override
   public void removeListener(SystemOutputListener listener) {
      Activator.getDefault().removeListener(listener);
   }

   @Override
   public void write(String input) {
      Activator.getDefault().write(input);
   }

}
