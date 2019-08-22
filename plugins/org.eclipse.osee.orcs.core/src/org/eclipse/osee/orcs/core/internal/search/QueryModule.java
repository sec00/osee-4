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
package org.eclipse.osee.orcs.core.internal.search;

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.HasStatistics;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilderFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.statistics.QueryStatistics;

/**
 * @author Roberto E. Escobar
 */
public class QueryModule implements HasStatistics<QueryStatistics> {

   private final QueryStatisticsImpl statistics = new QueryStatisticsImpl();

   private final CallableQueryFactory artQueryFactory;

   private final BranchCriteriaFactory branchCriteriaFactory;
   private final TransactionCriteriaFactory txCriteriaFactory;
   private final TupleQuery tupleQuery;
   private final ApplicabilityDsQuery applicabilityDsQuery;
   private final QueryEngine queryEngine;
   private final OrcsTypes orcsTypes;

   public static interface QueryModuleProvider {
      QueryFactory getQueryFactory(OrcsSession session);
   }

   public QueryModule(Log logger, QueryEngine queryEngine, GraphBuilderFactory builderFactory, GraphProvider provider, OrcsTypes orcsTypes, ExternalArtifactManager proxyManager) {
      this.queryEngine = queryEngine;
      QueryStatsCollectorImpl queryStatsCollector = new QueryStatsCollectorImpl(statistics);
      artQueryFactory =
         new CallableQueryFactory(logger, queryEngine, queryStatsCollector, builderFactory, provider, proxyManager);
      branchCriteriaFactory = new BranchCriteriaFactory();
      txCriteriaFactory = new TransactionCriteriaFactory();
      tupleQuery = queryEngine.createTupleQuery();
      applicabilityDsQuery = queryEngine.createApplicabilityDsQuery();
      this.orcsTypes = orcsTypes;
      this.queryEngine = queryEngine;
   }

   public QueryFactory createQueryFactory(OrcsSession session) {
      return new QueryFactoryImpl(artQueryFactory, branchCriteriaFactory, txCriteriaFactory, tupleQuery,
         applicabilityDsQuery, queryEngine, orcsTypes);
   }

   public CallableQueryFactory getArtQueryFactory() {
      return artQueryFactory;
   }

   @Override
   public QueryStatistics getStatistics(OrcsSession session) {
      return statistics.clone();
   }

   @Override
   public void clearStatistics(OrcsSession session) {
      statistics.clear();
   }
}