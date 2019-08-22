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
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfiguration;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.rest.internal.demo.DemoDatabaseConfig;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public final class AtsConfigEndpointImpl implements AtsConfigEndpointApi {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private final Log logger;
   private final ExecutorAdmin executorAdmin;
   private List<ArtifactImage> images;

   public AtsConfigEndpointImpl(AtsApi atsApi, OrcsApi orcsApi, Log logger, ExecutorAdmin executorAdmin) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.executorAdmin = executorAdmin;
   }

   @Override
   public String requestCacheReload() {
      executorAdmin.submit("REST requested ATS configuration cache reload", getConfigService);
      return "ATS configuration cache reload request submitted.";
   }

   private final Runnable getConfigService = new Runnable() {

      @Override
      public void run() {
         atsApi.getConfigService().getConfigurationsWithPend();
      }
   };

   @Override
   public AtsConfigurations get() {
      return atsApi.getConfigService().getConfigurations();
   }

   @Override
   public List<ArtifactImage> getArtifactImages() {
      if (images == null) {
         images = new LinkedList<>();
         images.addAll(AtsArtifactImages.getImages());
         for (ArtifactTypeToken artifactType : atsApi.getStoreService().getTeamWorkflowArtifactTypes()) {
            images.add(ArtifactImage.construct(artifactType, AtsArtifactImages.AGILE_TASK.getImageName(),
               AtsArtifactImages.AGILE_TASK.getBaseUrl()));
         }
      }
      return images;
   }

   @Override
   public AtsConfigurations getWithPend() {
      return atsApi.getConfigService().getConfigurationsWithPend();
   }

   @Override
   public ViewModel getNewSource() {
      return new ViewModel("templates/newConfigBranch.html");
   }

   @Override
   public AtsConfiguration createConfig(MultivaluedMap<String, String> form) {

      // get parameters
      BranchId fromBranchId = BranchId.valueOf(form.getFirst("fromBranchid"));
      String newBranchName = form.getFirst("newBranchName");
      Conditions.checkNotNullOrEmpty(newBranchName, "newBranchName");
      String userId = form.getFirst("userId");
      Conditions.checkNotNullOrEmpty(userId, "UserId");
      IAtsUser user = atsApi.getUserService().getUserById(userId);
      if (user == null) {
         logger.error("User by id [%s] does not exist", userId);
      }
      BranchId fromBranch = orcsApi.getQueryFactory().branchQuery().andId(fromBranchId).getResults().getExactlyOne();

      // Create new baseline branch off Root
      BranchId newBranch = orcsApi.getBranchOps().createTopLevelBranch(IOseeBranch.create(newBranchName), user);

      // Introduce all ATS heading artifacts to new branch
      introduceAtsHeadingArtifacts(fromBranch, newBranch, user);

      // Create config artifact on Common
      AtsConfiguration config = createConfigArtifactOnCommon(newBranchName, user, newBranch);

      // Return new branch id
      return config;
   }

   private void introduceAtsHeadingArtifacts(BranchId fromBranch, BranchId newBranch, UserId userId) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(newBranch, userId, "Add ATS Configuration");

      ArtifactId headingArt = introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.HeadingFolder, newBranch,
         CoreArtifactTokens.OseeConfiguration, null);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.TopActionableItem, newBranch, null, headingArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.TopTeamDefinition, newBranch, null, headingArt);
      ArtifactId configArt =
         introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.HeadingFolder, newBranch, null, headingArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.ConfigsFolder, newBranch, null, configArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.Users, newBranch, null, configArt);

      tx.commit();
   }

   private ArtifactId introduceAndRelateTo(TransactionBuilder tx, BranchId fromBranch, ArtifactToken introToken, BranchId newBranch, ArtifactToken parentToken, ArtifactId parentArt) {
      ArtifactReadable introArt =
         orcsApi.getQueryFactory().fromBranch(fromBranch).andId(introToken).getResults().getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL);

      if (introArt.getId().equals(ArtifactReadable.SENTINEL.getId())) {
         introArt =
            orcsApi.getQueryFactory().fromBranch(fromBranch).andTypeEquals(introToken.getArtifactType()).andNameEquals(
               introToken.getName()).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      }
      Conditions.assertNotSentinel(introArt);

      ArtifactId artifact = tx.introduceArtifact(fromBranch, introArt);
      if (parentToken != null && !parentToken.getId().equals(Id.SENTINEL)) {
         parentArt =
            orcsApi.getQueryFactory().fromBranch(newBranch).andId(parentToken).getResults().getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);
         if (parentArt.getId().equals(ArtifactReadable.SENTINEL.getId())) {
            parentArt = orcsApi.getQueryFactory().fromBranch(newBranch).andTypeEquals(
               parentToken.getArtifactType()).andNameEquals(parentToken.getName()).getResults().getAtMostOneOrDefault(
                  ArtifactReadable.SENTINEL);
         }
      }
      tx.addChild(parentArt, artifact);
      return artifact;
   }

   private AtsConfiguration createConfigArtifactOnCommon(String branchName, UserId userId, BranchId branch) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userId, "Add ATS Configuration");
      AtsConfiguration config = new AtsConfiguration();
      config.setName(branchName);
      config.setBranchId(branch);
      config.setIsDefault(false);
      ArtifactId configArt = tx.createArtifact(AtsArtifactTypes.Configuration, branchName);
      config.setArtifactId(configArt);
      tx.createAttribute(configArt, AtsAttributeTypes.AtsConfiguredBranch, branch.getIdString());
      XResultData rd = new XResultData();
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsApi, orcsApi);

      // Get or create Configs folder
      ArtifactId configsFolderArt = update.getOrCreateConfigsFolder(userId, rd);
      if (rd.isErrors()) {
         throw new OseeStateException(rd.toString());
      }
      // Add configuration to configs folder
      tx.relate(configsFolderArt, CoreRelationTypes.Default_Hierarchical__Child, configArt);
      tx.commit();
      return config;
   }

   @Override
   public XResultData createUpdateConfig() {
      XResultData resultData = new XResultData(false);
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsApi, orcsApi);
      update.createUpdateConfig(resultData);
      if (resultData.isEmpty()) {
         resultData.log("Nothing to update");
      }
      return resultData;
   }

   @Override
   public List<AtsAttributeValueColumn> generateAttrTypeViews() throws Exception {
      Map<String, AttributeTypeToken> idToToken = new HashMap<>();
      for (AttributeTypeToken attrType : orcsApi.getOrcsTypes().getAttributeTypes().getAll()) {
         idToToken.put(attrType.getName(), attrType);
      }

      List<String> sortedIds = new LinkedList<>();
      sortedIds.addAll(idToToken.keySet());
      Collections.sort(sortedIds);

      List<AtsAttributeValueColumn> columns = new LinkedList<>();
      for (String id : sortedIds) {
         AttributeTypeToken attrType = idToToken.get(id);
         ColumnAlign columnAlign = ColumnAlign.Left;
         SortDataType sortDataType = SortDataType.String;
         int width = 60;

         boolean isBoolean = orcsApi.getOrcsTypes().getAttributeTypes().isBooleanType(attrType);
         if (orcsApi.getOrcsTypes().getAttributeTypes().isEnumerated(attrType)) {
            width = 40;
         } else if (isBoolean) {
            width = 50;
         } else if (orcsApi.getOrcsTypes().getAttributeTypes().isIntegerType(attrType)) {
            width = 45;
            sortDataType = SortDataType.Integer;
            columnAlign = ColumnAlign.Center;
         } else if (orcsApi.getOrcsTypes().getAttributeTypes().isFloatingType(attrType)) {
            width = 40;
            sortDataType = SortDataType.Float;
            columnAlign = ColumnAlign.Center;
         } else if (orcsApi.getOrcsTypes().getAttributeTypes().isDateType(attrType)) {
            width = 80;
            sortDataType = SortDataType.Date;
         }

         AtsAttributeValueColumn valueColumn = new AtsAttributeValueColumn();
         valueColumn.setAttributeType(attrType);
         valueColumn.setWidth(width);
         valueColumn.setAlign(columnAlign);
         valueColumn.setVisible(true);
         valueColumn.setSortDataType(sortDataType.name());
         valueColumn.setColumnMultiEdit(true);
         valueColumn.setDescription(attrType.getDescription());
         valueColumn.setNamespace("org.eclipse.osee.ats.WorldXViewer");
         String name = attrType.getName().replaceAll("^.*\\.", "");
         valueColumn.setName(name);
         valueColumn.setId(generateId(attrType.getName(), name));
         if (isBoolean) {
            valueColumn.setBooleanNotSetShow("");
            valueColumn.setBooleanOnFalseShow("false");
            valueColumn.setBooleanOnTrueShow("true");
         }
         columns.add(valueColumn);
      }
      return columns;
   }

   private String generateId(String id, String name) {
      System.err.println(String.format("\nid [%s] name [%s]", id, name));
      String cleanName = name.replaceAll("-", " ");
      cleanName = cleanName.replaceAll("\\(", " ");
      cleanName = cleanName.replaceAll("\\)", " ");
      cleanName = cleanName.replaceAll("/", " ");
      cleanName = cleanName.replaceAll(" +", " ");
      cleanName = cleanName.replaceAll("^ ", "");
      System.err.println(String.format("cleanName [%s]", cleanName));
      String replaceValue = Lib.toCamelCaseFromStringsWithSpaces(cleanName);
      System.err.println(String.format("replaceValue [%s]", replaceValue));
      String result = id.replaceFirst(Lib.escapeForRegex(name), replaceValue);
      System.err.println(String.format("result [%s]", result));
      return result;
   }

   @Override
   public XResultData alive() {
      XResultData results = new XResultData();
      results.logf("Alive");
      return results;
   }

   @Override
   public XResultData demoDbInit() {
      XResultData rd = new XResultData();
      try {
         DemoDatabaseConfig config = new DemoDatabaseConfig(atsApi);
         config.run();
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }
      return rd;
   }

   @Override
   public XResultData atsDbInit() {
      XResultData rd = new XResultData();
      try {
         rd = atsApi.getConfigService().configAtsDatabase(atsApi);
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }
      return rd;
   }
}
