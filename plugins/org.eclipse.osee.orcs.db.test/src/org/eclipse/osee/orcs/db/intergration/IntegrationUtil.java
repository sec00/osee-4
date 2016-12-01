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
package org.eclipse.osee.orcs.db.intergration;

import static org.junit.Assert.assertEquals;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * @author Roberto E. Escobar
 */
public class IntegrationUtil {

   private static final Comparator<OrcsData> SORT_BY_LOCAL_ID = new IdComparator();

   public static TestRule integrationRule(Object testObject) {
      return RuleChain.outerRule(new OseeDatabase("orcs.jdbc.service")).around(new OsgiRule(testObject));
   }

   public static void sort(List<? extends OrcsData> data) {
      Collections.sort(data, SORT_BY_LOCAL_ID);
   }

   public static void verifyData(ArtifactData data, Object... values) {
      int index = 0;
      assertEquals(data.getLocalId(), values[index++]);
      assertEquals(data.getGuid(), values[index++]);

      verifyData(data, index, values);
   }

   public static void verifyData(AttributeData data, Object... values) throws OseeCoreException {
      int index = 0;
      assertEquals(data.getLocalId(), values[index++]);
      assertEquals(data.getArtifactId(), values[index++]);

      index = verifyData(data, index, values);

      Object[] proxied = data.getDataProxy().getData();
      assertEquals(values[index++], proxied[0]); // value
      assertEquals(values[index++], proxied[1]); // uri
   }

   public static void verifyData(RelationData data, Object... values) {
      int index = 0;
      assertEquals(data.getLocalId(), values[index++]);

      assertEquals(data.getArtIdA(), values[index++]);
      assertEquals(data.getArtIdB(), values[index++]);
      assertEquals(data.getRationale(), values[index++]);

      verifyData(data, index, values);
   }

   private static int verifyData(OrcsData orcsData, int index, Object... values) {
      assertEquals(orcsData.getModType(), values[index++]);
      assertEquals(orcsData.getTypeUuid(), values[index++]);

      VersionData version = orcsData.getVersion();
      assertEquals(version.getBranchId(), ((BranchId) values[index++]).getId());
      assertEquals(version.getTransactionId(), values[index++]);
      assertEquals(version.getStripeId(), TransactionId.SENTINEL);
      assertEquals(version.getGammaId(), values[index++]);
      return index;
   }

   private static final class IdComparator implements Comparator<OrcsData> {

      @Override
      public int compare(OrcsData arg0, OrcsData arg1) {
         return arg0.getLocalId() - arg1.getLocalId();
      }
   };
}