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
package org.eclipse.osee.define.report.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Random;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Ryan D. Brooks
 */
@Path("/")
public final class IpaDemoResource {
   private final QueryFactory queryFactory;
   private final Random rand = new Random();
   private final TransactionFactory transactionFactory;

   public IpaDemoResource(OrcsApi orcsApi) {
      queryFactory = orcsApi.getQueryFactory();
      transactionFactory = orcsApi.getTransactionFactory();
   }

   /**
    * create a tree of component artifacts with the given breadth and depth. The total number crated is given by the
    * Power Sum: for i = 1..D, sum (B^i) = B + B^2 + ... + B^D
    *
    * @param breadth
    * @param depth
    * @return
    */
   @Path("ipa")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String createIpaArtifacts(@QueryParam("breadth") int breadth, @QueryParam("depth") int depth) {
      TransactionBuilder txBuilder = transactionFactory.createTransaction(COMMON, getUser(), "Create IPA demo data");
      createChildren(txBuilder, getFolder("System Test"), breadth, depth, 0);

      txBuilder.commit();
      return "done";
   }

   private void createChildren(TransactionBuilder txBuilder, ArtifactId parent, int breadth, int depth, int currentDepth) {
      if (currentDepth >= depth) {
         return;
      }
      for (int childCount = 0; childCount < breadth; childCount++) {
         ArtifactId component = txBuilder.createArtifact(CoreArtifactTypes.Component, getName());
         txBuilder.setSoleAttributeValue(component, CoreAttributeTypes.Developmental, getDevelopmental());
         txBuilder.addChildren(parent, component);

         createChildren(txBuilder, component, breadth, depth, currentDepth + 1);
      }
   }

   private ArtifactReadable getUser() {
      return queryFactory.fromBranch(COMMON).andUuid(50).getResults().getExactlyOne();
   }

   private ArtifactReadable getFolder(String name) {
      ArtifactReadable root = queryFactory.fromBranch(CoreBranches.COMMON).andUuid(
         CoreArtifactTokens.DefaultHierarchyRoot.getUuid()).getResults().getAtMostOneOrNull();

      for (ArtifactReadable child : root.getChildren()) {
         if (child.getName().equals(name)) {
            return child;
         }
      }
      return null;
   }

   private boolean getDevelopmental() {
      return rand.nextDouble() < 0.5;
   }

   private String getName() {
      return String.valueOf(rand.nextInt());
   }
}