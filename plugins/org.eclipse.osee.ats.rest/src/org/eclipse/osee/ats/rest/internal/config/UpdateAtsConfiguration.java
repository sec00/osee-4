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
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.IAtsConfigurationViewsProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.ats.core.column.ColorTeamColumn;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class UpdateAtsConfiguration {
   private static final String VIEWS_KEY = "views";
   private static final String VIEWS_EQUAL_KEY = VIEWS_KEY + "=";
   private static final String COLOR_COLUMN_KEY = "colorColumns";
   public static final String VALID_STATE_NAMES_KEY = "validStateNames";

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public UpdateAtsConfiguration(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData createUpdateConfig(XResultData rd) {
      UserId user = SystemUser.OseeSystem;
      ArtifactReadable atsConfigArt = (ArtifactReadable) getOrCreateAtsConfig(user, rd);
      createRuleDefinitions(user, rd);
      createUpdateColorColumnAttributes();
      createUpdateConfigAttributes(atsConfigArt, user, rd);
      try {
         createUpdateValidStateAttributes();
      } catch (Exception ex) {
         rd.errorf("Error in createUpdateValidStateAttributes [%s]", Lib.exceptionToString(ex));
      }
      return rd;
   }

   private void createRuleDefinitions(UserId userId, XResultData rd) {
      try {
         if ((ArtifactReadable) atsApi.getQueryService().getArtifact(AtsArtifactToken.RuleDefinitions) == null) {
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userId,
               "Add Rule Definitions Artifact");
            ArtifactId headingArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.HeadingFolder);
            ArtifactId ruleDefConfigArt = tx.createArtifact(AtsArtifactToken.RuleDefinitions);
            String ruleDefs = OseeInf.getResourceContents("atsConfig/ruleDefinitions.ats", getClass());
            tx.createAttribute(ruleDefConfigArt, AtsAttributeTypes.DslSheet, ruleDefs);
            if (rd.isErrors()) {
               throw new OseeStateException(rd.toString());
            }
            tx.relate(headingArt, CoreRelationTypes.Default_Hierarchical__Child, ruleDefConfigArt);
            tx.commit();
         }
      } catch (Exception ex) {
         OseeLog.log(UpdateAtsConfiguration.class, Level.SEVERE, ex);
         rd.error("Error loading column ruleDefinitions.ats file (see log for details) " + ex.getLocalizedMessage());
      }
   }

   private List<String> getViewsJsonStrings() throws Exception {
      List<String> viewsJson = new LinkedList<>();
      viewsJson.add(OseeInf.getResourceContents("atsConfig/views.json", getClass()));
      for (IAtsConfigurationViewsProvider provider : AtsConfigurationViewsService.getViewsProviders()) {
         viewsJson.add(provider.getViewsJson());
      }
      return viewsJson;
   }

   private void createUpdateConfigAttributes(ArtifactReadable configArt, UserId userId, XResultData rd) {
      try {
         AtsViews databaseViews = getConfigViews();
         for (String viewsJson : getViewsJsonStrings()) {
            AtsViews atsViews = JsonUtil.readValue(viewsJson, AtsViews.class);
            // merge any new default view items to current database view items
            List<AtsAttributeValueColumn> toAdd = new LinkedList<>();
            for (AtsAttributeValueColumn defaultView : atsViews.getAttrColumns()) {
               boolean found = false;
               for (AtsAttributeValueColumn dbView : databaseViews.getAttrColumns()) {
                  boolean defaultViewNameValid =
                     Strings.isValid(dbView.getName()) && Strings.isValid(defaultView.getName());
                  if (defaultViewNameValid && dbView.getName().equals(defaultView.getName())) {
                     found = true;
                     break;
                  }
                  if (!found && dbView.getAttributeType().equals(defaultView.getAttributeType())) {
                     found = true;
                     break;
                  }
               }
               if (!found) {
                  toAdd.add(defaultView);
               }
            }
            databaseViews.getAttrColumns().addAll(toAdd);
         }

         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userId,
            "Create Update Config Attributes");
         Iterator<? extends AttributeReadable<String>> iterator =
            configArt.getAttributes(CoreAttributeTypes.GeneralStringData, DeletionFlag.EXCLUDE_DELETED).iterator();
         boolean found = false;
         while (iterator.hasNext()) {
            AttributeReadable<String> attributeReadable = iterator.next();

            if (attributeReadable != null && attributeReadable.getValue().startsWith(VIEWS_EQUAL_KEY)) {
               tx.setAttributeById(configArt, attributeReadable, getViewsAttrValue(databaseViews));
               rd.log("Create or update AtsConfig.VIEWS attribute\n");
               found = true;
               break;
            }
         }
         if (!found) {
            tx.createAttribute(configArt, CoreAttributeTypes.GeneralStringData, getViewsAttrValue(databaseViews));
            rd.log("Creating VIEWS attribute\n");
         }
         tx.commit();
      } catch (Exception ex) {
         OseeLog.log(UpdateAtsConfiguration.class, Level.SEVERE, ex);
         rd.error("Error loading column views.json file (see log for details) " + ex.getLocalizedMessage());
      }
   }

   private String getViewsAttrValue(AtsViews defaultViews) {
      return VIEWS_EQUAL_KEY + JsonUtil.toJson(defaultViews);
   }

   public ArtifactId getOrCreateAtsConfig(UserId userId, XResultData rd) {
      ArtifactToken atsConfigArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsConfig);
      if (atsConfigArt == null) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userId, "Create AtsConfig");
         ArtifactToken headingArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.HeadingFolder);
         atsConfigArt = tx.createArtifact(AtsArtifactToken.AtsConfig);
         tx.relate(headingArt, CoreRelationTypes.Default_Hierarchical__Parent, atsConfigArt);
         tx.commit();
         rd.log("Created AtsConfig");
      }
      return atsConfigArt;
   }

   private void createUpdateColorColumnAttributes() {
      ColorColumns columns = new ColorColumns();
      columns.addColumn(ColorTeamColumn.getColor());
      String colorColumnsJson = JsonUtil.toJson(columns);
      atsApi.setConfigValue(COLOR_COLUMN_KEY, colorColumnsJson);
   }

   private void createUpdateValidStateAttributes() throws Exception {

      Collection<String> validStateNames = atsApi.getWorkDefinitionService().getAllValidStateNames(new XResultData());
      atsApi.setConfigValue(VALID_STATE_NAMES_KEY, Collections.toString(",", validStateNames));
   }

   public ArtifactId getOrCreateConfigsFolder(UserId userId, XResultData rd) {
      ArtifactId configsFolderArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.ConfigsFolder);
      if (configsFolderArt == null) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userId, "Create Configs Folder");
         configsFolderArt = tx.createArtifact(AtsArtifactToken.ConfigsFolder);
         ArtifactId headingArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.HeadingFolder);
         tx.relate(configsFolderArt, CoreRelationTypes.Default_Hierarchical__Parent, headingArt);
         tx.commit();
         rd.log("Created Configs Folder");
      }
      return configsFolderArt;
   }

   public Collection<String> getValidStateNames() {
      String stateNamesStr = atsApi.getConfigValue(VALID_STATE_NAMES_KEY);
      List<String> stateNames = new LinkedList<>();
      if (Strings.isValid(stateNamesStr)) {
         for (String stateName : stateNamesStr.split(",")) {
            stateNames.add(stateName);
         }
      }
      return stateNames;
   }

   public AtsViews getConfigViews() {
      String viewsStr = atsApi.getConfigValue(VIEWS_KEY);
      AtsViews views = null;
      if (Strings.isValid(viewsStr)) {
         views = JsonUtil.readValue(viewsStr, AtsViews.class);
      } else {
         views = new AtsViews();
      }
      return views;
   }

   public ColorColumns getColorColumns() {
      String colorStr = atsApi.getConfigValue(COLOR_COLUMN_KEY);
      ColorColumns columns = null;
      if (Strings.isValid(colorStr)) {
         columns = JsonUtil.readValue(colorStr, ColorColumns.class);
      } else {
         columns = new ColorColumns();
      }
      return columns;
   }

}
