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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.eclipse.osee.template.engine.PageFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * http://getbootstrap.com/components/ <br />
 * 
 * @author Ryan D. Brooks
 */
@Path("/")
public final class ImportArtifactsResource {
   private final OrcsApi orcsApi;
   private final IResourceRegistry registry;
   private final QueryFactory queryFactory;
   private final ArtifactTypes artifactTypes;

   public ImportArtifactsResource(IResourceRegistry registry, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.registry = registry;
      queryFactory = orcsApi.getQueryFactory(null);
      artifactTypes = orcsApi.getOrcsTypes(null).getArtifactTypes();
   }

   @GET
   @Path("importUI")
   @Produces(MediaType.TEXT_HTML)
   public String getImportPage() {
      return PageFactory.realizePage("artifactImport.html", OseeAppletPage.class);
   }

   private String getTestJson() throws IOException {
      ResourceToken jsonToken = new ClassBasedResourceToken(-1L, "import.json", OseeAppletPage.class, "html/");
      return Lib.inputStreamToString(jsonToken.getInputStream());
   }

   @Path("import")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public void doImport() throws IOException, JSONException {
      JSONArray json = new JSONArray(getTestJson());
      TransactionBuilder txBuilder = createTxBuilder("Create Initial Feature Set");

      ArtifactReadable features =
         queryFactory.fromBranch(3765787263195969415L).andUuid(6625310).getResults().getExactlyOne();

      int artifactCount = json.length();
      for (int i = 0; i < artifactCount; i++) {
         JSONObject artifact = json.getJSONObject(i);
         ArtifactId childArtifact = createArtifact(txBuilder, artifact);
         //txBuilder.addChildren(features, childArtifact);
      }

      txBuilder.commit();
   }

   private ArtifactId createArtifact(TransactionBuilder txBuilder, JSONObject artifactJson) throws OseeCoreException, JSONException {
      IArtifactType type = artifactTypes.getByUuid(artifactJson.getLong("type"));
      JSONObject attributes = artifactJson.getJSONObject("atttributes");
      String name = attributes.getString("1152921504606847088");
      ArtifactId artifact = txBuilder.createArtifact(type, name);

      JSONArray children = artifactJson.optJSONArray("children");
      if (children != null) {
         int childrenCount = children.length();
         for (int i = 0; i < childrenCount; i++) {
            JSONObject child = children.getJSONObject(i);
            ArtifactId childArtifact = createArtifact(txBuilder, child);
            txBuilder.addChildren(artifact, childArtifact);
         }
      }
      return artifact;
   }

   private TransactionBuilder createTxBuilder(String comment) {
      TransactionFactory txFactory = orcsApi.getTransactionFactory(null);
      ArtifactReadable userArtifact =
         queryFactory.fromBranch(CoreBranches.COMMON).andUuid(50).getResults().getExactlyOne();

      return txFactory.createTransaction(3765787263195969415L, userArtifact, comment);
   }
}