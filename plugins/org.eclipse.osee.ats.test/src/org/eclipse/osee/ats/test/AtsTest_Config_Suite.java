/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.test;

import org.eclipse.osee.ats.test.artifact.AtsTeamDefintionToWorkflowTest;
import org.eclipse.osee.ats.test.config.AtsActionableItemToTeamDefinitionTest;
import org.eclipse.osee.ats.test.workflow.AtsWorkItemDefinitionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsWorkItemDefinitionTest.class,
   AtsActionableItemToTeamDefinitionTest.class,
   AtsTeamDefintionToWorkflowTest.class})
/**
 * This test suite contains test that can be run against any production db
 * 
 * @author Donald G. Dunne
 */
public class AtsTest_Config_Suite {
   // test provided above
}
