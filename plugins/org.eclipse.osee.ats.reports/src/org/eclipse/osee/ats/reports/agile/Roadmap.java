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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.exception.OseeStateException;

/**
 * @author Ryan D. Brooks
 */
public final class Roadmap {
   private LinkedHashSet<UserStory> stories;
   private final List<Sprint> sprints = new ArrayList<Sprint>();
   private final Scrum scrum;

   public Roadmap(Scrum scrum) {
      this.scrum = scrum;
   }

   public List<Sprint> createSprints(String[][] sprintDates) throws ParseException {
      int sprintNumber = 1;
      for (String[] datePair : sprintDates) {
         Sprint sprint = new Sprint(this, sprintNumber++);
         sprint.createSprintDays(toCal(datePair[0]), toCal(datePair[1]));
         sprints.add(sprint);
      }

      return sprints;
   }

   /**
    * createSprints must be called before populateSprints
    * 
    * @param closedStories
    * @param orderedBacklog
    * @throws OseeStateException
    */
   public void populateSprints(List<UserStory> closedStories, List<UserStory> orderedBacklog) throws OseeStateException {
      orderStories(closedStories, orderedBacklog);

      for (Sprint sprint : sprints) {
         sprint.layoutUserStories(stories);
      }
   }

   public Calendar getSimulationDate() {
      return SprintDay.getDateOnly(Calendar.getInstance());
   }

   private void orderStories(List<UserStory> closedStories, List<UserStory> orderedBacklog) throws OseeStateException {
      int setSize = (closedStories.size() + orderedBacklog.size()) * 4 / 3 + 1;
      stories = new LinkedHashSet<UserStory>(setSize);

      Iterator<UserStory> iter = closedStories.iterator();
      while (iter.hasNext()) {
         UserStory story = iter.next();
         if (!story.isClosed()) {
            iter.remove();
            throw new OseeStateException("Story %s was includied in closedStories, but is still open.", story);
         }
      }

      Collections.sort(closedStories);
      stories.addAll(closedStories);
      stories.addAll(orderedBacklog);
   }

   @Override
   public String toString() {
      return sprints.toString();
   }

   /**
    * @param workPackage
    * @return the percent complete of the given workpackage based on the weighted average of the points for all user
    * stories in this roadmap with this workpackage
    */
   public double getPercentCompleteFor(String workPackage) {
      throw new UnsupportedOperationException();
   }

   private static void test() throws ParseException, OseeStateException {
      List<UserStory> closedStories = new ArrayList<UserStory>();
      addTestClosedStories(closedStories);

      List<UserStory> orderedBacklog = new ArrayList<UserStory>();
      orderedBacklog.add(createStory("Bug y", 2, "OSEE Fixes", null));

      Roadmap roadmap = new Roadmap(new Scrum());

      roadmap.createSprints(new String[][] {
         {"2012-01-06", "2012-01-25"},
         {"2012-01-26", "2012-02-22"},
         {"2012-02-23", "2012-03-21"},
         {"2012-03-22", "2012-04-26"},
         {"2012-04-27", "2012-06-03"},
         {"2012-06-04", "2012-07-04"},
         {"2012-07-05", "2012-08-01"},
         {"2012-08-02", "2012-09-04"},
         {"2012-09-05", "2012-10-01"},
         {"2012-10-02", "2012-10-28"},
         {"2012-10-29", "2012-11-14"},
         {"2012-11-15", "2012-12-09"},
         {"2012-12-10", "2012-12-31"}});
      roadmap.populateSprints(closedStories, orderedBacklog);
      roadmap.printDetailedReport();
   }

   private static void addTestClosedStories(List<UserStory> closedStories) throws ParseException {
      closedStories.add(createStory("Feature a", 4, "OSEE Features", "2012-11-01"));
      closedStories.add(createStory("Feature b", 4, "OSEE Features", "2012-11-20"));
   }

   public void printSummary() {
      for (Sprint sprint : sprints) {
         System.out.println(sprint);
      }
   }

   public void printDetailedReport() {
      for (Sprint sprint : sprints) {
         for (SprintDay day : sprint.getSprintDays()) {
            HashMap<UserStory, Double> stories = day.getStoriesWorked();
            for (Entry<UserStory, Double> entry : stories.entrySet()) {
               UserStory story = entry.getKey();
               System.out.printf("Sprint %d,%s,%s,%s,\"%s\"\n", sprint.getSprintNumber(), day, story.getMainTheme(),
                  entry.getValue(), story.getName());
            }
         }
      }
   }

   public Scrum getScrum() {
      return scrum;
   }

   private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

   private static Date toDate(String dateStr) throws ParseException {
      return dateStr == null ? null : sdf.parse(dateStr);
   }

   private static Calendar toCal(String dateStr) throws ParseException {
      Calendar cal = Calendar.getInstance();
      cal.setTime(toDate(dateStr));
      return cal;
   }

   private static UserStory createStory(String name, int points, String theme, String closeDate) throws ParseException {
      return new UserStory(name, points, theme, toDate(closeDate));
   }
}