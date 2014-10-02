/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.app.internal;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * http://getbootstrap.com/components/ <br />
 * 
 * @author Ryan D. Brooks
 */
@Path("/")
public final class OseeSinglePageAppResource {
   private final OrcsApi orcsApi;
   private final IResourceRegistry registry;
   private final QueryFactory queryFactory;

   public OseeSinglePageAppResource(IResourceRegistry registry, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.registry = registry;
      queryFactory = orcsApi.getQueryFactory(null);
   }

   @GET
   @Path("{appId}")
   @Produces(MediaType.TEXT_HTML)
   public String getSinglePageApp(@PathParam("appId") int appId) throws JSONException {
      ArtifactReadable app = getAppArtifact(appId);
      String jsonString = app.getSoleAttributeAsString(CoreAttributeTypes.OseeAppDefinition);
      return realizeApp(jsonString, appId);
   }

   public String realizeApp(String jsonString, int appId) throws JSONException {
      JSONObject json = new JSONObject(jsonString);

      String[] keyValues =
         {
            "author",
            json.optString("author", "anonymous"),
            "title",
            json.optString("title", "OSEE Single Page App"),
            "description",
            json.optString("description"),
            "subtitle",
            json.optString("subtitle"),
            "appId",
            String.valueOf(appId)};
      PageCreator page = PageFactory.newPageCreator(null, keyValues);
      page.addSubstitution(new SinglePageAppRule(orcsApi, json));

      ResourceToken templateResource = new ClassBasedResourceToken("oseeSinglePageApp.html", OseeAppletPage.class);

      return page.realizePage(templateResource);
   }

   private String getTestJson(String name) throws IOException {
      ResourceToken jsonToken = new ClassBasedResourceToken(-1L, "test.json", OseeAppletPage.class, "html/");

      return Lib.inputStreamToString(jsonToken.getInputStream()).replace("\"System Safety Report", "\"" + name);
   }

   private ArtifactReadable getAppArtifact(long appId) {
      QueryBuilder query = queryFactory.fromBranch(CoreBranches.COMMON).andUuid(appId);
      return query.getResults().getOneOrNull();
   }

   @Path("{appId}/source")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getSource(@PathParam("appId") int appId) throws IOException {
      ArtifactReadable app = getAppArtifact(appId);
      String name = app.getName();
      String json = app.getSoleAttributeAsString(CoreAttributeTypes.OseeAppDefinition);

      return PageFactory.realizePage("oseeSinglePageAppSource.html", OseeAppletPage.class, "title", name, "appSource",
         json);
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAppList() {
      QueryBuilder query = queryFactory.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.OseeApp);
      return PageFactory.realizePage("oseeSinglePageAppList.html", OseeAppletPage.class,
         new AppListRule(query.getResults()));
   }

   @POST
   @Path("create/{name}")
   public String createAppArtifact(@PathParam("name") String name) throws OseeCoreException, IOException {
      TransactionBuilder txBuilder = createTxBuilder("Create Osee App");
      ArtifactId artifact = txBuilder.createArtifact(CoreArtifactTypes.OseeApp, name);
      txBuilder.createAttribute(artifact, CoreAttributeTypes.OseeAppDefinition, getTestJson(name));
      txBuilder.commit();
      return String.valueOf(queryFactory.fromBranch(CoreBranches.COMMON).andGuid(artifact.getGuid()).getResultsAsLocalIds().getExactlyOne().getLocalId());
   }

   @Path("{appId}")
   @POST
   @Produces(MediaType.TEXT_HTML)
   public void saveSource(@PathParam("appId") int appId) {
      ArtifactReadable app = getAppArtifact(appId);

      TransactionBuilder txBuilder = createTxBuilder("Update App Source");
      txBuilder.setSoleAttributeValue(app, CoreAttributeTypes.OseeAppDefinition, "");
      txBuilder.commit();
   }

   private TransactionBuilder createTxBuilder(String comment) {
      TransactionFactory txFactory = orcsApi.getTransactionFactory(null);
      ArtifactReadable userArtifact =
         orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();

      return txFactory.createTransaction(CoreBranches.COMMON, userArtifact, comment);
   }
}