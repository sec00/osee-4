package org.eclipse.osee.ote.message.timer;

import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.ote.scheduler.OTETaskRegistration;

public class CancelTimerFromReg implements ICancelTimer {

   private OTETaskRegistration reg;

   public CancelTimerFromReg(OTETaskRegistration reg) {
      this.reg = reg;
   }

   @Override
   public void cancelTimer() {
      reg.unregister();
   }

}
