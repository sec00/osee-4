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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgamDescriptorData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoRoles;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Angel Avila
 */
@Path("program")
public class DispoProgramResource {

   private final DispoApi dispoApi;

   public DispoProgramResource(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   /**
    * Create a new Disposition Set given a DispoSetDescriptor
    *
    * @param descriptor Descriptor Data which includes name and import path
    * @return The created Disposition Set if successful. Error Code otherwise
    * @response.representation.201.doc Created the Disposition Set
    * @response.representation.409.doc Conflict, tried to create a Disposition Set with same name
    * @response.representation.400.doc Bad Request, did not provide both a Name and a valid Import Path
    */
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public Response createProgram(DispoProgamDescriptorData programDescriptor) {
      String name = programDescriptor.getName();
      Response.Status status;
      Response response;
      if (!name.isEmpty()) {
         boolean isUniqueName = dispoApi.isUniqueProgramName(name);
         if (isUniqueName) {
            long createdProgramId = dispoApi.createDispoProgram(name);
            status = Status.CREATED;
            response = Response.status(status).entity(createdProgramId).build();
         } else {
            status = Status.CONFLICT;
            response = Response.status(status).entity(DispoMessages.Set_ConflictingNames).build();
         }
      } else {
         status = Status.BAD_REQUEST;
         response = Response.status(status).entity(DispoMessages.Set_EmptyNameOrPath).build();
      }
      return response;
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
   public Response getAllPrograms() throws JSONException {
      List<IOseeBranch> allPrograms = dispoApi.getDispoPrograms();
      Collections.sort(allPrograms, new Comparator<IOseeBranch>() {
         @Override
         public int compare(IOseeBranch o1, IOseeBranch o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
      JSONArray jarray = new JSONArray();

      //      for (IOseeBranch branch : allPrograms) {
      //         JSONObject jobject = new JSONObject();
      //         String uuid = branch.getIdString();
      //         jobject.put("value", uuid);
      //         jobject.put("text", branch.getName());
      //         jarray.put(jobject);
      //      }
      Status status;
      if (allPrograms.isEmpty()) {
         status = Status.NOT_FOUND;
      } else {
         status = Status.OK;
      }

      test();
      return Response.status(status).entity(jarray.toString()).build();
   }

   private void test() {
      List<Integer> mockL = new ArrayList<>();
      mockL.add(9);

      boolean b = isSumPresent(mockL, 9);
      System.out.println(b);

      int answer = getBadCommit(4, 600);
      System.out.println(answer);
      System.out.println();
   }

   public int getBadCommit(int s, int e) {
      if (s == e) {
         if (tester(s)) {
            return s;
         }
      } else {
         int mid = s + ((e - s) / 2);
         if (tester(mid)) {
            return getBadCommit(s, mid);
         } else {
            return getBadCommit(mid + 1, e);
         }
      }

      return -1;
   }

   private boolean tester(int cId) {
      if (cId >= 69) {
         return true;
      }
      return false;
   }

   public boolean isSumPresent(List<Integer> l, int sum) {
      boolean ret = false;

      Queue<Integer> q = new LinkedList<>();
      int tot = 0;
      for (Integer i : l) {
         if (i <= sum) {
            tot += i;
            if (tot == sum) {
               return true;
            } else if (tot < sum) {
               q.add(i);
            } else {
               q.add(i);
               while (tot > sum) {
                  int r = q.remove();
                  tot -= r;
               }
               if (tot == sum) {
                  return true;
               }
            }
         } else {
            q = null;
            tot = 0;
            q = new PriorityQueue<>();
         }
      }

      // ensure number is an candidate by checking its smaller than sum

      // if it is, see if running total plus candidate is still valid

      // if it's less add and keep going
      // if it's more remove from queueu until valid
      // if it's equal: done

      return ret;
   }

   @Path("{branchId}/set")
   public DispoSetResource getAnnotation(@PathParam("branchId") BranchId branch) {
      return new DispoSetResource(dispoApi, branch);
   }

   @Path("{branchId}/admin")
   public DispoAdminResource getDispoSetReport(@PathParam("branchId") BranchId branch) {
      return new DispoAdminResource(dispoApi, branch);
   }

   @Path("{branchId}/config")
   public DispoConfigResource getDispoDataStore(@PathParam("branchId") BranchId branch) {
      return new DispoConfigResource(dispoApi, branch);
   }
}