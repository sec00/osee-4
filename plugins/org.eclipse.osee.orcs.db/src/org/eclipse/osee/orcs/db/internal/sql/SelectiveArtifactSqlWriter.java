/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Ryan D. Brooks
 */
public class SelectiveArtifactSqlWriter extends AbstractSqlWriter {
   private final QueryData queryData;
   private final List<AbstractJoinQuery> joinTables = new ArrayList<>();
   private final AbstractSqlWriter parentWriter;
   private final List<Object> parameters = new ArrayList<>();

   private SelectiveArtifactSqlWriter(AbstractSqlWriter parentWriter, SqlJoinFactory sqlJoinFactory, JdbcClient jdbcClient, QueryData queryData, QueryType queryType) {
      super(sqlJoinFactory, jdbcClient, queryType);
      this.parentWriter = parentWriter;
      this.queryData = queryData;
   }

   public SelectiveArtifactSqlWriter(SqlJoinFactory sqlJoinFactory, JdbcClient jdbcClient, QueryData queryData, QueryType queryType) {
      this(null, sqlJoinFactory, jdbcClient, queryData, queryType);
   }

   public SelectiveArtifactSqlWriter(SelectiveArtifactSqlWriter parentWriter) {
      this(parentWriter, parentWriter.joinFactory, parentWriter.getJdbcClient(), parentWriter.queryData,
         parentWriter.queryType);
   }

   @Override
   public void addParameter(Object parameter) {
      if (parentWriter == null) {
         parameters.add(parameter);
      } else {
         parentWriter.addParameter(parameter);
      }
   }

   @Override
   protected void addJoin(AbstractJoinQuery join) {
      if (parentWriter == null) {
         joinTables.add(join);
      } else {
         parentWriter.addJoin(join);
      }
   }

   @Override
   public void addWithClause(WithClause withClause) {
      if (parentWriter == null) {
         super.addWithClause(withClause);
      } else {
         parentWriter.addWithClause(withClause);
      }
   }

   public void runSql(Consumer<JdbcStatement> consumer, SqlHandlerFactory handlerFactory) {
      try {
         write(handlerFactory.createHandlers(queryData));
         for (AbstractJoinQuery join : joinTables) {
            join.store();
         }
         getJdbcClient().runQuery(consumer, JDBC__MAX_FETCH_SIZE, toSql(), parameters.toArray());
      } finally {
         for (AbstractJoinQuery join : joinTables) {
            try {
               join.close();
            } catch (Exception ex) {
               // Ensure we try to delete all join entries
            }
         }
         reset();
      }
   }

   public String toSql() {
      return output.toString();
   }

   @Override
   public Options getOptions() {
      return queryData.getOptions();
   }

   @Override
   protected void writeSelectFields() {
      String artAlias = getMainTableAlias(TableEnum.ARTIFACT_TABLE);
      String txAlias = getMainTableAlias(TableEnum.TXS_TABLE);
      writeSelectFields(artAlias, "art_id", artAlias, "art_type_id", txAlias, "app_id");
   }

   @Override
   public void writeGroupAndOrder() {
      if (parentWriter == null && !isCountQueryType() && !isSelectQueryType()) {
         write(" ORDER BY art_id");
      }
   }

   @Override
   public void writeTxBranchFilter(String txsAlias, boolean allowDeleted) {
      write(txsAlias);
      write(".tx_current");
      if (allowDeleted) {
         write(" <> ");
         write(TxCurrent.NOT_CURRENT.getIdString());
      } else {
         write(" = ");
         write(TxCurrent.CURRENT.getIdString());
      }

      BranchId branch = queryData.getBranch();
      if (branch.isValid()) {
         write(" AND ");
         writeEqualsParameter(txsAlias, "branch_id", branch);
      }
   }

   @Override
   protected void reset() {
      super.reset();
      parameters.clear();
      joinTables.clear();
      queryData.reset();
   }

   @Override
   public String getWithClauseTxBranchFilter(String txsAlias, boolean deletedPredicate) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      if (parentWriter == null) {
         StringBuilder strB = new StringBuilder();
         String[] tokens = output.toString().split("\\?");
         for (int i = 0; i < tokens.length; i++) {
            strB.append(tokens[i]);
            if (i < parameters.size()) {
               Object parameter = parameters.get(i);
               if (parameter instanceof Id) {
                  strB.append(((Id) parameter).getIdString());
               } else {
                  strB.append(parameter);
               }
            } else if (i < tokens.length - 1) {
               strB.append("?");
            }
         }
         return strB.toString();
      } else {
         return toSql();
      }
   }
}