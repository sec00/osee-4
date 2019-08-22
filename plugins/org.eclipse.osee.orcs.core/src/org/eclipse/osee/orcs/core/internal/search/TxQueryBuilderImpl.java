/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAuthorIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaCommitIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetPrior;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIds;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.TxQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class TxQueryBuilderImpl<T> implements TxQueryBuilder<T> {

   private final TransactionCriteriaFactory criteriaFactory;
   private final QueryData queryData;

   public TxQueryBuilderImpl(TransactionCriteriaFactory criteriaFactory, QueryData queryData) {
      this.criteriaFactory = criteriaFactory;
      this.queryData = queryData;
   }

   private Options getOptions() {
      return queryData.getOptions();
   }

   @Override
   public T andTxIds(Collection<TransactionId> ids) {
      return addAndCheck(queryData, new CriteriaTxIds(ids));
   }

   @Override
   public T andTxId(Operator op, int id) {
      Criteria criteria = criteriaFactory.newByIdWithOperator(op, id);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andTxId(Operator op1, int id1, Operator op2, int id2) {
      Criteria criteria = criteriaFactory.newByIdWithTwoOperators(op1, id1, op2, id2);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andCommentEquals(String value) {
      Criteria criteria = criteriaFactory.newCommentCriteria(value, false);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andCommentPattern(String pattern) {
      Criteria criteria = criteriaFactory.newCommentCriteria(pattern, true);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andIs(TransactionDetailsType... types) {
      return andIs(Arrays.asList(types));
   }

   @Override
   public T andIs(Collection<TransactionDetailsType> types) {
      Criteria criteria = criteriaFactory.newTxTypeCriteria(types);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andBranch(BranchId... ids) {
      return andBranch(Arrays.asList(ids));
   }

   @Override
   public T andBranch(Collection<? extends BranchId> ids) {
      Criteria criteria = criteriaFactory.newTxBranchIdCriteria(ids);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andBranchIds(Collection<? extends BranchId> ids) {
      Criteria criteria = criteriaFactory.newTxBranchIdCriteria(ids);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andDate(Operator op, Timestamp date) {
      Criteria criteria = criteriaFactory.newByDateWithOperator(op, date);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andDate(Timestamp from, Timestamp to) {
      Criteria criteria = criteriaFactory.newByDateRange(from, to);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andAuthorId(ArtifactId author) {
      return addAndCheck(queryData, new CriteriaAuthorIds(author));
   }

   @Override
   public T andAuthorIds(Collection<ArtifactId> authors) {
      return addAndCheck(queryData, new CriteriaAuthorIds(authors));
   }

   @Override
   public T andNullCommitId() {
      return andCommitId(null);
   }

   @Override
   public T andCommitIds(Collection<ArtifactId> ids) {
      return addAndCheck(queryData, new CriteriaCommitIds(ids));
   }

   @Override
   public T andCommitId(ArtifactId id) {
      return addAndCheck(queryData, new CriteriaCommitIds(id));
   }

   @Override
   public T andIsHead(BranchId branch) {
      Criteria criteria = criteriaFactory.newGetHead(branch);
      return addAndCheck(queryData, criteria);
   }

   @Override
   public T andIsPriorTx(TransactionToken tx) {
      return addAndCheck(queryData, new CriteriaTxGetPrior(tx));
   }

   @SuppressWarnings("unchecked")
   private T addAndCheck(QueryData queryData, Criteria criteria) {
      if (criteria.checkValid(getOptions())) {
         queryData.addCriteria(criteria);
      }
      return (T) this;
   }

   public QueryData build() {
      if (queryData.hasNoCriteria()) {
         addAndCheck(queryData, criteriaFactory.createAllTransactionsCriteria());
      }
      return queryData;

   }

   @Override
   public T andTxId(TransactionId id) {
      return addAndCheck(queryData, new CriteriaTxIds(id));
   }
}