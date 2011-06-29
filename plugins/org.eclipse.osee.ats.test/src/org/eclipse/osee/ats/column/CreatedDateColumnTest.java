/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.Date;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests CreatedDateColumn
 * @author Donald G. Dunne
 */
public class CreatedDateColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertNotNull(CreatedDateColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));
      Date date = CreatedDateColumn.getDate(codeArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CreatedDateColumn.getDateStr(codeArt));

      Artifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CreatedDateColumn.getDateStr(actionArt));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
