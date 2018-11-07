/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractSoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.BaselinedBy;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.BaselinedTimestamp;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitCommitAuthorDate;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitCommitSHA;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ReviewId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ReviewStoryId;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.GitLatest;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import org.eclipse.osee.define.api.CertBaselineData;
import org.eclipse.osee.define.api.CertFileData;
import org.eclipse.osee.define.api.TraceData;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.define.rest.internal.TraceReportGenerator;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.TriConsumer;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Ryan D. Brooks
 */
public class TraceabilityOperationsImpl implements TraceabilityOperations {

   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private final TupleQuery tupleQuery;

   public TraceabilityOperationsImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.tupleQuery = queryFactory.tupleQuery();
   }

   @Override
   public void generateTraceReport(BranchId branchId, String codeRoot, String traceRoot, Writer providedWriter, IArtifactType artifactType, AttributeTypeToken attributeType) {
      TraceReportGenerator generator = new TraceReportGenerator(artifactType, attributeType);
      try {
         generator.generate(orcsApi, branchId, codeRoot, traceRoot, providedWriter);
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   @Override
   public TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType) {
      ResultSet<ArtifactReadable> allSwReqs =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement).getResults();

      List<String> swReqs =
         allSwReqs.getList().stream().filter(req -> excludeType.isInvalid() || !req.isOfType(excludeType)).map(
            req -> req.getName()).collect(Collectors.toList());

      ResultSet<ArtifactReadable> impds =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.ImplementationDetails).getResults();

      Map<String, String[]> impdMap = new HashMap<>();
      for (ArtifactReadable impd : impds) {
         List<ArtifactReadable> matchingReq =
            impd.getRelated(CoreRelationTypes.Implementation_Info__Requirement).getList();

         String[] pair;
         if (matchingReq.isEmpty()) {
            pair = findMatchingReq(impd);
         } else {
            pair = new String[matchingReq.size() + 1];
            for (int i = 0; i < matchingReq.size(); i++) {
               pair[i] = matchingReq.get(i).getName();
            }
            pair[matchingReq.size()] = "0";
         }
         impdMap.put(impd.getName(), pair);
      }
      TraceData traceData = new TraceData(swReqs, impdMap);
      return traceData;
   }

   private String[] findMatchingReq(ArtifactReadable impd) {
      ArtifactReadable cursor = impd.getParent();
      int level = 1;
      while (cursor != null) {
         if (cursor.isOfType(AbstractSoftwareRequirement)) {
            return new String[] {cursor.getName(), String.valueOf(level)};
         }
         level++;
         cursor = cursor.getParent();
      }
      return null;
   }

   @Override
   public ArtifactId baselineFiles(BranchId branch, String repositoryName, CertBaselineData baselineData, UserId account) {
      return new GitTraceability(orcsApi, branch, repositoryName).baselineFiles(baselineData, account);
   }

   @Override
   public void parseGitHistory(String repositoryName, String gitHistory, BranchId branch, UserId account) {
      parseGitHistory(getRepoId(branch, repositoryName), gitHistory, branch, account);
   }

   @Override
   public ArtifactId getRepoId(BranchId branch, String repositoryName) {
      return queryFactory.fromBranch(branch).andNameEquals(repositoryName).andTypeEquals(
         CoreArtifactTypes.GitRepository).loadArtifactId();
   }

   @Override
   public ArtifactReadable getRepo(BranchId branch, String repositoryName) {
      return queryFactory.fromBranch(branch).andNameEquals(repositoryName).andTypeEquals(
         CoreArtifactTypes.GitRepository).getArtifact();
   }

   @Override
   public void parseGitHistory(ArtifactId repository, String gitHistory, BranchId branch, UserId account) {
      new GitTraceability(orcsApi, branch, repository).parseGitHistory(gitHistory, branch, account);
   }

   @Override
   public List<CertBaselineData> getBaselineData(BranchId branch, String repositoryName) {
      ArtifactReadable repo = getRepo(branch, repositoryName);
      return Collections.transform(repo.getChildren().getList(), this::getBaselineData);
   }

   @Override
   public List<CertFileData> getCertFileData(BranchId branch, String repositoryName) {
      ArtifactReadable repo = getRepo(branch, repositoryName);
      List<CertFileData> files = new ArrayList<>();

      TriConsumer<ArtifactId, ArtifactId, ArtifactId> consumer = (codeUnit, lastestCommitId, baselinedCommitId) -> {
         CertFileData file = new CertFileData();

         ArtifactToken codeUnitToken = queryFactory.fromBranch(branch).andId(codeUnit).getAtMostOneOrSentinal();
         if (codeUnitToken.isInvalid()) {
            return; // exclude deleted code units
         }
         file.path = codeUnitToken.getName();

         ArtifactReadable latestCommit = queryFactory.fromBranch(branch).andId(lastestCommitId).getArtifact();
         String latestSha = latestCommit.getSoleAttributeValue(GitCommitSHA);
         file.latestChangeId = latestCommit.getSoleAttributeValue(GitChangeId, latestSha);
         file.latestTimestamp = latestCommit.getSoleAttributeValue(GitCommitAuthorDate);

         ArtifactReadable baselinedCommit =
            queryFactory.fromBranch(branch).andId(baselinedCommitId).getResults().getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);
         if (baselinedCommit.isValid()) {
            file.baselinedChangeId = baselinedCommit.getSoleAttributeValue(GitChangeId);
            file.baselinedTimestamp = baselinedCommit.getSoleAttributeValue(GitCommitAuthorDate);
         }

         files.add(file);
         System.out.println(file.path);
      };

      tupleQuery.getTuple4E2E3E4FromE1(GitLatest, branch, repo, consumer);

      return files;
   }

   @Override
   public CertBaselineData getBaselineData(ArtifactReadable baselineArtifact) {
      CertBaselineData baselineData = new CertBaselineData();
      baselineData.eventName = baselineArtifact.getName();
      baselineData.changeId = baselineArtifact.getSoleAttributeValue(GitChangeId);

      ArtifactId baselinedByUser = baselineArtifact.getSoleAttributeValue(BaselinedBy);
      String userId = queryFactory.fromBranch(COMMON).andId(baselinedByUser).loadArtifactTokens(
         CoreAttributeTypes.UserId).iterator().next().getName();

      baselineData.baselinedByUserId = userId;
      baselineData.baselinedTimestamp = baselineArtifact.getSoleAttributeValue(BaselinedTimestamp);
      baselineData.reviewId = baselineArtifact.getSoleAttributeValue(ReviewId);
      baselineData.reviewStoryId = baselineArtifact.getSoleAttributeValue(ReviewStoryId);

      baselineData.files =
         Collections.transform(baselineArtifact.getRelated(CoreRelationTypes.SupportingInfo_SupportingInfo).getList(),
            ArtifactReadable::getName);
      return baselineData;
   }
}