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

/**
 * @author Ryan D. Brooks
 */
public final class UserStory implements Comparable<UserStory> {
   private final Calendar closeDate;
   private final int points;
   private final String theme;
   private final String name;

   private Sprint sprint;
   private SprintDay closeDay;
   private String workpackage;

   public UserStory(String name, int points, String theme, Calendar closeDate) {
      this.name = name;
      this.points = points;
      this.theme = theme == null ? "Uncategorized" : theme;
      this.closeDate = closeDate == null ? null : SprintDay.getDateOnly(closeDate);
   }

   public UserStory(String name, int points, String theme, Date closeDate) {
      this(name, points, theme, SprintDay.getDateOnly(closeDate));
   }

   public UserStory(String name, int points) {
      this(name, points, null, (Calendar) null);
   }

   public Sprint getSprint() {
      return sprint;
   }

   public String getName() {
      return name;
   }

   void assignSprintAndCloseDay(Sprint sprint, SprintDay closeDay) {
      this.sprint = sprint;
      this.closeDay = closeDay;
   }

   /**
    * @return if closed returns the sprint day corresponding to the cancelled or completed date, otherwise returns
    * sprint end date
    */
   public SprintDay getCloseDay() {
      return closeDay;
   }

   Calendar getCloseDate() {
      return closeDate;
   }

   public String getMainTheme() {
      return theme;
   }

   public String getWorkPackage() {
      return workpackage;
   }

   public void setWorkPackage(String workpackage) {
      this.workpackage = workpackage;
   }

   public int getPoints() {
      return points;
   }

   public boolean isClosed() {
      return closeDate != null;
   }

   public boolean isWalkup() {
      return false;
   }

   @Override
   public int compareTo(UserStory story) {
      if (isClosed() == story.isClosed()) {
         if (isClosed()) {
            return closeDate.compareTo(story.closeDate);
         } else {
            return 0;
         }
      } else {
         return isClosed() ? 1 : -1;
      }
   }

   @Override
   public String toString() {
      return String.format("\"%s\" %d pt, closeDate=%tF", name, points, closeDate);
   }
}