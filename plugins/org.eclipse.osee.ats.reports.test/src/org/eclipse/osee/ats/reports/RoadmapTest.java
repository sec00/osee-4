/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.reports;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.reports.agile.Roadmap;
import org.eclipse.osee.ats.reports.agile.Scrum;
import org.eclipse.osee.ats.reports.agile.UserStory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link Roadmap}
 * 
 * @author Ryan D. Brooks
 */
public class RoadmapTest {

   @BeforeClass
   public void setup() {
   }

   private Date getCloseDate(Artifact workflow) throws OseeCoreException {
      Date date = workflow.getSoleAttributeValue(AtsAttributeTypes.CompletedDate, null);
      if (date == null) {
         date = workflow.getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
      }
      return date;
   }

   private String getTheme(Artifact workflow) throws OseeCoreException {
      List<Artifact> groups = workflow.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Group);
      if (groups.isEmpty()) {
         return null;
      }
      if (groups.size() > 1) {
         System.out.println("more than one group from for " + workflow);
      }
      return groups.get(0).getName();
   }

   private UserStory createStory(Artifact workflow) throws OseeCoreException {
      return new UserStory(workflow.getName(), getPoints(workflow), getTheme(workflow), getCloseDate(workflow));
   }

   @Test
   public void testRoadmapUsingMockData(boolean includeClosed) throws OseeCoreException, ParseException {
      List<UserStory> closedStories;
      if (includeClosed) {
         List<Artifact> sprintWorkflows =
            ArtifactQuery.getArtifactListFromAttributeKeywords(CoreBranches.COMMON, "OSEE Sprint", true,
               DeletionFlag.EXCLUDE_DELETED, false, AtsAttributeTypes.SmaNote);

         closedStories = new ArrayList<UserStory>(sprintWorkflows.size());
         for (Artifact workflow : sprintWorkflows) {
            UserStory story = createStory(workflow);
            if (story.isClosed()) {
               closedStories.add(story);
            }
         }
      } else {
         closedStories = Collections.emptyList();
      }

      Artifact goal = ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Goal, "OSEE", CoreBranches.COMMON);
      List<Artifact> members = goal.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      List<UserStory> orderedBacklog = new ArrayList<UserStory>(members.size());
      for (Artifact workflow : members) {
         try {
            UserStory story = createStory(workflow);
            if (!story.isClosed()) {
               orderedBacklog.add(story);
            }
         } catch (AttributeDoesNotExist ex) {
            ex.printStackTrace();
            break;
         }
      }

      Roadmap roadmap = new Roadmap(new Scrum());

      roadmap.createSprints(new String[][] {
         {"2013-01-17", "2013-02-16"},
         {"2013-02-17", "2013-03-16"},
         {"2013-03-17", "2013-04-16"},
         {"2013-04-17", "2013-05-16"},
         {"2013-05-17", "2013-06-16"},
         {"2013-06-17", "2013-07-16"},
         {"2013-07-17", "2013-08-16"},
         {"2013-08-17", "2013-09-16"},
         {"2013-09-17", "2013-10-16"},
         {"2013-10-17", "2013-11-16"},
         {"2013-11-17", "2013-12-16"}});
      roadmap.populateSprints(closedStories, orderedBacklog);
      roadmap.printDetailedReport();

   }

   @Test
   public void testRoadmapUsingArtifacts(boolean includeClosed) throws OseeCoreException, ParseException {
      List<UserStory> closedStories;
      if (includeClosed) {
         List<Artifact> sprintWorkflows =
            ArtifactQuery.getArtifactListFromAttributeKeywords(CoreBranches.COMMON, "OSEE Sprint", true,
               DeletionFlag.EXCLUDE_DELETED, false, AtsAttributeTypes.SmaNote);

         closedStories = new ArrayList<UserStory>(sprintWorkflows.size());
         for (Artifact workflow : sprintWorkflows) {
            UserStory story = createStory(workflow);
            if (story.isClosed()) {
               closedStories.add(story);
            }
         }
      } else {
         closedStories = Collections.emptyList();
      }

      Artifact goal = ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Goal, "OSEE", CoreBranches.COMMON);
      List<Artifact> members = goal.getRelatedArtifacts(AtsRelationTypes.Goal_Member);
      List<UserStory> orderedBacklog = new ArrayList<UserStory>(members.size());
      for (Artifact workflow : members) {
         try {
            UserStory story = createStory(workflow);
            if (!story.isClosed()) {
               orderedBacklog.add(story);
            }
         } catch (AttributeDoesNotExist ex) {
            ex.printStackTrace();
            break;
         }
      }

      Roadmap roadmap = new Roadmap(new Scrum());

      roadmap.createSprints(new String[][] {
         {"2013-01-17", "2013-02-16"},
         {"2013-02-17", "2013-03-16"},
         {"2013-03-17", "2013-04-16"},
         {"2013-04-17", "2013-05-16"},
         {"2013-05-17", "2013-06-16"},
         {"2013-06-17", "2013-07-16"},
         {"2013-07-17", "2013-08-16"},
         {"2013-08-17", "2013-09-16"},
         {"2013-09-17", "2013-10-16"},
         {"2013-10-17", "2013-11-16"},
         {"2013-11-17", "2013-12-16"}});
      roadmap.populateSprints(closedStories, orderedBacklog);
      roadmap.printDetailedReport();

   }

   private int getPoints(Artifact workflow) throws OseeCoreException {
      String points = workflow.getSoleAttributeValue(AtsAttributeTypes.Points);
      try {
         return Integer.parseInt(points);
      } catch (NumberFormatException ex) {
         return 13;
      }
   }
}