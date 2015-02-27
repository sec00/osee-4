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
package org.eclipse.osee.orcs.rest.internal;

import java.io.OutputStream;
import java.util.Arrays;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
@Path("/tag/")
public final class IndexerResource {
   private final OrcsApi orcsApi;
   private final ActivityLog activityLog;

   public IndexerResource(OrcsApi orcsApi, ActivityLog activityLog) {
      this.orcsApi = orcsApi;
      this.activityLog = activityLog;
   }

   @Path("type/{typeId}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response tagByType(@PathParam("typeId") Long typeId) throws Exception {
      return buildResponse(new StreamingLogger() {
         @Override
         public void stream(Log logger) throws Exception {
            orcsApi.getQueryIndexer(null).indexAllBranches(Arrays.asList(CoreAttributeTypes.WordTemplateContent),
               activityLog);
         }
      });
   }

   @Path("all")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response tagAllBranches() throws Exception {
      return buildResponse(new StreamingLogger() {
         @Override
         public void stream(Log logger) throws Exception {
            orcsApi.getQueryIndexer(null).indexAllBranches(activityLog);
         }
      });
   }

   private Response buildResponse(final StreamingLogger streamer) {
      StreamingOutput streamingOutput = new StreamingOutput() {
         @Override
         public void write(OutputStream output) {
            try {
               Log htmlLogger = new HtmlLogger(output);
               streamer.stream(htmlLogger);
               output.close();
            } catch (Exception ex) {
               throw new WebApplicationException(ex);
            }
         }
      };
      ResponseBuilder builder = Response.ok(streamingOutput).header(HttpHeaders.CONTENT_ENCODING, "UTF-8");
      return builder.build();
   }
}