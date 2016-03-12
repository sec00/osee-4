package org.eclipse.ote.scheduler;

public class OTEClockSimulated extends OTEClock {

   public long nanoTime() {
      return currentTimeMillis()*1000000;
   }

   public long currentTimeMillis() {
      return tick*resolution;
   }

}
