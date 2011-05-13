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
package org.eclipse.osee.framework.database.core;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ArtifactJoinQueryTest.class,
   CharJoinQueryTest.class,
   ExportImportJoinQueryTest.class,
   IdJoinQueryTest.class,
   SearchTagJoinQueryTest.class,
   TagQueueJoinQueryTest.class,
   TransactionJoinQueryTest.class})
/**
 * @author Roberto E. Escobar
 */
public class DatabaseCoreTestSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
   }
}
