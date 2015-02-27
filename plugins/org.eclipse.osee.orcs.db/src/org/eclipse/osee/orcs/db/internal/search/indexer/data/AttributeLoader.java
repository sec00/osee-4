/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.indexer.data;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.core.ds.IndexedResource;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexedResourceLoader;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Ryan D. Brooks
 */
public class AttributeLoader implements IndexedResourceLoader {
   private static final String LOAD_ATTRIBUTE =
      "SELECT gamma_id, value, uri, attr_type_id, attr_id FROM osee_attribute, osee_join_id jid WHERE attr_type_id = jid.id and query_id = ? order by attr_type_id, gamma_id";

   private final JdbcClient jdbcClient;
   private final Collection<? extends IAttributeType> typesToTag;
   private final int batchSize;
   private final IResourceManager resourceManager;
   private final JdbcConnection connection;
   private final SqlJoinFactory joinFactory;
   private IdJoinQuery attrTypeJoin;
   private JdbcStatement chStmt = null;

   public AttributeLoader(JdbcClient jdbcClient, JdbcConnection connection, Collection<? extends IAttributeType> typesToTag, IResourceManager resourceManager, SqlJoinFactory joinFactory, int batchSize) {
      this.jdbcClient = jdbcClient;
      this.connection = connection;
      this.typesToTag = typesToTag;
      this.batchSize = batchSize;
      this.resourceManager = resourceManager;
      this.joinFactory = joinFactory;
   }

   public void init() {
      attrTypeJoin = joinFactory.createIdJoinQuery();
      for (IAttributeType attributeType : typesToTag) {
         Long uuid = attributeType.getGuid();
         attrTypeJoin.add(uuid);
      }

      attrTypeJoin.store(connection);
      chStmt = jdbcClient.getStatement(connection);
      chStmt.runPreparedQuery(batchSize, LOAD_ATTRIBUTE, attrTypeJoin.getQueryId());
   }

   @Override
   public void loadSource(OrcsDataHandler<IndexedResource> handler, int tagQueueQueryId) {
      int count = 0;
      IndexerDataSourceImpl data = null;
      while (chStmt.next() && count < batchSize) {
         data = new IndexerDataSourceImpl(resourceManager);
         loadInto(data);
         handler.onData(data);
         count++;
      }
      if (data != null) {
         System.out.println("Type id: " + data.getTypeUuid());
      }
   }

   @Override
   public void cleanupSource(JdbcConnection connection, int tagQueueQueryId) {
      // this will be called at the end of each batch so don't put final cleanup here
   }

   public synchronized boolean loadInto(IndexerDataSourceImpl dataSource) {
      boolean next = chStmt.next();
      if (next) {
         int itemId = chStmt.getInt("attr_id");
         long typeUuid = chStmt.getLong("attr_type_id");
         long gammaId = chStmt.getLong("gamma_id");
         String uri = chStmt.getString("uri");
         String value = chStmt.getString("value");
         dataSource.populate(itemId, typeUuid, gammaId, value, uri);
      }
      return next;
   }

   public void cleanup() {
      attrTypeJoin.delete(connection);
      chStmt.close();
   }

   public int getOptimalNumberOfThreads() {
      return 4;
   }
}