/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTasks;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.task.CreateTasksOperation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskEndpointImpl implements AtsTaskEndpointApi {
   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsTaskEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   @Path("chgRpt")
   public ChangeReportTaskData create(ChangeReportTaskData changeReportTaskData) {
      return atsApi.getTaskService().createTasks(changeReportTaskData);
   }

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public JaxAtsTasks create(NewTaskDatas newTaskDatas) {
      CreateTasksOperation operation = new CreateTasksOperation(newTaskDatas, atsApi, new XResultData());
      XResultData results = operation.validate();

      if (results.isErrors()) {
         JaxAtsTasks tasks = new JaxAtsTasks();
         tasks.setResults(results);
      }

      operation.run();
      JaxAtsTasks tasks = new JaxAtsTasks();
      tasks.getTasks().addAll(operation.getTasks());
      return tasks;
   }

   @GET
   @Path("{taskId}")
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public JaxAtsTask get(@PathParam("taskId") long taskId) {
      IAtsWorkItem task =
         atsApi.getQueryService().createQuery(WorkItemType.WorkItem).isOfType(WorkItemType.Task).andIds(
            taskId).getResults().getOneOrDefault(IAtsWorkItem.SENTINEL);
      if (task.getId().equals(IAtsWorkItem.SENTINEL.getId())) {
         throw new OseeArgumentException("No Task found with id %d", taskId);
      }
      JaxAtsTask jaxAtsTask = CreateTasksOperation.createNewJaxTask(task.getId(), atsApi);
      return jaxAtsTask;
   }

   @DELETE
   @Path("{taskId}")
   @Override
   public void delete(@PathParam("taskId") long taskId) {
      IAtsWorkItem task =
         atsApi.getQueryService().createQuery(WorkItemType.WorkItem).isOfType(WorkItemType.Task).andIds(
            taskId).getResults().getOneOrDefault(IAtsWorkItem.SENTINEL);
      if (task.isValid()) {
         IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Delete Task", AtsCoreUsers.SYSTEM_USER);
         changes.deleteArtifact(task);
         changes.execute();
      }
   }
}