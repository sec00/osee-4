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
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.workflow.transition;

import java.util.Collections;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class StateManagerTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testUpdateMetrics() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      IAtsStateManager stateMgr = teamWf.getStateMgr();

      stateMgr.updateMetrics(AtsTestUtil.getAnalyzeStateDef(), 1.1, 1, false);
      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());

      ITransitionHelper helper =
         new MockTransitionHelper("dodad", Collections.singletonList(teamWf),
            AtsTestUtil.getImplementStateDef().getName(),
            Collections.singleton(AtsClientService.get().getUserAdmin().getCurrentUser()), null, changes);
      TransitionManager manager = new TransitionManager(helper);
      TransitionResults results = manager.handleAll();
      changes.execute();
      Assert.assertTrue(results.isEmpty());

      stateMgr.updateMetrics(AtsTestUtil.getImplementStateDef(), 2.2, 1, false);
      helper =
         new MockTransitionHelper("dodad", Collections.singletonList(teamWf),
            AtsTestUtil.getCompletedStateDef().getName(),
            Collections.singleton(AtsClientService.get().getUserAdmin().getCurrentUser()), null, changes);
      manager = new TransitionManager(helper);
      results = manager.handleAll();
      changes.execute();

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals(3.3, HoursSpentUtil.getHoursSpentTotal(teamWf), 0.001);

      stateMgr.updateMetrics(AtsTestUtil.getCompletedStateDef(), -2.2, 1, false);
      Assert.assertEquals(1.1, HoursSpentUtil.getHoursSpentTotal(teamWf), 0.001);

      stateMgr.updateMetrics(AtsTestUtil.getCompletedStateDef(), -2.2, 1, false);
      Assert.assertEquals(0, HoursSpentUtil.getHoursSpentTotal(teamWf), 0.001);

      AtsTestUtil.cleanup();
   }
}
