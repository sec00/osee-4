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
package org.eclipse.osee.orcs.db.internal.loader;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.BranchIdProvider;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link DataLoaderFactoryImpl}
 * 
 * @author Roberto E. Escobar
 */
public class DataLoaderFactoryImplTest {

   //@formatter:off
    @Mock private Log logger;

   @Mock private IOseeDatabaseService dbService;
   @Mock private IOseeStatement chStmt;
   
   @Mock private IdentityManager identityService;
   @Mock private SqlProvider sqlProvider;
   
   @Mock private LoadDataHandler builder;
   
   @Mock private OrcsObjectFactory rowDataFactory;
   @Mock private BranchIdProvider branchIdProvider;
   @Mock private HasCancellation cancellation;
   
    @Captor private ArgumentCaptor<LoadSqlContext> contextCaptor;
    @Captor private ArgumentCaptor<ArtifactJoinQuery> joinCaptor;
    @Captor private ArgumentCaptor<CriteriaOrcsLoad> criteriaCaptor;
    @Captor private ArgumentCaptor<LoadDescription> descriptionCaptor;
   
   @Mock private OrcsSession session;
   //@formatter:on

   private final static long EXPECTED_BRANCH_ID = 65;
   private final static int EXPECTED_TX_ID = 45678;
   private final static int EXPECTED_HEAD_TX_ID = 50000;
   private final static IOseeBranch BRANCH = CoreBranches.COMMON;

   private DataLoaderFactory factory;
   private SqlObjectLoader spyLoader;

   @Before
   public void setUp() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      LoaderModule module = new LoaderModule(logger, dbService, identityService, sqlProvider, null);
      SqlObjectLoader loader = module.createSqlObjectLoader(rowDataFactory);

      spyLoader = spy(loader);
      factory = module.createDataLoaderFactory(spyLoader, branchIdProvider);

      when(branchIdProvider.getBranchId(BRANCH)).thenReturn(EXPECTED_BRANCH_ID);
      when(sqlProvider.getSql(OseeSql.QUERY_BUILDER)).thenReturn("/*+ ordered */");

      when(dbService.getStatement()).thenReturn(chStmt);
      when(dbService.runPreparedQueryFetchObject(eq(-1), Matchers.anyString(), eq(BRANCH.getUuid()))).thenReturn(
         EXPECTED_HEAD_TX_ID);
   }

   @Test
   public void testLoadFull() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ALL;

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX_ID);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      // @formatter:off
      verify(spyLoader, times(0)).loadHeadTransactionId(BRANCH);
      verify(spyLoader, times(1)).loadArtifacts(eq(builder), criteriaCaptor.capture(), contextCaptor.capture(), eq(200));
      verify(spyLoader, times(1)).loadAttributes(eq(builder), criteriaCaptor.capture(), contextCaptor.capture(), eq(200));
      verify(spyLoader, times(1)).loadRelations(eq(builder), criteriaCaptor.capture(), contextCaptor.capture(), eq(200));
      // @formatter:on
   }

   @Test
   public void testLoadArtifactIds() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_DATA;
      String expected = "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
      " jart1.art_id, art1.art_type_id, art1.guid\n" + //
      " FROM \n" + //
      "osee_join_artifact jart1, osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.art_id = jart1.art_id AND jart1.query_id = ? AND art1.gamma_id = txs1.gamma_id\n" + //
      " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
      " ORDER BY txs1.branch_id, jart1.art_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactIncludeDeleted() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_DATA;
      String expected = "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
      " jart1.art_id, art1.art_type_id, art1.guid\n" + //
      " FROM \n" + //
      "osee_join_artifact jart1, osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.art_id = jart1.art_id AND jart1.query_id = ? AND art1.gamma_id = txs1.gamma_id\n" + //
      " AND txs1.tx_current IN (1, 2, 3) AND txs1.branch_id = jart1.branch_id\n" + //
      " ORDER BY txs1.branch_id, jart1.art_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactHistorical() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_DATA;
      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.transaction_id as stripe_transaction_id,\n" + //
         " jart1.art_id, art1.art_type_id, art1.guid\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.art_id = jart1.art_id AND jart1.query_id = ? AND art1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.transaction_id <= jart1.transaction_id AND txs1.mod_type != 3 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX_ID);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactHistoricalIncludeDeleted() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_DATA;
      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.transaction_id as stripe_transaction_id,\n" + //
         " jart1.art_id, art1.art_type_id, art1.guid\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.art_id = jart1.art_id AND jart1.query_id = ? AND art1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.transaction_id <= jart1.transaction_id AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX_ID);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX_ID, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testLoadAttributes() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected = "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
      " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
      " FROM \n" + //
      "osee_join_artifact jart1, osee_attribute att1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
      " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
      " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadAttributesWithType() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_attribute att1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.attr_type_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected,
         CoreAttributeTypes.Annotation.getGuid());
   }

   @Test
   public void testLoadAttributesWithTypes() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_join_id jid1, osee_attribute att1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.attr_type_id = jid1.id AND jid1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation, CoreAttributeTypes.Category);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID),
         list(data(CoreAttributeTypes.Annotation.getGuid(), CoreAttributeTypes.Category.getGuid())));
   }

   @Test
   public void testLoadAttributesWithId() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected = "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
      " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
      " FROM \n" + //
      "osee_join_artifact jart1, osee_attribute att1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.attr_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
      " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
      " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeIds(45);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected, 45);
   }

   @Test
   public void testLoadAttributesWithIds() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_join_id jid1, osee_attribute att1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.attr_id = jid1.id AND jid1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeIds(45, 55);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID), list(data(45, 55)));
   }

   @Test
   public void testLoadAttributesWithIdsAndTypes() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_join_id jid1, osee_join_id jid2, osee_attribute att1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.attr_id = jid1.id AND jid1.query_id = ? AND att1.attr_type_id = jid2.id AND jid2.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeIds(45, 55);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation, CoreAttributeTypes.Category);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID, JQID),
         list(data(45, 55), data(CoreAttributeTypes.Annotation.getGuid(), CoreAttributeTypes.Category.getGuid())));
   }

   @Test
   public void testLoadAttributesWithIdAndType() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_attribute att1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.attr_id = ? AND att1.attr_type_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeIds(45);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected, 45,
         CoreAttributeTypes.Annotation.getGuid());
   }

   @Test
   public void testLoadAttributesIncludeDeleted() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected = "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
      " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
      " FROM \n" + //
      "osee_join_artifact jart1, osee_attribute att1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
      " AND txs1.tx_current IN (1, 2, 3) AND txs1.branch_id = jart1.branch_id\n" + //
      " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadAttributesHistorical() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.transaction_id as stripe_transaction_id,\n" + //
         " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_attribute att1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.transaction_id <= jart1.transaction_id AND txs1.mod_type != 3 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX_ID);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadAttributesHistoricalIncludeDeleted() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.transaction_id as stripe_transaction_id,\n" + //
         " jart1.art_id, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_attribute att1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "att1.art_id = jart1.art_id AND jart1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.transaction_id <= jart1.transaction_id AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX_ID);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX_ID, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   @Test
   public void testLoadRelations() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadRelationsIncludeDeleted() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current IN (1, 2, 3) AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadRelationsWithType() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.rel_link_type_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected,
         CoreRelationTypes.Default_Hierarchical__Child.getGuid());
   }

   @Test
   public void testLoadRelationsWithTypes() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_join_id jid1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.rel_link_type_id = jid1.id AND jid1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child,
         CoreRelationTypes.Dependency__Artifact);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(
         EXPECTED_HEAD_TX_ID,
         expectedLoadLevel,
         EXCLUDE_DELETED,
         expected,
         data(JQID),
         list(data(CoreRelationTypes.Default_Hierarchical__Child.getGuid(),
            CoreRelationTypes.Dependency__Artifact.getGuid())));
   }

   @Test
   public void testLoadRelationsWithId() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.rel_link_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationIds(45);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected, 45);
   }

   @Test
   public void testLoadRelationsWithIds() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_join_id jid1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.rel_link_id = jid1.id AND jid1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationIds(45, 55);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID), list(data(45, 55)));
   }

   @Test
   public void testLoadRelationsWithIdsAndTypes() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_join_id jid1, osee_join_id jid2, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.rel_link_id = jid1.id AND jid1.query_id = ? AND rel1.rel_link_type_id = jid2.id AND jid2.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationIds(45, 55);
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child,
         CoreRelationTypes.Dependency__Artifact);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(
         EXPECTED_HEAD_TX_ID,
         expectedLoadLevel,
         EXCLUDE_DELETED,
         expected,
         data(JQID, JQID),
         list(data(45, 55),
            data(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), CoreRelationTypes.Dependency__Artifact)));
   }

   @Test
   public void testLoadRelationsWithIdAndType() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.rel_link_id = ? AND rel1.rel_link_type_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationIds(45);
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected, 45,
         CoreRelationTypes.Default_Hierarchical__Child.getGuid());
   }

   @Test
   public void testLoadRelationsHistorical() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.transaction_id as stripe_transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.transaction_id <= jart1.transaction_id AND txs1.mod_type != 3 AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX_ID);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactIncludeDeletedAttributes() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ALL;
      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current IN (1, 2, 3) AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedAttributes();
      dataLoader.withAttributeIds(45, 55);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation, CoreAttributeTypes.Category);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactIncludeDeletedRelations() throws OseeCoreException {
      LoadLevel expectedLoadLevel = LoadLevel.ALL;
      String expected =
         "SELECT/*+ ordered */ txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id,\n" + //
         " jart1.art_id, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
         " FROM \n" + //
         "osee_join_artifact jart1, osee_relation_link rel1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "(rel1.a_art_id = jart1.art_id OR rel1.b_art_id = jart1.art_id) AND jart1.query_id = ? AND rel1.rel_link_type_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
         " AND txs1.tx_current IN (1, 2, 3) AND txs1.branch_id = jart1.branch_id\n" + //
         " ORDER BY txs1.branch_id, jart1.art_id, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, BRANCH, Arrays.asList(1, 2, 3));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedRelations();
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX_ID, expectedLoadLevel, EXCLUDE_DELETED, expected,
         CoreRelationTypes.Default_Hierarchical__Child.getGuid());
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   private static final Long JQID = Long.MAX_VALUE;

   private List<Object[]> list(Object[]... input) {
      return Arrays.asList(input);
   }

   private Object[] data(Object... data) {
      return data;
   }

   private void verifyCommon(int txId, LoadLevel level, DeletionFlag includeDeleted, String expectedSQL, Object... params) throws OseeCoreException {
      verifyCommon(txId, level, includeDeleted, expectedSQL, params, list());
   }

   private void verifyCommon(int txId, LoadLevel level, DeletionFlag includeDeleted, String expectedSQL, Object[] params, List<Object[]> joinDatas) throws OseeCoreException {
      verify(spyLoader).loadArtifacts(eq(cancellation), eq(builder), joinCaptor.capture(), criteriaCaptor.capture(),
         contextCaptor.capture(), eq(200));

      verify(builder, times(1)).onLoadDescription(descriptionCaptor.capture());
      LoadDescription descriptor = descriptionCaptor.getValue();

      boolean isHeadTx = EXPECTED_HEAD_TX_ID == txId;

      assertEquals(session, descriptor.getSession());
      Options options = descriptor.getOptions();

      assertEquals(BRANCH, descriptor.getBranch());
      assertEquals(txId, descriptor.getTransaction());

      assertEquals(isHeadTx, OptionsUtil.isHeadTransaction(options));
      assertEquals(!isHeadTx, OptionsUtil.isHistorical(options));
      assertEquals(level, OptionsUtil.getLoadLevel(options));
      assertEquals(includeDeleted, OptionsUtil.getIncludeDeletedArtifacts(options));

      assertTrue(joinCaptor.getValue().wasStored());
      assertEquals(3, joinCaptor.getValue().size());

      LoadSqlContext context = contextCaptor.getValue();

      assertEquals(session, context.getSession());
      assertEquals(expectedSQL, context.getSql());

      assertEquals(1 + params.length, context.getParameters().size());

      Iterator<Object> iterator = context.getParameters().iterator();

      Object queryId = iterator.next();
      assertFalse(queryId.equals(-1));
      assertEquals(joinCaptor.getValue().getQueryId(), queryId);

      List<Object> queryIdsToMatch = new ArrayList<Object>();
      for (Object param : params) {
         if (param.equals(JQID)) {
            queryIdsToMatch.add(iterator.next());
         } else {
            assertEquals(param, iterator.next());
         }
      }

      assertEquals(joinDatas.size(), context.getJoins().size());
      Iterator<AbstractJoinQuery> jQuerys = context.getJoins().iterator();
      Iterator<Object> queryIds = queryIdsToMatch.iterator();
      for (Object[] data : joinDatas) {
         AbstractJoinQuery jQuery = jQuerys.next();
         assertEquals(queryIds.next(), jQuery.getQueryId());
         assertEquals(data.length, jQuery.size());
      }
   }
}
