/*******************************************************************************
 * Copyright (c) 20014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.indexer;

import java.util.HashSet;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;

/**
 * @author Ryan D. Brooks
 */
public class TagCollectorAndStorage implements TagCollector {
   private static final String INSERT_TAGS = "insert into osee_search_tags (gamma_id, coded_tag_id) values (?, ?)";

   private static final String DELETE_TAGS = "delete from osee_search_tags where gamma_id = ?";
   private final HashSet<Long> tags = new HashSet<Long>(10000);
   private final JdbcConnection connection;
   private final JdbcClient jdbcClient;
   private final ActivityLog activityLog;
   private OseePreparedStatement dropTags;
   private OseePreparedStatement addTags;
   private final int storageThreshold;
   private int tagCount = 0;

   public TagCollectorAndStorage(JdbcClient jdbcClient, JdbcConnection connection, int storageThreshold, ActivityLog activityLog) {
      this.jdbcClient = jdbcClient;
      this.connection = connection;
      this.storageThreshold = storageThreshold;
      this.activityLog = activityLog;
      initDbStatements();
   }

   private void initDbStatements() {
      dropTags = jdbcClient.getBatchStatement(connection, DELETE_TAGS);
      addTags = jdbcClient.getBatchStatement(connection, INSERT_TAGS);
   }

   public void gammaComplete(Long gammaId) {
      tags.clear();
      dropTags.addToBatch(gammaId);
      if (tagCount > storageThreshold) {
         System.out.println("Storing " + tagCount + " tags including for gamma: " + gammaId);
         store();
         // message should be created with text: Storing %d tags including for gamma: %d
         //         activityLog.createEntry(Activity.SRS_TRACE, tagCount, gammaId);
         System.out.println(String.format("Storing %d tags including for gamma: %d", tagCount, gammaId));
         tagCount = 0;
      }
   }

   @Override
   public void addTag(Long gammaId, String word, Long codedTag) {
      if (tags.add(codedTag)) {
         addTags.addToBatch(gammaId, codedTag);
         tagCount++;
      }
   }

   public void store() {
      synchronized (connection) {
         jdbcClient.runTransaction(connection, new JdbcTransaction() {
            @Override
            public void handleTxWork(JdbcConnection connection) {
               dropTags.execute();
               addTags.execute();
            }
         });
         initDbStatements();
      }
   }
}