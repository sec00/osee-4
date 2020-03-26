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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTaskTeamWfData {

   WorkType workType;
   ArtifactToken chgRptTeamWf;
   ArtifactToken destTeamWf;
   ArtifactToken destTeamDef;
   XResultData rd;
   boolean reportOnly;
   /**
    * Collection of task objects that are created for all tasks needed to be created and then later used to match any
    * tasks already created
    */
   Collection<ChangeReportTaskMatch> taskMatches = new ArrayList<ChangeReportTaskMatch>();
   NewTaskData newTaskData = new NewTaskData();

   public ChangeReportTaskTeamWfData() {
      // for jax-rs
   }

   public WorkType getWorkType() {
      return workType;
   }

   public void setWorkType(WorkType workType) {
      this.workType = workType;
   }

   public ArtifactToken getChgRptTeamWf() {
      return chgRptTeamWf;
   }

   public void setChgRptTeamWf(ArtifactToken chgRptTeamWf) {
      this.chgRptTeamWf = chgRptTeamWf;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   public boolean isReportOnly() {
      return reportOnly;
   }

   public void setReportOnly(boolean reportOnly) {
      this.reportOnly = reportOnly;
   }

   public boolean isPersist() {
      return !reportOnly;
   }

   public NewTaskData getNewTaskData() {
      return newTaskData;
   }

   public void setNewTaskData(NewTaskData newTaskData) {
      this.newTaskData = newTaskData;
   }

   public ArtifactToken getDestTeamWf() {
      return destTeamWf;
   }

   public void setDestTeamWf(ArtifactToken destTeamWf) {
      this.destTeamWf = destTeamWf;
   }

   public Collection<ChangeReportTaskMatch> getTaskMatches() {
      return taskMatches;
   }

   public void setTaskMatches(Collection<ChangeReportTaskMatch> taskMatches) {
      this.taskMatches = taskMatches;
   }

   public void addTaskMatch(ChangeReportTaskMatch taskMatch) {
      this.taskMatches.add(taskMatch);
   }

   public Set<String> getTaskNames() {
      Set<String> names = new HashSet<>();
      for (ChangeReportTaskMatch taskMatch : getTaskMatches()) {
         if (taskMatch.getMatchType() == ChangeReportTaskMatchType.Match || taskMatch.getMatchType() == ChangeReportTaskMatchType.Manual) {
            names.add(taskMatch.getTaskName());
         }
      }
      return names;
   }

   public ArtifactId getToChgArt(String name) {
      for (ChangeReportTaskMatch taskMatch : getTaskMatches()) {
         if (taskMatch.getTaskName().equals(name)) {
            return taskMatch.getChgRptArt();
         }
      }
      return null;
   }

   public void addTaskMatch(ArtifactId art, ChangeReportTaskMatchType changeReportTaskMatchType, String format, Object... data) {
      ChangeReportTaskMatch taskMatch = new ChangeReportTaskMatch();
      taskMatch.setTaskName(String.format(format, data));
      if (art != null) {
         taskMatch.setChgRptArt(art);
      }
      taskMatch.setType(changeReportTaskMatchType);
      taskMatches.add(taskMatch);
   }

   public ArtifactToken getDestTeamDef() {
      return destTeamDef;
   }

   public void setDestTeamDef(ArtifactToken destTeamDef) {
      this.destTeamDef = destTeamDef;
   }

}