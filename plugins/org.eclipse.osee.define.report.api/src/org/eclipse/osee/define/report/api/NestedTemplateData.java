/*
 * Created on Aug 31, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.report.api;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.util.RendererOption;

public class NestedTemplateData {

   List<ArtifactId> artifacts;
   BranchId branch;
   UserId user;
   String sessionId;
   ArtifactId masterTemplate;
   ArtifactId slaveTemplate;
   Map<RendererOption, Object> rendererOptions;

   public void setArtifacts(List<ArtifactId> artifacts) {
      this.artifacts = artifacts;
   }

   public List<ArtifactId> getArtifacts() {
      return artifacts;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public BranchId getBranch() {
      return branch;
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

   public void setMasterTemplate(ArtifactId masterTemplate) {
      this.masterTemplate = masterTemplate;
   }

   public ArtifactId getMasterTemplate() {
      return masterTemplate;
   }

   public void setSlaveTemplate(ArtifactId slaveTemplate) {
      this.slaveTemplate = slaveTemplate;
   }

   public ArtifactId getSlaveTemplate() {
      return slaveTemplate;
   }

   public void setRendererOptions(Map<RendererOption, Object> rendererOptions) {
      this.rendererOptions = rendererOptions;
   }

   public Map<RendererOption, Object> getRendererOptions() {
      return rendererOptions;
   }

}
