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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeStateException;

/**
 * @author Ryan D. Brooks
 */
public final class Sprint {
   private final LinkedList<SprintDay> sprintDays = new LinkedList<SprintDay>();
   private final Roadmap roadmap;
   private double plannedPoints;
   private final LinkedList<UserStory> sprintStories = new LinkedList<UserStory>();
   private final int sprintNumber;
   private static final double epsilon = 0.0000001;

   public Sprint(Roadmap roadmap, int sprintNumber) {
      this.roadmap = roadmap;
      plannedPoints = -1;
      this.sprintNumber = sprintNumber;
   }

   /**
    * removes selected stories from the backlog and adds them to this sprint
    * 
    * @param roadmapStories
    * @throws OseeStateException
    */
   private void selectStories(LinkedHashSet<UserStory> roadmapStories) throws OseeStateException {
      int cumulativePoints = 0;
      int dayIndex = 0;

      for (UserStory story : roadmapStories) {
         cumulativePoints += story.getPoints();
         if (outsideSprint(story, cumulativePoints)) {
            break;
         }

         dayIndex = assignSprintAndCloseDay(story, dayIndex);
         story.getCloseDay().setCumulativePoints(cumulativePoints);
         sprintStories.add(story);
      }
      roadmapStories.removeAll(sprintStories);
   }

   private boolean outsideSprint(UserStory story, int cumulativePoints) {
      if (story.isClosed()) {
         // must use date because closeDay is still null at this point
         return getEndDay().isBefore(story.getCloseDate());
      }
      return cumulativePoints > getPlannedPoints() || getEndDay().isBefore(roadmap.getSimulationDate());
   }

   private int assignSprintAndCloseDay(UserStory story, int dayIndex) throws OseeStateException {
      if (story.isClosed()) {
         Calendar closeDate = story.getCloseDate();
         for (; dayIndex < sprintDays.size(); dayIndex++) {
            SprintDay day = sprintDays.get(dayIndex);
            if (day.isOnOrAfter(closeDate)) {
               story.assignSprintAndCloseDay(this, day);
               return dayIndex;
            }
         }
         throw new OseeStateException("close date %tF is outside of sprint with end date %s", closeDate, getEndDay());
      } else {
         story.assignSprintAndCloseDay(this, getEndDay());
         return dayIndex;
      }
   }

   public void createSprintDays(Calendar startDate, Calendar endDate) {
      sprintDays.clear();
      int sprintDayNum = 1;

      Calendar stopDate = SprintDay.getDateOnly(endDate);
      startDate = SprintDay.getDateOnly(startDate);
      stopDate.add(Calendar.DAY_OF_YEAR, 1);

      for (Calendar date = startDate; date.before(stopDate); date.add(Calendar.DAY_OF_YEAR, 1)) {
         if (isSprintDay(date)) {
            sprintDays.add(new SprintDay((Calendar) date.clone(), this, sprintDayNum++));
         }
      }
   }

   public void layoutUserStories(LinkedHashSet<UserStory> roadmapStories) throws OseeStateException {
      selectStories(roadmapStories);
      setMissingCumulativePoints();

      for (UserStory story : sprintStories) {
         allocateStoryPoints(story);
      }
   }

   private void setMissingCumulativePoints() {
      int previousDayCumulativePoints = 0;
      for (SprintDay day : sprintDays) {
         if (day.getCumulativePoints() == 0) {
            day.setCumulativePoints(previousDayCumulativePoints);
         }
         previousDayCumulativePoints = day.getCumulativePoints();
      }
   }

   private void allocateStoryPoints(UserStory story) throws OseeStateException {
      SprintDay closeDay = story.getCloseDay();
      double avgPointsPerDay = ((double) closeDay.getCumulativePoints()) / closeDay.getDayNumber();
      double allocatedPoints = 0;

      while (lessThan(allocatedPoints, story.getPoints())) {
         SprintDay day = getNextAvailableDay(story, avgPointsPerDay);
         double remainingPoints = story.getPoints() - allocatedPoints;
         double idealPointsPerDay = getIdealPoints(day, story, remainingPoints);
         double pointsForThisDay = Math.min(idealPointsPerDay, avgPointsPerDay - day.getAllocatedPoints());

         day.allocate(story, pointsForThisDay);
         allocatedPoints += pointsForThisDay;
      }
   }

   // TODO: move this method to the proper utility
   private static boolean lessThan(double doubleA, double doubleB) {
      return doubleA + epsilon < doubleB;
   }

   private double getIdealPoints(SprintDay day, UserStory story, double remainingPoints) {
      int remainingDays = story.getCloseDay().getDayNumber() - day.getDayNumber() + 1;
      return remainingPoints / remainingDays;
   }

   /**
    * - next day that has not been fully allocated and not allocated to this story
    * 
    * @param story
    * @return
    * @throws OseeStateException
    */
   private SprintDay getNextAvailableDay(UserStory story, double avgPointsPerDay) throws OseeStateException {
      for (SprintDay day : sprintDays) {
         if (day.getAllocatedPoints() < avgPointsPerDay && !day.isStoryAlreadyAllocated(story)) {
            if (!day.isPlanningDay() || story.getCloseDay().isPlanningDay()) {
               return day;
            }
         }
      }
      throw new OseeStateException("Sprint %s has no more available days for story %s", this, story);
   }

   public List<SprintDay> getSprintDays() {
      return sprintDays;
   }

   private boolean isSprintDay(Calendar date) {
      int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
      return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY;
   }

   public void setPlannedPoints(double plannedPoints) {
      this.plannedPoints = plannedPoints;
   }

   public double getPlannedPoints() {
      if (isNotPlanned()) {
         return sprintDays.size() * roadmap.getScrum().getAverageDailyVelocity();
      }
      return plannedPoints;
   }

   /**
    * @return points set aside for walk-up work for this sprint
    */
   public double getWalkupPoints() {
      return sprintDays.size() * roadmap.getScrum().getWalkupPointsPerDay();
   }

   public SprintDay getStartDay() {
      return sprintDays.getFirst();
   }

   public SprintDay getEndDay() {
      return sprintDays.getLast();
   }

   public boolean isNotPlanned() {
      return plannedPoints == -1;
   }

   public boolean isAfter(Sprint sprint) {
      return getStartDay().isAfter(sprint.getStartDay());
   }

   public int getSprintNumber() {
      return sprintNumber;
   }

   @Override
   public String toString() {
      StringBuilder strB = new StringBuilder(sprintDays.size() * 20);
      strB.append("Sprint ");
      strB.append(sprintNumber);
      strB.append('\n');
      for (SprintDay day : sprintDays) {
         strB.append(day);
         strB.append(',');
         strB.append(day.getAllocatedPoints());
         strB.append(", ");
         strB.append(day.getCumulativePoints());
         strB.append('\n');
      }
      return strB.toString();
   }
}