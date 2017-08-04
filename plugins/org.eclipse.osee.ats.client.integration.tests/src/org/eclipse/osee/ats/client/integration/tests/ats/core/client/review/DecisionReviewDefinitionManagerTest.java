/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.review;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.JaxAtsWorkDef;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.integration.AtsClientIntegrationTestSuite;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.workflow.transition.MockTransitionHelper;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewDefinitionManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link DecisionReviewDefinitionManager}
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionManagerTest extends DecisionReviewDefinitionManager {

   public static String WORK_DEF_FILE_NAME = "support/WorkDef_Team_DecisionReviewDefinitionManagerTest_toDecision.ats";
   public static String WORK_DEF_FILE_NAME_PREPARE =
      "support/WorkDef_Team_DecisionReviewDefinitionManagerTest_Prepare.ats";

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_ToDecision() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - ToDecision");

      try {
         String atsDsl = AWorkspace.getOseeInfResource(WORK_DEF_FILE_NAME, AtsClientIntegrationTestSuite.class);
         JaxAtsWorkDef jaxWorkDef = new JaxAtsWorkDef();
         jaxWorkDef.setName(AtsTestUtil.WORK_DEF_NAME);
         jaxWorkDef.setWorkDefDsl(atsDsl);
         AtsTestUtil.importWorkDefinition(jaxWorkDef);
         AtsClientService.get().clearCaches();
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Error importing " + WORK_DEF_FILE_NAME);
      }

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals("Implement State should have a single decision review definition", 1,
         teamArt.getWorkDefinition().getStateByName(TeamState.Implement.getName()).getDecisionReviews().size());
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         TeamState.Implement.getName(), Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         changes, TransitionOption.None);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results =
         transitionAndLogResults("testCreateDecisionReviewDuringTransition_ToDecision", transitionMgr);

      Assert.assertTrue(results.toString(), results.isEmpty());
      Assert.assertFalse(teamArt.isDirty());
      Assert.assertFalse(teamArt.getLog().isDirty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

      Assert.assertEquals(DecisionReviewState.Decision.getName(), decArt.getCurrentStateName());
      Assert.assertEquals("UnAssigned", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Transition.name(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is my review title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(TeamState.Implement.getName(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_Prepare() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - Prepare");

      try {
         String atsDsl = AWorkspace.getOseeInfResource(WORK_DEF_FILE_NAME_PREPARE, AtsClientIntegrationTestSuite.class);
         JaxAtsWorkDef jaxWorkDef = new JaxAtsWorkDef();
         jaxWorkDef.setName(AtsTestUtil.WORK_DEF_NAME);
         jaxWorkDef.setWorkDefDsl(atsDsl);
         AtsTestUtil.importWorkDefinition(jaxWorkDef);
         AtsClientService.get().clearCaches();
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Error importing " + WORK_DEF_FILE_NAME_PREPARE);
      }

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      MockTransitionHelper helper = new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt),
         TeamState.Implement.getName(), Arrays.asList(AtsClientService.get().getUserService().getCurrentUser

         ()), null, changes, TransitionOption.None);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results =
         transitionAndLogResults("testCreateDecisionReviewDuringTransition_Prepare", transitionMgr);

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

      Assert.assertEquals(DecisionReviewState.Prepare.getName(), decArt.getCurrentStateName());
      // Current user assigned if non specified
      Assert.assertEquals("Joe Smith", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Commit.name(), decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is the title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(TeamState.Implement.getName(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

   private TransitionResults transitionAndLogResults(String testName, IAtsTransitionManager transitionMgr) {
      System.err.println("entering test " + testName);
      TransitionResults results = transitionMgr.handleAllAndPersist();
      TransactionId transId = results.getTransactionId();
      System.err.println(String.format("transaction record [%s]", transId));
      List<ArtifactId> artIds = AtsClientService.get().getQueryService().getArtifactIdsFromQuery(
         "Select * From Osee_Txs Txs, Osee_Artifact Art Where Branch_Id = 570 And Transaction_Id = ? And Mod_Type = 1 And" //
            + " Txs.Gamma_Id = Art.Gamma_Id",
         transId);
      System.err.println(String.format("Artifact Ids %s", artIds));
      if (artIds.isEmpty()) {
         Artifact artifactFromId =
            ArtifactQuery.getArtifactFromId(artIds.iterator().next(), AtsClientService.get().getAtsBranch());
         AtsClientService.get().getLogger().error("Artifact  %s Type %s", artifactFromId.toStringWithId(),
            artifactFromId.getArtifactType());
      }
      System.err.println("leaving test " + testName + " with results " + results);
      return results;
   }

}
