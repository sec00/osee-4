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
package org.eclipse.osee.orcs.core.internal.transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Test Case for {@link TransactionFactoryImpl}
 *
 * @author Roberto E. Escobar
 */
public class TransactionFactoryImplTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private OrcsSession session;
   @Mock private TxDataManager txDataManager;
   @Mock private TxCallableFactory txCallableFactory;
   @Mock private OrcsApi orcsApi;
   @Mock private OrcsBranch orcsBranch;
   @Mock private TxDataStore txDataStore;
   @Mock private KeyValueOps keyValueOps;
   @Mock private UserId expectedAuthor;
   @Mock private TxData txData;
   @Mock private QueryFactory queryFactory;
   @Mock private BranchQuery branchQuery;

   private final Long author = 2L;
   // @formatter:on

   private final BranchId expectedBranch = CoreBranches.COMMON;
   private TransactionFactoryImpl factory;

   @Before
   public void init() {
      initMocks(this);
      when(orcsApi.getQueryFactory()).thenReturn(queryFactory);
      when(queryFactory.branchQuery()).thenReturn(branchQuery);
      when(branchQuery.andId(expectedBranch)).thenReturn(branchQuery);
      when(branchQuery.exists()).thenReturn(true);
      factory = new TransactionFactoryImpl(session, txDataManager, txCallableFactory, orcsApi, orcsBranch, keyValueOps,
         txDataStore);
      when(expectedAuthor.getLocalId()).thenReturn(5L);
   }

   @Test
   public void testNullAuthor() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("author cannot be null");
      factory.createTransaction(expectedBranch, (Long) null, "my comment");
   }

   @Test
   public void testNullComment() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("comment cannot be null");
      factory.createTransaction(expectedBranch, expectedAuthor, null);
   }

   @Test
   public void testEmptyComment() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("comment cannot be empty");
      factory.createTransaction(expectedBranch, expectedAuthor, "");
   }

   @Test
   public void testCreateTransaction() {
      String expectedComment = "This is my comment";

      when(txDataManager.createTxData(session, expectedBranch)).thenReturn(txData);
      when(txData.getAuthor()).thenReturn(author);
      when(txData.getBranch()).thenReturn(expectedBranch);
      when(txData.getComment()).thenReturn(expectedComment);

      TransactionBuilder tx = factory.createTransaction(expectedBranch, expectedAuthor, expectedComment);
      assertNotNull(tx);
      assertEquals(expectedBranch, tx.getBranch());
      assertEquals(expectedComment, tx.getComment());
   }
}
