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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.orcs.db.mock.OseeDatabase.integrationRule;
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
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.Id4JoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
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
   @Rule public TestRule db = integrationRule(this);
   @OsgiService public OrcsApi orcsApi;
   @OsgiService public SqlJoinFactory joinFactory;

   @Mock private Log logger;
   @Mock private JdbcClient jdbcClient;
   @Mock private JdbcStatement chStmt;
   @Mock private IdentityManager identityService;
   @Mock private LoadDataHandler builder;
   @Mock private OrcsObjectFactory rowDataFactory;
   @Mock private HasCancellation cancellation;

   @Captor private ArgumentCaptor<LoadSqlContext> contextCaptor;
   @Captor private ArgumentCaptor<Id4JoinQuery> joinCaptor;
   @Captor private ArgumentCaptor<CriteriaOrcsLoad> criteriaCaptor;
   @Captor private ArgumentCaptor<LoadDescription> descriptionCaptor;

   @Mock private OrcsSession session;
   //@formatter:on

   private final static TransactionId EXPECTED_TX = TransactionId.valueOf(45678);
   private final static TransactionId EXPECTED_HEAD_TX = TransactionId.valueOf(50000);

   private DataLoaderFactory factory;
   private SqlObjectLoader spyLoader;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      LoaderModule module = new LoaderModule(logger, jdbcClient, identityService, joinFactory, null);
      SqlObjectLoader loader = module.createSqlObjectLoader(rowDataFactory, null, orcsApi.getOrcsTypes());

      spyLoader = spy(loader);
      factory = module.createDataLoaderFactory(spyLoader);

      when(jdbcClient.getStatement()).thenReturn(chStmt);
      when(jdbcClient.fetch(eq(TransactionId.SENTINEL), Matchers.anyString(), eq(COMMON))).thenReturn(EXPECTED_HEAD_TX);

      when(jdbcClient.getDbType()).thenReturn(JdbcDbType.h2);
   }

   @Test
   public void testLoadFull() {
      LoadLevel expectedLoadLevel = LoadLevel.ALL;

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      // @formatter:off
      verify(spyLoader, times(0)).loadHeadTransactionId(COMMON);
      verify(spyLoader, times(1)).loadArtifacts(eq(builder), criteriaCaptor.capture(), contextCaptor.capture(), eq(200));
      verify(spyLoader, times(1)).loadAttributes(eq(builder), criteriaCaptor.capture(), contextCaptor.capture(), eq(200));
      verify(spyLoader, times(1)).loadRelations(eq(builder), criteriaCaptor.capture(), contextCaptor.capture(), eq(200));
      // @formatter:on
   }

   @Test
   public void testasArtifactIds() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_DATA;
      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, art1.art_type_id, art1.guid\n" + //
            " FROM osee_join_id4 jart1, osee_artifact art1, osee_txs txs1\n" + //
            " WHERE art1.art_id = jart1.id2 AND jart1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactIncludeDeleted() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_DATA;
      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, art1.art_type_id, art1.guid\n" + //
            " FROM osee_join_id4 jart1, osee_artifact art1, osee_txs txs1\n" + //
            " WHERE art1.art_id = jart1.id2 AND jart1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current <> 0 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactHistorical() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_DATA;
      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, txs1.transaction_id as stripe_transaction_id, jart1.id2, jart1.id4, art1.art_type_id, art1.guid\n" + //
            " FROM osee_join_id4 jart1, osee_artifact art1, osee_txs txs1\n" + //
            " WHERE art1.art_id = jart1.id2 AND jart1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= jart1.id3 AND txs1.mod_type != 3 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactHistoricalIncludeDeleted() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_DATA;
      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, txs1.transaction_id as stripe_transaction_id, jart1.id2, jart1.id4, art1.art_type_id, art1.guid\n" + //
            " FROM osee_join_id4 jart1, osee_artifact art1, osee_txs txs1\n" + //
            " WHERE art1.art_id = jart1.id2 AND jart1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= jart1.id3 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testLoadAttributes() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadAttributesWithType() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.attr_type_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, CoreAttributeTypes.Annotation);
   }

   @Test
   public void testLoadAttributesWithTypes() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_join_id jid1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.attr_type_id = jid1.id AND jid1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation, CoreAttributeTypes.Category);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID),
         list(data(CoreAttributeTypes.Annotation, CoreAttributeTypes.Category)));
   }

   @Test
   public void testLoadAttributesWithId() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.attr_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeIds(45);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, 45L);
   }

   @Test
   public void testLoadAttributesWithIds() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_join_id jid1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.attr_id = jid1.id AND jid1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeIds(45, 55);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID), list(data(45, 55)));
   }

   @Test
   public void testLoadAttributesWithIdsAndTypes() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_join_id jid1, osee_join_id jid2, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.attr_id = jid1.id AND jid1.query_id = ? AND att1.attr_type_id = jid2.id AND jid2.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeIds(45, 55);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation, CoreAttributeTypes.Category);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID, JQID),
         list(data(45, 55), data(CoreAttributeTypes.Annotation, CoreAttributeTypes.Category)));
   }

   @Test
   public void testLoadAttributesWithIdAndType() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.attr_id = ? AND att1.attr_type_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withAttributeIds(45);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, 45L, CoreAttributeTypes.Annotation);
   }

   @Test
   public void testLoadAttributesIncludeDeleted() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current <> 0 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadAttributesHistorical() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, txs1.transaction_id as stripe_transaction_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.transaction_id <= jart1.id3 AND txs1.mod_type != 3 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadAttributesHistoricalIncludeDeleted() {
      LoadLevel expectedLoadLevel = LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, txs1.transaction_id as stripe_transaction_id, jart1.id2, jart1.id4, att1.attr_id, att1.attr_type_id, att1.value, att1.uri\n" + //
            " FROM osee_join_id4 jart1, osee_attribute att1, osee_txs txs1\n" + //
            " WHERE att1.art_id = jart1.id2 AND jart1.query_id = ? AND att1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.transaction_id <= jart1.id3 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, att1.attr_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   @Test
   public void testLoadRelations() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadRelationsIncludeDeleted() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current <> 0 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedArtifacts();

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, INCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadRelationsWithType() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.rel_link_type_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected,
         CoreRelationTypes.Default_Hierarchical__Child.getGuid());
   }

   @Test
   public void testLoadRelationsWithTypes() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_join_id jid1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.rel_link_type_id = jid1.id AND jid1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child,
         CoreRelationTypes.Dependency__Artifact);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID),
         list(data(CoreRelationTypes.Default_Hierarchical__Child.getGuid(),
            CoreRelationTypes.Dependency__Artifact.getGuid())));
   }

   @Test
   public void testLoadRelationsWithId() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.rel_link_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationIds(45);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, 45L);
   }

   @Test
   public void testLoadRelationsWithIds() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_join_id jid1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.rel_link_id = jid1.id AND jid1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationIds(45, 55);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID), list(data(45, 55)));
   }

   @Test
   public void testLoadRelationsWithIdsAndTypes() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_join_id jid1, osee_join_id jid2, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.rel_link_id = jid1.id AND jid1.query_id = ? AND rel1.rel_link_type_id = jid2.id AND jid2.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationIds(45, 55);
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child,
         CoreRelationTypes.Dependency__Artifact);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, data(JQID, JQID), list(data(45, 55),
         data(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), CoreRelationTypes.Dependency__Artifact)));
   }

   @Test
   public void testLoadRelationsWithIdAndType() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.rel_link_id = ? AND rel1.rel_link_type_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current = 1 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.withRelationIds(45);
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected, 45L,
         CoreRelationTypes.Default_Hierarchical__Child.getGuid());
   }

   @Test
   public void testLoadRelationsHistorical() {
      LoadLevel expectedLoadLevel = LoadLevel.RELATION_DATA;

      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, txs1.transaction_id as stripe_transaction_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.transaction_id <= jart1.id3 AND txs1.mod_type != 3 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));
      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.fromTransaction(EXPECTED_TX);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_TX, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactIncludeDeletedAttributes() {
      LoadLevel expectedLoadLevel = LoadLevel.ALL;
      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current <> 0 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedAttributes();
      dataLoader.withAttributeIds(45, 55);
      dataLoader.withAttributeTypes(CoreAttributeTypes.Annotation, CoreAttributeTypes.Category);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected);
   }

   @Test
   public void testLoadArtifactIncludeDeletedRelations() {
      LoadLevel expectedLoadLevel = LoadLevel.ALL;
      String expected =
         "SELECT txs1.gamma_id, txs1.mod_type, txs1.branch_id, txs1.transaction_id, txs1.app_id, jart1.id2, jart1.id4, rel1.rel_link_id, rel1.rel_link_type_id, rel1.a_art_id, rel1.b_art_id, rel1.rationale\n" + //
            " FROM osee_join_id4 jart1, osee_relation_link rel1, osee_txs txs1\n" + //
            " WHERE (rel1.a_art_id = jart1.id2 OR rel1.b_art_id = jart1.id2) AND jart1.query_id = ? AND rel1.rel_link_type_id = ? AND rel1.gamma_id = txs1.gamma_id\n" + //
            " AND txs1.tx_current <> 0 AND txs1.branch_id = jart1.id1\n" + //
            " ORDER BY txs1.branch_id, jart1.id2, rel1.rel_link_id, txs1.transaction_id desc";

      DataLoader dataLoader = factory.newDataLoaderFromIds(session, COMMON, Arrays.asList(1L, 2L, 3L));

      dataLoader.withLoadLevel(expectedLoadLevel);
      dataLoader.includeDeletedRelations();
      dataLoader.withRelationTypes(CoreRelationTypes.Default_Hierarchical__Child);

      assertEquals(expectedLoadLevel, dataLoader.getLoadLevel());

      dataLoader.load(cancellation, builder);

      verifyCommon(EXPECTED_HEAD_TX, expectedLoadLevel, EXCLUDE_DELETED, expected,
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

   private void verifyCommon(TransactionId txId, LoadLevel level, DeletionFlag includeDeleted, String expectedSQL, Object... params) {
      verifyCommon(txId, level, includeDeleted, expectedSQL, params, list());
   }

   private void verifyCommon(TransactionId txId, LoadLevel level, DeletionFlag includeDeleted, String expectedSQL, Object[] params, List<Object[]> joinDatas) {
      verify(spyLoader).loadArtifacts(eq(cancellation), eq(builder), joinCaptor.capture(), criteriaCaptor.capture(),
         contextCaptor.capture(), eq(200));

      verify(builder, times(1)).onLoadDescription(descriptionCaptor.capture());
      LoadDescription descriptor = descriptionCaptor.getValue();

      boolean isHeadTx = EXPECTED_HEAD_TX.equals(txId);

      assertEquals(session, descriptor.getSession());
      Options options = descriptor.getOptions();

      assertEquals(COMMON, descriptor.getBranch());
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

      List<Object> queryIdsToMatch = new ArrayList<>();
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
