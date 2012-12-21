/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.reports.agile;

/**
 * @author Ryan D. Brooks
 */
public final class Scrum {

   public double getAveragePointsPerManDay() {
      return 0.5;
   }

   public double getAverageDailyVelocity() {
      return getAvailableManPower(null) * getAveragePointsPerManDay();
   }

   public double getWalkupPointsPerDay() {
      throw new UnsupportedOperationException();
   }

   /**
    * @param sprint
    * @return the number of average number of heads for the given sprint
    */
   public double getAvailableManPower(Sprint sprint) {
      return 8.0;
   }

   @Override
   public String toString() {
      return String.format("");
   }
}