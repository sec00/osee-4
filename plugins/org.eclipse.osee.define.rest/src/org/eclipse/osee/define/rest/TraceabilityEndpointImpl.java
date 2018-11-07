/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.BaselineEventsFolder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitRepository;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RepositoryUrl;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.define.api.CertBaselineData;
import org.eclipse.osee.define.api.CertFileData;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.TraceData;
import org.eclipse.osee.define.api.TraceabilityEndpoint;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.define.rest.internal.PublishLowHighReqStreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.template.engine.ArtifactTypeOptionsRule;

/**
 * @author Ryan D. Brooks
 */
public final class TraceabilityEndpointImpl implements TraceabilityEndpoint {
   private final OrcsApi orcsApi;
   private final TraceabilityOperations traceOps;
   private final IResourceRegistry resourceRegistry;
   private final ActivityLog activityLog;
   private final QueryFactory queryFactory;

   public TraceabilityEndpointImpl(ActivityLog activityLog, IResourceRegistry resourceRegistry, OrcsApi orcsApi, DefineApi defineApi) {
      this.orcsApi = orcsApi;
      this.traceOps = defineApi.getTraceabilityOperations();
      this.resourceRegistry = resourceRegistry;
      this.activityLog = activityLog;
      queryFactory = orcsApi.getQueryFactory();
   }

   @Override
   public Response getLowHighReqReport(BranchId branch, String selectedTypes) {
      Conditions.checkNotNull(branch, "branch query param");
      Conditions.checkNotNull(selectedTypes, "selected_types query param");
      if (branch.getId().equals(8888L) && selectedTypes.equals("8888")) {
         return Response.ok("TEST").build();
      }

      StreamingOutput streamingOutput =
         new PublishLowHighReqStreamingOutput(activityLog, orcsApi, branch, selectedTypes);
      String fileName = "Requirement_Trace_Report.xml";

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Override
   public TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType) {
      return traceOps.getSrsToImpd(branch, excludeType);
   }

   @Override
   public String getSinglePageApp() {
      OseeAppletPage pageUtil = new OseeAppletPage(queryFactory.branchQuery());

      ArtifactTypeOptionsRule selectRule =
         new ArtifactTypeOptionsRule("artifactTypeSelect", getTypes(), new HashSet<String>());
      return pageUtil.realizeApplet(resourceRegistry, "publishLowHighReport.html", getClass(), selectRule);
   }

   private Set<String> getTypes() {
      OrcsTypes orcsTypes = orcsApi.getOrcsTypes();
      ArtifactTypes artifactTypes = orcsTypes.getArtifactTypes();
      Set<String> toReturn = new HashSet<>();

      for (IArtifactType type : artifactTypes.getAll()) {
         toReturn.add(type.getName());
      }
      return toReturn;
   }

   @Override
   public ArtifactId baselineFiles(BranchId branch, String repositoryName, CertBaselineData baselineData, UserId account) {
      return traceOps.baselineFiles(branch, repositoryName, baselineData, account);
   }

   @Override
   public List<CertBaselineData> getBaselineData(BranchId branch, String repositoryName) {
      return traceOps.getBaselineData(branch, repositoryName);
   }

   @Override
   public List<CertFileData> getCertFileData(BranchId branch, String repositoryName) {
      return traceOps.getCertFileData(branch, repositoryName);
   }

   @Override
   public CertBaselineData getBaselineData(BranchId branch, ArtifactId certBaselineData) {
      ArtifactReadable baselineArtifact = queryFactory.fromBranch(branch).andId(certBaselineData).getArtifact();
      return traceOps.getBaselineData(baselineArtifact);
   }

   @Override
   public TransactionId importGitHistory(BranchId branch, String repositoryName, UserId account, String gitHistory) {
      traceOps.parseGitHistory(repositoryName, gitHistory, branch, account);
      return TransactionId.SENTINEL; //TODO: provide better return value
   }

   @Override
   public ArtifactId createGitRepository(BranchId branch, UserId account, String gitRepoUrl) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
         "TraceabilityEndpointImpl.createGitRepository()");

      String repoName = gitRepoUrl.substring(gitRepoUrl.lastIndexOf('/') + 1);
      ArtifactId gitRepo = tx.createArtifact(BaselineEventsFolder, GitRepository, repoName);
      tx.setSoleAttributeValue(gitRepo, RepositoryUrl, gitRepoUrl);

      tx.commit();
      return gitRepo;
   }

   private static final Pattern filePattern = Pattern.compile(".*\\.(java|c|h|cpp)");

   @Override
   public TransactionId importFilesAsArtifacts(BranchId branch, ArtifactTypeId artifactType, String filePath, UserId account) {
      File file = new File(filePath);
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - trace import files");

      IArtifactType artifactTypeToken = orcsApi.getOrcsTypes().getArtifactTypes().get(artifactType);

      try {
         if (file.isFile()) {
            for (String path : Lib.readListFromFile(file, true)) {
               handleDirectory(new File(path), tx, artifactTypeToken);
            }
         } else if (file.isDirectory()) {
            handleDirectory(file, tx, artifactTypeToken);
         } else {
            throw new OseeStateException("Invalid path [%s]", file.getCanonicalPath());
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return tx.commit();
   }

   private void handleDirectory(File directory, TransactionBuilder tx, IArtifactType artifactType) {
      int pathPrefixLength = directory.getParentFile().getAbsolutePath().length();

      for (File sourceFile : Lib.recursivelyListFiles(directory, null)) {
         String relativePath = sourceFile.getPath().substring(pathPrefixLength);
         ArtifactId id = tx.createArtifact(artifactType, relativePath);
      }
   }
}