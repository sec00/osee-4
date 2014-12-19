/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoFactory;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.json.JSONException;

/**
 * @author Angel Avila
 */
@Path("program")
public class DispoProgramResource {

   private final DispoApi dispoApi;
   private final DispoFactory dispoFactory;

   public DispoProgramResource(DispoApi dispoApi, DispoFactory dispoFactory) {
      this.dispoApi = dispoApi;
      this.dispoFactory = dispoFactory;
   }

   /**
    * Get all Disposition Programs as JSON
    * 
    * @return The Disposition Programs found
    * @throws JSONException
    * @response.representation.200.doc OK, Found Disposition Program
    * @response.representation.404.doc Not Found, Could not find any Disposition Programs
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Iterable<IOseeBranch> getAllPrograms() {
      ResultSet<IOseeBranch> allPrograms = dispoApi.getDispoPrograms();
      return allPrograms;
   }

   @Path("{programId}/set")
   public DispoSetResource getAnnotation(@PathParam("programId") String programId) {
      return new DispoSetResource(dispoApi, dispoFactory.createProgram(programId, Long.parseLong(programId)));
   }

   @Path("{programId}/admin")
   public DispoAdminResource getDispoSetReport(@PathParam("programId") String programId) {
      return new DispoAdminResource(dispoApi, dispoFactory.createProgram(programId, Long.parseLong(programId)));
   }
}