/*
 * Created on Mar 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.cases;

import org.eclipse.osee.framework.skynet.core.event.InternalEventManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * event loopback will test that remote messages get processed and treated like local messages by turning off local and
 * enabling remote to be loop-ed back without another client. same tests as base-class should still pass
 * 
 * @author Donald G. Dunne
 */
public class ArtifactEventManagerLoopbackTest extends ArtifactEventManagerTest {

   @BeforeClass
   public static void setUp() {
      InternalEventManager.setEnableRemoteEventLoopback(true);
   }

   @AfterClass
   public static void tearDown() {
      InternalEventManager.setEnableRemoteEventLoopback(false);
   }
}
