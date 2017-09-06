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

package org.eclipse.osee.define.report.api;

import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;

public class TemplateData {

   List<ArtifactId> artifacts;
   BranchId branch;
   UserId user;
   String sessionId;
   String templateContent;
   String templateOptions;
   String templateStyles;
   IContainer folder;
   String outlineNumber;
   String outlineType;
   PresentationType presentationType;
   Map<RendererOption, Object> rendererOptions;

   public List<ArtifactId> getArtifacts() {
      return artifacts;
   }

   public void setArtifacts(List<ArtifactId> artifacts) {
      this.artifacts = artifacts;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public void setUser(UserId user) {
      this.user = user;
   }

   public UserId getUser() {
      return user;
   }

   public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
   }

   public String getSessionId() {
      return sessionId;
   }

   public String getTemplateContent() {
      return templateContent;
   }

   public void setTemplateContent(String templateContent) {
      this.templateContent = templateContent;
   }

   public String getTemplateOptions() {
      return templateOptions;
   }

   public void setTemplateOptions(String templateOptions) {
      this.templateOptions = templateOptions;
   }

   public String getTemplateStyles() {
      return templateStyles;
   }

   public void setTemplateStyles(String templateStyles) {
      this.templateStyles = templateStyles;
   }

   public IContainer getFolder() {
      return folder;
   }

   public void setFolder(IContainer folder) {
      this.folder = folder;
   }

   public String getOutlineNumber() {
      return outlineNumber;
   }

   public void setOutlineNumber(String outlineNumber) {
      this.outlineNumber = outlineNumber;
   }

   public String getOutlineType() {
      return outlineType;
   }

   public void setOutlineType(String outlineType) {
      this.outlineType = outlineType;
   }

   public PresentationType getPresentationType() {
      return presentationType;
   }

   public void setPresentationType(PresentationType presentationType) {
      this.presentationType = presentationType;
   }

   public Map<RendererOption, Object> getRendererOptions() {
      return rendererOptions;
   }

   public void setRendererOptions(Map<RendererOption, Object> rendererOptions) {
      this.rendererOptions = rendererOptions;
   }

}
