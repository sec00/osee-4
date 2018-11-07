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
package org.eclipse.osee.define.api;

import java.io.Writer;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Morgan E. Cook
 */
public interface TraceabilityOperations {

   void generateTraceReport(BranchId branchId, String codeRoot, String traceRoot, Writer providedWriter, IArtifactType artifactType, AttributeTypeToken attributeType);

   TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType);

   void parseGitHistory(String repositoryName, String gitHistory, BranchId branch, UserId account);

   void parseGitHistory(ArtifactId repository, String gitHistory, BranchId branch, UserId account);

   ArtifactId baselineFiles(BranchId branch, String repositoryName, CertBaselineData baselineData, UserId account);

   ArtifactId getRepoId(BranchId branch, String repositoryName);

   ArtifactReadable getRepo(BranchId branch, String repositoryName);

   CertBaselineData getBaselineData(ArtifactReadable baselineArtifact);

   List<CertBaselineData> getBaselineData(BranchId branch, String repositoryName);

   /**
    * @return list of current files (excluding deleted) in given Git repository with their latest change and baselined
    * data
    */
   List<CertFileData> getCertFileData(BranchId branch, String repositoryName);
}