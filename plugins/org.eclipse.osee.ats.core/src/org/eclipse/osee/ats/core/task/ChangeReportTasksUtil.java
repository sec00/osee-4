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
package org.eclipse.osee.ats.core.task;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatch;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatchType;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTasksUtil {

   public static String AUTO_GENERATED_STATIC_ID = "AutoGenTask";
   public static final String NO_MATCHING_CHANGE_REPORT_ARTIFACT = "No Match to Change Report Artifact; ";
   public static final String TASKS_MUST_BE_AUTOGEN_CODE_OR_TEST_TASKS = "Tasks must be Auto Generated Tasks";
   private static WorkType workType;

   private ChangeReportTasksUtil() {
      // helper methods
   }

   public static void getBranchOrCommitChangeData(ChangeReportTaskData crtd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      IAtsTeamWorkflow chgRptTeamWf = atsApi.getWorkItemService().getTeamWf(crtd.getChgRptTeamWf());

      // If working branch, get change data from branch
      List<ChangeItem> changeData = null;
      BranchId workOrParentBranch = null;
      if (atsApi.getBranchService().isWorkingBranchInWork(chgRptTeamWf)) {
         IOseeBranch workingBranch = atsApi.getBranchService().getWorkingBranch(chgRptTeamWf);
         workOrParentBranch = workingBranch;
         changeData = atsApi.getBranchService().getChangeData(workingBranch);
         crtd.getResults().logf("Using Working Branch %s\n", workingBranch.toStringWithId());
      }
      // Else get change data from earliest transaction
      else if (atsApi.getBranchService().isCommittedBranchExists(chgRptTeamWf)) {
         TransactionToken tx = atsApi.getBranchService().getEarliestTransactionId(chgRptTeamWf);
         workOrParentBranch = tx.getBranch();
         changeData = atsApi.getBranchService().getChangeData(tx);
         crtd.getResults().logf("Using Commit Branch %s\n",
            atsApi.getBranchService().getBranch(tx.getBranch()).toStringWithId());
      }
      crtd.setWorkOrParentBranch(workOrParentBranch);
      crtd.setChangeData(changeData);
   }

   @SuppressWarnings("unchecked")
   public static void processChangeData(ChangeReportTaskData crtd) {
      for (ChangeItem item : crtd.getChangeData()) {
         if (item.getChangeType() == ChangeType.ARTIFACT_CHANGE && item.isDeleted()) {
            crtd.getDeletedArts().add(item.getArtId());
         } else {
            if (item.getChangeType() == ChangeType.ATTRIBUTE_CHANGE || item.getChangeType() == ChangeType.ARTIFACT_CHANGE) {
               crtd.getAddedModifiedArts().add(item.getArtId());
            } else if (item.getChangeType() == ChangeType.RELATION_CHANGE) {
               crtd.getRelArts().add(item.getArtId());
               crtd.getRelArts().add(item.getArtIdB());
            }
         }
      }
      crtd.getAllArtifacts().addAll(org.eclipse.osee.framework.jdk.core.util.Collections.setUnion(
         crtd.getAddedModifiedArts(), crtd.getDeletedArts(), crtd.getRelArts()));
   }

   /**
    * Compare already TaskComputedAsNeeded task matches with existing tasks and determine fate.
    */
   public static void determinExistingTaskMatchType(Map<ArtifactId, ArtifactReadable> idToArtifact, ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, CreateTasksDefinition setDef, WorkType workType, IAtsTeamWorkflow destTeamWf) {
      AtsApi atsApi = AtsApiService.get();
      Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks(destTeamWf);
      System.out.println(String.format("%s tasks for %s", tasks.size(), destTeamWf.toStringWithId()));
      for (IAtsTask task : tasks) {
         ArtifactId refChgArtId = atsApi.getAttributeResolver().getSoleArtifactIdReference(task,
            AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);

         // Search for artifact token (include deleted) cause needed name to compare
         if (refChgArtId.isValid()) {
            boolean found = false;
            for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
               // We found matching TaskMatch
               if (refChgArtId.getId().equals(taskMatch.getChgRptArt().getId()) && task.getName().equals(
                  taskMatch.getTaskName())) {
                  System.err.println("Match\n");
                  taskMatch.setTaskName(task.getName());
                  taskMatch.setType(ChangeReportTaskMatchType.Match);
                  taskMatch.setTaskWf(task);
                  taskMatch.setTaskTok(task.getArtifactToken());
                  found = true;
                  break;
               }
            }

            // If not, add task match that will probably be marked for removal
            if (!found) {
               System.err.println("No Match\n");
               ChangeReportTaskMatch newTaskMatch = new ChangeReportTaskMatch();
               newTaskMatch.setTaskName(task.getName());
               newTaskMatch.setTaskWf(task);
               newTaskMatch.setTaskTok(task.getArtifactToken());
               newTaskMatch.setType(ChangeReportTaskMatchType.TaskRefAttrValidButRefChgArtMissing);
               crttwd.addTaskMatch(newTaskMatch);
            }
         } else {
            // No matching rel chg art attr; mark for removal
            ChangeReportTaskMatch taskMatch = new ChangeReportTaskMatch();
            taskMatch.setTaskName(task.getName());
            taskMatch.setTaskWf(task);
            taskMatch.setTaskTok(task.getArtifactToken());
            taskMatch.setType(ChangeReportTaskMatchType.TaskRefAttrMissing);
            crttwd.addTaskMatch(taskMatch);
         }
      }
   }

   /**
    * @return task match if task referenced
    */
   public static ChangeReportTaskMatch getTaskMatch(IAtsTask task, ArtifactId referencedChgArt, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      ArtifactId taskRefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(task,
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
      for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
         if (referencedChgArt.getId().equals(taskRefArt.getId())) {
            System.err.println("Match\n");
            taskMatch.setTaskName(task.getName());
            taskMatch.setType(ChangeReportTaskMatchType.Match);
            taskMatch.setTaskWf(task);
            taskMatch.setTaskTok(task.getArtifactToken());
            return taskMatch;
         }
      }
      System.err.println("No Match\n");
      return null;
   }

   // TBD - Need to add this check back in if appropriate
   private static boolean isAttributeTypeTaskable(Id itemTypeId, CreateTasksDefinition setDef) {
      AttributeTypeId attrType = AttributeTypeId.valueOf(itemTypeId.getId());
      if (setDef.getChgRptOptions().getAttributeTypes().contains(
         attrType) || !setDef.getChgRptOptions().getNotAttributeTypes().contains(attrType)) {
         return true;
      }
      return false;
   }

   // TBD - Need to add this check back in if appropriate
   private static boolean isArtifactTypeTaskable(ArtifactId artifact, ChangeReportTaskTeamWfData crd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      ArtifactTypeToken artType = atsApi.getStoreService().getArtifactType(artifact);
      if (setDef.getChgRptOptions().getArtifactTypes().contains(
         artType) || !setDef.getChgRptOptions().getNotArtifactTypes().contains(artType)) {
         return true;
      }
      return false;
   }

   public static void getTasksComputedAsNeeded(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      getModifiedArifactNames(crtd, crttwd, atsApi);
      getDeletedArifactNames(crtd, crttwd, atsApi);
      getRelArtifactNames(crtd, crttwd, atsApi);
      getExtensionArtifactNames(crtd, crttwd, atsApi);
   }

   private static void getExtensionArtifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      // TBD - Allow extensions to add tasks
   }

   private static void getRelArtifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      for (ArtifactId chgRptArt : crtd.getRelArts()) {
         logAndAddTaskName(crtd, crttwd, atsApi, chgRptArt, "Relation");
      }
   }

   private static void getModifiedArifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      for (ArtifactId chgRptArt : crtd.getAddedModifiedArts()) {
         logAndAddTaskName(crtd, crttwd, atsApi, chgRptArt, "Add/Mod");
      }
   }

   private static void getDeletedArifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      for (ArtifactId chgRptArt : crtd.getDeletedArts()) {
         logAndAddTaskName(crtd, crttwd, atsApi, chgRptArt, "Deleted");
      }
   }

   private static void logAndAddTaskName(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi, ArtifactId chgRptArt, String chgType) {
      ArtifactReadable art = (ArtifactReadable) atsApi.getQueryService().getArtifact(chgRptArt,
         crtd.getWorkOrParentBranch(), DeletionFlag.INCLUDE_DELETED);
      String safeName = String.format("Handle %s change to [%s]", chgType, art.getSafeName());
      if ("Deleted".equals(chgType)) {
         safeName += " (Deleted)";
      }
      ChangeReportTaskMatch match = new ChangeReportTaskMatch();
      match.setChgRptArt(chgRptArt);
      match.setTaskName(safeName);
      match.setType(ChangeReportTaskMatchType.TaskComputedAsNeeded);
      crttwd.getTaskMatches().add(match);
   }

   public static IAtsTeamWorkflow getDestTeamWfOrNull(ChangeReportTaskTeamWfData crttwd, WorkType workType, AtsApi atsApi, IAtsTeamWorkflow sourceTeamWf, IAtsTeamDefinition destTeamDef) {
      // Try to find by Derive_To first
      ArtifactToken chgRptTeamWf = crttwd.getChgRptTeamWf();
      for (ArtifactToken related : atsApi.getRelationResolver().getRelated(chgRptTeamWf, AtsRelationTypes.Derive_To)) {
         if (related instanceof IAtsTeamWorkflow) {
            IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) related;
            if (teamWf.getTeamDefinition().equals(crttwd.getDestTeamDef())) {
               crttwd.setDestTeamWf(teamWf.getStoreObject());
               return teamWf;
            }
         }
      }
      //       Else, look through siblings for matching team def
      for (IAtsTeamWorkflow teamWf : sourceTeamWf.getParentAction().getTeamWorkflows()) {
         if (!teamWf.equals(sourceTeamWf) && teamWf.getTeamDefinition().equals(destTeamDef)) {
            crttwd.setDestTeamWf(teamWf.getArtifactToken());
            return teamWf;
         }
      }
      return null;
   }

}
