/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Roberto E. Escobar
 */
public class QueryFactoryImpl implements QueryFactory {

   private final OrcsSession context;
   private final CriteriaFactory criteriaFctry;
   private final CallableQueryFactory queryFctry;
   private final BranchCriteriaFactory branchCriteriaFactory;
   private final TransactionCriteriaFactory txCriteriaFactory;
   private final TupleQuery tupleQuery;
   private final ApplicabilityDsQuery applicabilityDsQuery;
   private final QueryEngine queryEngine;

   public QueryFactoryImpl(OrcsSession context, CriteriaFactory criteriaFctry, CallableQueryFactory queryFctry, BranchCriteriaFactory branchCriteriaFactory, TransactionCriteriaFactory txCriteriaFactory, TupleQuery tupleQuery, ApplicabilityDsQuery applicabilityDsQuery, QueryEngine queryEngine) {
      this.context = context;
      this.criteriaFctry = criteriaFctry;
      this.queryFctry = queryFctry;
      this.branchCriteriaFactory = branchCriteriaFactory;
      this.txCriteriaFactory = txCriteriaFactory;
      this.tupleQuery = tupleQuery;
      this.applicabilityDsQuery = applicabilityDsQuery;
      this.queryEngine = queryEngine;
   }

   private QueryBuilder createBuilder(BranchId branchId) {
      Options options = OptionsUtil.createOptions();
      List<Criteria> criteria = new ArrayList<>();
      if (branchId != null) {
         criteria.add(new CriteriaBranch(branchId));
      }
      QueryData queryData = new QueryData(criteria, options);
      QueryBuilder builder = new QueryBuilderImpl(queryFctry, criteriaFctry, context, queryData);
      return builder;
   }

   @Override
   public BranchQuery branchQuery() {
      return new BranchQueryImpl(queryEngine, branchCriteriaFactory, new QueryData());
   }

   @Override
   public QueryBuilder fromBranch(BranchId branch) {
      return createBuilder(branch);
   }

   @Override
   public QueryBuilder fromArtifacts(Collection<? extends ArtifactReadable> artifacts) {
      Conditions.checkNotNullOrEmpty(artifacts, "artifacts");
      ArtifactReadable artifact = artifacts.iterator().next();
      Set<String> guids = new HashSet<>();
      for (ArtifactReadable art : artifacts) {
         guids.add(art.getGuid());
      }
      return fromBranch(artifact.getBranch()).andGuids(guids);
   }

   @Override
   public QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType) {
      QueryBuilder builder = createBuilder(null);
      builder.andIsOfType(artifactType);
      return builder;
   }

   @Override
   public TransactionQuery transactionQuery() {
      return new TransactionQueryImpl(queryEngine, txCriteriaFactory, new QueryData());
   }

   @Override
   public TupleQuery tupleQuery() {
      return tupleQuery;
   }

   @Override
   public ApplicabilityQuery applicabilityQuery() {
      return new ApplicabilityQueryImpl(applicabilityDsQuery, this);
   }
}