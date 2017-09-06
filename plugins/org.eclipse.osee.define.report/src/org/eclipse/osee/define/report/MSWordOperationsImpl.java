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
package org.eclipse.osee.define.report;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.osee.define.report.api.DataRightsOperations;
import org.eclipse.osee.define.report.api.MSWordOperations;
import org.eclipse.osee.define.report.api.WordTemplateContentData;
import org.eclipse.osee.define.report.api.WordUpdateChange;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.eclipse.osee.define.report.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.report.internal.wordupdate.WordTemplateProcessor;
import org.eclipse.osee.define.report.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.osgi.service.event.EventAdmin;

public class MSWordOperationsImpl implements MSWordOperations {

   private final OrcsApi orcsApi;
   private final Log logger;
   private final EventAdmin eventAdmin;
   private final DataRightsOperations dataRights;

   public MSWordOperationsImpl(OrcsApi orcsApi, Log logger, EventAdmin eventAdmin, DataRightsOperations dataRights) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.eventAdmin = eventAdmin;
      this.dataRights = dataRights;
   }

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) {
      WordTemplateContentRendererHandler wordRendererHandler = new WordTemplateContentRendererHandler(orcsApi);

      return wordRendererHandler.renderWordML(data);
   }

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      WordUpdateArtifact updateArt = new WordUpdateArtifact(orcsApi, eventAdmin);
      return updateArt.updateArtifacts(data);
   }

   @Override
   public InputStream applyTemplate(List<ArtifactId> artifacts, BranchId branch, UserId user, String sessionId, String templateContent, String templateOptions, String templateStyles, IContainer folder, String outlineNumber, String outlineType, PresentationType presentationType, Map<RendererOption, Object> rendererOptions) {
      WordTemplateProcessor wtp = new WordTemplateProcessor(orcsApi, dataRights, user, sessionId, this);
      List<ArtifactReadable> artifactReadables =
         orcsApi.getQueryFactory().fromBranch(branch).andIds(artifacts).getResults().getList();

      return wtp.applyTemplate(artifactReadables, templateContent, templateOptions, templateStyles, folder,
         outlineNumber, outlineType, presentationType, rendererOptions);
   }

   @Override
   public InputStream applyNestedTemplates(ArtifactId masterTemplateArtifact, ArtifactId slaveTemplateArtifact, List<ArtifactId> artifacts, BranchId branch, UserId user, String sessionId, Map<RendererOption, Object> rendererOptions) {
      WordTemplateProcessor wtp = new WordTemplateProcessor(orcsApi, dataRights, user, sessionId, this);
      return wtp.applyNestedTemplates(masterTemplateArtifact, slaveTemplateArtifact, artifacts, branch,
         rendererOptions);
   }
}
