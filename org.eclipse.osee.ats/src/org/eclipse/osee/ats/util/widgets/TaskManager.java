/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TaskArtifact.TaskStates;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserCache;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class TaskManager {
   private final SMAManager smaMgr;

   public TaskManager(SMAManager smaMgr) {
      super();
      this.smaMgr = smaMgr;
   }

   public Collection<TaskArtifact> getTaskArtifacts() throws OseeCoreException {
      return smaMgr.getSma().getRelatedArtifacts(AtsRelation.SmaToTask_Task, TaskArtifact.class);
   }

   public Collection<TaskArtifact> getTaskArtifactsFromCurrentState() throws OseeCoreException {
      return getTaskArtifacts(smaMgr.getStateMgr().getCurrentStateName());
   }

   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException {
      List<TaskArtifact> arts = new ArrayList<TaskArtifact>();
      for (TaskArtifact taskArt : smaMgr.getSma().getRelatedArtifacts(AtsRelation.SmaToTask_Task, TaskArtifact.class)) {
         if (taskArt.getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "").equals(
               stateName)) arts.add(taskArt);
      }
      return arts;
   }

   public boolean hasTaskArtifacts() {
      try {
         return smaMgr.getSma().getRelatedArtifactsCount(AtsRelation.SmaToTask_Task) > 0;
      } catch (OseeCoreException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return false;
      }
   }

   public TaskArtifact createNewTask(String title, boolean persist) throws OseeCoreException {
      return createNewTask(Arrays.asList(UserCache.getUser()), title, persist);
   }

   public TaskArtifact createNewTask(User assignee, String title, boolean persist) throws OseeCoreException {
      return createNewTask(Arrays.asList(assignee), title, persist);
   }

   public TaskArtifact createNewTask(Collection<User> assignees, String title, boolean persist) throws OseeCoreException {
      TaskArtifact taskArt = null;
      taskArt =
            (TaskArtifact) ArtifactTypeManager.addArtifact(TaskArtifact.ARTIFACT_NAME,
                  BranchManager.getAtsBranch(), title);
      taskArt.getSmaMgr().getLog().addLog(LogType.Originated, "", "");

      // Initialize state machine
      taskArt.getSmaMgr().getStateMgr().initializeStateMachine(TaskStates.InWork.name(), assignees);
      taskArt.getSmaMgr().getLog().addLog(LogType.StateEntered, "InWork", "");

      // Set parent state task is related to
      taskArt.setSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(),
            smaMgr.getStateMgr().getCurrentStateName());

      smaMgr.getSma().addRelation(AtsRelation.SmaToTask_Task, taskArt);
      if (persist) {
         taskArt.persistAttributesAndRelations();
      }

      return taskArt;
   }

   public Result areTasksComplete() {
      return areTasksComplete(true);
   }

   public Result areTasksComplete(boolean popup) {
      try {
         for (TaskArtifact taskArt : getTaskArtifacts()) {
            if (!taskArt.isCompleted() && taskArt.isCancelled()) return new Result("Not Complete");
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return Result.TrueResult;
   }

   /**
    * Return Estimated Task Hours of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Estimated Hours
    */
   public double getEstimatedHours(String relatedToStateName) throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName))
         hours += taskArt.getEstimatedHoursTotal();
      return hours;
   }

   /**
    * Return Estimated Hours for all tasks
    * 
    * @return
    * @throws Exception
    */
   public double getEstimatedHours() throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts())
         hours += taskArt.getEstimatedHoursFromArtifact();
      return hours;

   }

   /**
    * Return Remain Task Hours of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Remain Hours
    */
   public double getRemainHours(String relatedToStateName) throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName))
         hours += taskArt.getRemainHoursFromArtifact();
      return hours;
   }

   /**
    * Return Remain Hours for all tasks
    * 
    * @return
    * @throws Exception
    */
   public double getRemainHours() throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts())
         hours += taskArt.getRemainHoursFromArtifact();
      return hours;

   }

   /**
    * Return Hours Spent for Tasks of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Hours Spent
    */
   public double getHoursSpent(String relatedToStateName) throws OseeCoreException {
      double spent = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName))
         spent += taskArt.getHoursSpentSMATotal();
      return spent;
   }

   /**
    * Return Total Percent Complete / # Tasks for "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Percent Complete.
    */
   public int getPercentComplete(String relatedToStateName) throws OseeCoreException {
      int spent = 0;
      Collection<TaskArtifact> taskArts = getTaskArtifacts(relatedToStateName);
      for (TaskArtifact taskArt : taskArts)
         spent += taskArt.getPercentCompleteSMATotal();
      if (spent == 0) return 0;
      return spent / taskArts.size();
   }

}
