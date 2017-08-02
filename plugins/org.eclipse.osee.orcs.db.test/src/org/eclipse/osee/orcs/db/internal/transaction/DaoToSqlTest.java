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
package org.eclipse.osee.orcs.db.internal.transaction;

import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link DaoToSql}
 *
 * @author Roberto E. Escobar
 */
public class DaoToSqlTest {
   private static final GammaId gamma123 = GammaId.valueOf(123);

   @Mock
   private DataProxy proxy;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testGetGammaIdAndIsNewGammaId() {
      boolean isNewGammaId = true;
      GammaId gammaId = GammaId.valueOf(2345);

      DaoToSql dao1 = new DaoToSql(gammaId, proxy, isNewGammaId);
      Assert.assertEquals(gammaId, dao1.getGammaId());
      Assert.assertEquals(true, dao1.hasNewGammaId());

      DaoToSql dao2 = new DaoToSql(gamma123, proxy, false);
      Assert.assertEquals(gamma123, dao2.getGammaId());
      Assert.assertEquals(false, dao2.hasNewGammaId());
   }

   @Test
   public void testGetUriAndValue() throws OseeCoreException {
      String value = "theValue";
      String uri = "theUri";
      Object[] data = new Object[] {value, uri};

      Mockito.when(proxy.getData()).thenReturn(data);

      DaoToSql dao = new DaoToSql(gamma123, proxy, false);
      Assert.assertEquals(uri, dao.getUri());
      Assert.assertEquals(value, dao.getValue());
   }

   @Test
   public void testValueAndUriNull() throws OseeCoreException {
      String value = "theValue";
      String uri = null;
      Object[] data = new Object[] {value, uri};

      Mockito.when(proxy.getData()).thenReturn(data);

      DaoToSql dao = new DaoToSql(gamma123, proxy, false);
      Assert.assertEquals("", dao.getUri());
      Assert.assertEquals(value, dao.getValue());
   }

   @Test
   public void testUriAndValueNull() throws OseeCoreException {
      String value = "theValue";
      String uri = null;
      Object[] data = new Object[] {value, uri};

      Mockito.when(proxy.getData()).thenReturn(data);

      DaoToSql dao = new DaoToSql(gamma123, proxy, false);
      Assert.assertEquals("", dao.getUri());
      Assert.assertEquals(value, dao.getValue());
   }

   @Test
   public void testPersist() throws OseeCoreException {
      DaoToSql dao = new DaoToSql(gamma123, proxy, true);
      dao.persist();
      Mockito.verify(proxy).persist(gamma123.getId());

      Mockito.reset(proxy);

      DaoToSql dao2 = new DaoToSql(gamma123, proxy, false);
      dao2.persist();
      Mockito.verify(proxy, Mockito.times(0)).persist(gamma123.getId());
   }

   @Test
   public void testPurge() throws OseeCoreException {
      DaoToSql dao = new DaoToSql(gamma123, proxy, true);
      dao.rollBack();
      Mockito.verify(proxy).purge();

      Mockito.reset(proxy);

      DaoToSql dao2 = new DaoToSql(gamma123, proxy, false);
      dao2.rollBack();
      Mockito.verify(proxy, Mockito.times(0)).purge();
   }
}