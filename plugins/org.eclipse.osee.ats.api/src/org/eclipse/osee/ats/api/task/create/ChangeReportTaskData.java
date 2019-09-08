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
package org.eclipse.osee.ats.api.task.create;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTaskData {

   public List<ChangeReportTaskTeamWfData> changeReportDatas = new ArrayList<ChangeReportTaskTeamWfData>();
   public XResultData results = new XResultData();
   boolean reportOnly = false;
   IAtsVersion targetedVersion;
   AtsUser asUser;
   // Workflow that initiated request
   ArtifactToken hostTeamWf;
   // Workflow that owns change report or empty (will be determined from create task definition team def)
   ArtifactToken chgRptTeamWf = ArtifactToken.SENTINEL;
   AtsTaskDefToken taskDefToken;
   private BranchId workOrParentBranch;
   private List<ChangeItem> changeData;
   private Collection<ArtifactId> addedModifiedArts = new HashSet<>();
   private Collection<ArtifactId> deletedArts = new HashSet<>();
   private Collection<ArtifactId> relArts = new HashSet<>();
   private Collection<ArtifactId> allArtifacts = new HashSet<>();
   private CreateTasksDefinition setDef;

   public ChangeReportTaskData() {
      // for jax-rs
   }

   public List<ChangeReportTaskTeamWfData> getChangeReportDatas() {
      return changeReportDatas;
   }

   public void addChangeReportData(ChangeReportTaskTeamWfData changeReportData) {
      changeReportDatas.add(changeReportData);
   }

   public void setChangeReportDatas(List<ChangeReportTaskTeamWfData> changeReportDatas) {
      this.changeReportDatas = changeReportDatas;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public boolean isReportOnly() {
      return reportOnly;
   }

   public void setReportOnly(boolean reportOnly) {
      this.reportOnly = reportOnly;
   }

   public IAtsVersion getTargetedVersion() {
      return targetedVersion;
   }

   public void setTargetedVersion(IAtsVersion targetedVersion) {
      this.targetedVersion = targetedVersion;
   }

   public AtsUser getAsUser() {
      return asUser;
   }

   public void setAsUser(AtsUser asUser) {
      this.asUser = asUser;
   }

   public AtsTaskDefToken getTaskDefToken() {
      return taskDefToken;
   }

   public void setTaskDefToken(AtsTaskDefToken taskDefToken) {
      this.taskDefToken = taskDefToken;
   }

   public ArtifactToken getHostTeamWf() {
      return hostTeamWf;
   }

   public void setHostTeamWf(ArtifactToken hostTeamWf) {
      this.hostTeamWf = hostTeamWf;
   }

   public ArtifactToken getChgRptTeamWf() {
      return chgRptTeamWf;
   }

   public void setChgRptTeamWf(ArtifactToken chgRptTeamWf) {
      this.chgRptTeamWf = chgRptTeamWf;
   }

   public void setWorkOrParentBranch(BranchId workOrParentBranch) {
      this.workOrParentBranch = workOrParentBranch;
   }

   public void setChangeData(List<ChangeItem> changeData) {
      this.changeData = changeData;
   }

   public BranchId getWorkOrParentBranch() {
      return workOrParentBranch;
   }

   @JsonIgnore
   public List<ChangeItem> getChangeData() {
      return changeData;
   }

   public Collection<ArtifactId> getAddedModifiedArts() {
      return addedModifiedArts;
   }

   public void setAddedModifiedArts(Collection<ArtifactId> addedModifiedArts) {
      this.addedModifiedArts = addedModifiedArts;
   }

   public Collection<ArtifactId> getDeletedArts() {
      return deletedArts;
   }

   public void setDeletedArts(Collection<ArtifactId> deletedArts) {
      this.deletedArts = deletedArts;
   }

   public Collection<ArtifactId> getRelArts() {
      return relArts;
   }

   public void setRelArts(Collection<ArtifactId> relArts) {
      this.relArts = relArts;
   }

   public Collection<ArtifactId> getAllArtifacts() {
      return allArtifacts;
   }

   public void setAllArtifacts(Collection<ArtifactId> allArtifacts) {
      this.allArtifacts = allArtifacts;
   }

   public void setSetDef(CreateTasksDefinition setDef) {
      this.setDef = setDef;
   }

   public CreateTasksDefinition getSetDef() {
      return setDef;
   }

}
