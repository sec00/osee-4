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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.eclipse.osee.framework.core.exception.OseeStateException;

/**
 * @author Ryan D. Brooks
 */
public final class SprintDay {
   private final HashMap<UserStory, Double> storiesToPointsWorked = new HashMap<UserStory, Double>();
   private double allocatedPoints;
   private final Sprint sprint;
   private final int dayNumber;
   private final Calendar date;
   private int cumulativePoints;

   /**
    * @param date must have the time feild smaller than a day set to zero
    * @param sprint
    * @param dayNumber
    */
   public SprintDay(Calendar date, Sprint sprint, int dayNumber) {
      this.date = date;
      this.sprint = sprint;
      this.dayNumber = dayNumber;
   }

   public int getDayNumber() {
      return dayNumber;
   }

   public double getAllocatedPoints() {
      return allocatedPoints;
   }

   /**
    * - points completed by story for this day
    * 
    * @return
    */
   public HashMap<UserStory, Double> getStoriesWorked() {
      return storiesToPointsWorked;
   }

   /**
    * - first check if story has already been allocated to this day; check if day can support allocated of this many
    * points
    * 
    * @param story
    * @param pointsToAllocate
    * @throws OseeStateException
    */

   public boolean isStoryAlreadyAllocated(UserStory story) {
      return storiesToPointsWorked.containsKey(story);
   }

   public void allocate(UserStory story, double pointsToAllocate) throws OseeStateException {
      if (isStoryAlreadyAllocated(story)) {
         throw new OseeStateException("%s is already allocated to %s", story, this);
      }
      storiesToPointsWorked.put(story, pointsToAllocate);
      allocatedPoints += pointsToAllocate;
   }

   public int getCumulativePoints() {
      return cumulativePoints;
   }

   public void setCumulativePoints(int cumulativePoints) {
      this.cumulativePoints = cumulativePoints;
   }

   public Sprint getSprint() {
      return sprint;
   }

   public boolean isPlanningDay() {
      return dayNumber == 0;
   }

   public boolean isAfter(SprintDay day) {
      //      if (day.sprint.equals(sprint)) {
      //         return dayNumber > day.dayNumber;
      //      }
      //      return sprint.isAfter(day.sprint);
      return date.after(day.date);
   }

   public boolean isOnOrAfter(Calendar cal) {
      return equals(cal) || date.after(cal);
   }

   public boolean isOnOrBefore(SprintDay day) {
      if (day.sprint.equals(sprint)) {
         return dayNumber <= day.dayNumber;
      }
      return sprint.equals(day.sprint) || day.sprint.isAfter(sprint);
   }

   public boolean isBefore(Calendar cal) {
      return date.before(cal);
   }

   public static Calendar getDateOnly(Calendar dateAndTime) {
      Calendar date = ((Calendar) dateAndTime.clone());
      date.set(Calendar.HOUR, 0);
      date.set(Calendar.MINUTE, 0);
      date.set(Calendar.SECOND, 0);
      date.set(Calendar.MILLISECOND, 0);
      return date;
   }

   public static Calendar getDateOnly(Date dateAndTime) {
      if (dateAndTime == null) {
         return null;
      }
      Calendar cal = Calendar.getInstance();
      cal.setTime(dateAndTime);
      return getDateOnly(cal);
   }

   @Override
   public String toString() {
      return String.format("day %d, %tF", dayNumber, date);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = prime + dayNumber;
      result = prime * result + ((sprint == null) ? 0 : sprint.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof SprintDay) {
         SprintDay day = (SprintDay) obj;
         return sprint.equals(day.sprint) && dayNumber == day.dayNumber;
      }
      if (obj instanceof Calendar) {
         Calendar cal = getDateOnly((Calendar) obj);
         return date.equals(cal);
      }
      return false;
   }
}