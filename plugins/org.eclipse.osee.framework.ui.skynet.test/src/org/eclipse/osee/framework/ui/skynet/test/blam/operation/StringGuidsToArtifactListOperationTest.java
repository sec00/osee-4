package org.eclipse.osee.framework.ui.skynet.test.blam.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.operation.StringOperationLogger;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.framework.ui.skynet.blam.operation.StringGuidsToArtifactListOperation;
import org.eclipse.osee.framework.ui.skynet.widgets.IXWidgetInputAddable;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * {@link StringGuidsToArtifactListOperation}
 *
 * @author Karol M. Wilk
 */
public class StringGuidsToArtifactListOperationTest {

   private static final String SAMPLE_SEPARATOR = "\r\n";
   private static final int capacity = 10;
   private static Branch testBranch;

   private final static Collection<Object> artifacts = new ArrayList<Object>(capacity);
   private final static String[] guids = new String[capacity];
   private final String invalidGuid = String.format("4F@3g@#$G@GZS%s", SAMPLE_SEPARATOR);

   @BeforeClass
   public static void setUpOnce() throws OseeCoreException {
      testBranch =
         BranchManager.createWorkingBranch(DemoSawBuilds.SAW_Bld_1,
            StringGuidsToArtifactListOperationTest.class.getSimpleName() + " Branch",
            UserManager.getUser(SystemUser.OseeSystem));

      for (int i = 0; i < capacity; ++i) {
         Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, testBranch, "Test" + i);
         guids[i] = artifact.getGuid();
         artifact.persist("Save");
         artifacts.add(artifact);
      }
   }

   @AfterClass
   public static void tearDownOnce() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(testBranch, true));
   }

   @Test
   public void test_doWork_findCreatedArtifacts() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(new StringGuidsToArtifactListOperation(new StringOperationLogger(),
         generateSampleClipboardContent(), testBranch, widgetMock_Equal));
   }

   @Test
   public void test_doWork_guidGarbageData() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(new StringGuidsToArtifactListOperation(new StringOperationLogger(),
         generateGarbageClipboardContent(), testBranch, widgetMock_2Uniques));
   }

   @Test
   public void test_doWork_nullClipboardData() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(new StringGuidsToArtifactListOperation(new StringOperationLogger(), null,
         testBranch, widgetMock_Equal));
   }

   private String generateGarbageClipboardContent() {
      StringBuilder builder = new StringBuilder(guids.length);
      builder.append(invalidGuid);
      for (int i = 0; i < guids.length; i++) {
         switch (i) {
            case 1:
            case 5:
               //inject at some random places
               builder.append(invalidGuid);
               break;
            default:
               builder.append(String.format("%s%s", guids[i], SAMPLE_SEPARATOR));
               break;
         }
      }
      return builder.toString();
   }

   private String generateSampleClipboardContent() {
      StringBuilder builder = new StringBuilder();
      for (String guid : guids) {
         builder.append(String.format("%s%s", guid, SAMPLE_SEPARATOR));
      }
      return builder.toString();
   }

   private final IXWidgetInputAddable widgetMock_Equal = new IXWidgetInputAddable() {
      @Override
      public void addToInput(Collection<Object> objects) {
         List<Object> uniques = Collections.setComplement(new HashSet<Object>(objects), new HashSet<Object>(artifacts));
         Assert.assertTrue(uniques.isEmpty());
      }
   };

   private final IXWidgetInputAddable widgetMock_2Uniques = new IXWidgetInputAddable() {
      @Override
      public void addToInput(Collection<Object> objects) {
         List<Object> uniques = Collections.setComplement(new HashSet<Object>(artifacts), new HashSet<Object>(objects));
         Assert.assertTrue(uniques.size() == 2); //generateGarbageClipboardContent() takes out 1 and 5
      }
   };

}