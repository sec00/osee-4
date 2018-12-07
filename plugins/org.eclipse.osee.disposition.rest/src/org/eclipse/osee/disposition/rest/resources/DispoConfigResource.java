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
package org.eclipse.osee.disposition.rest.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoConfigData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoRoles;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Angel Avila
 */
public class DispoConfigResource {

   private final DispoApi dispoApi;
   private final BranchId branch;

   public DispoConfigResource(DispoApi dispoApi, BranchId branch) {
      this.dispoApi = dispoApi;
      this.branch = branch;
   }

   @PUT
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   public Response updateConfig(DispoConfigData config, @QueryParam("userName") String userName) {
      Response.Status status;
      boolean editDispoConfig = dispoApi.editDispoConfig(branch, config, userName);
      if (editDispoConfig) {
         status = Status.OK;
      } else {
         status = Status.INTERNAL_SERVER_ERROR;
      }

      return Response.status(status).build();

   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getDispoConfig() {
      Response.Status status;
      Response response;
      DispoConfig config = dispoApi.getDispoConfig(branch);

      if (config == null) {
         status = Status.NOT_FOUND;
         response = Response.status(status).build();
      } else {
         status = Status.OK;
         response = Response.status(status).entity(config).build();
      }

      return response;
   }
}
