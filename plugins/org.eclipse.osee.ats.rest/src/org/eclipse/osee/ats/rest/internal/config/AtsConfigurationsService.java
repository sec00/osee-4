/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Configuration;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Active;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.AtsConfiguredBranch;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Default;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamActionableItem_ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamDefinitionToVersion_Version;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Users_User;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfiguration;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.JaxActionableItem;
import org.eclipse.osee.ats.api.config.JaxTeamDefinition;
import org.eclipse.osee.ats.api.config.JaxVersion;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.config.AbstractAtsConfigurationService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Loads the configurations from the database and provides to both server and clients through endpoint.
 *
 * @author Donald G Dunne
 */
public class AtsConfigurationsService extends AbstractAtsConfigurationService {

   private final OrcsApi orcsApi;

   public AtsConfigurationsService(AtsApi atsApi, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
   }

   /**
    * Not synchronized to improve performance after cache is initially loaded. Depends on synchronization of load() and
    * its repeated check of atsConfigurations == null
    */
   @Override
   public AtsConfigurations getConfigurations() {
      if (atsConfigurations == null) {
         load(false);
      }
      return atsConfigurations;
   }

   @Override
   public AtsConfigurations getConfigurationsWithPend() {
      return load(true);
   }

   private synchronized AtsConfigurations load(boolean reload) {
      // fast design of get() depends on re-checking atsConfigurations == null here
      if (reload || atsConfigurations == null) {
         if (orcsApi.getAdminOps().isDataStoreInitialized()) {
            atsConfigurations = getAtsConfigurationsFromDb();
         } else {
            // just return an empty one if database is being initialized so don't get NPE
            atsConfigurations = new AtsConfigurations();
         }
      }
      return atsConfigurations;
   }

   private AtsConfigurations getAtsConfigurationsFromDb() {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);

      ElapsedTime time = new ElapsedTime("getAtsConfigurationsFromDb");

      List<ArtifactId> teamDefIds = query.andTypeEquals(TeamDefinition).asArtifactIds();
      List<ArtifactId> aiIds = query.andTypeEquals(ActionableItem).asArtifactIds();

      // load ats branch configurations
      AtsConfigurations configs = new AtsConfigurations();
      for (ArtifactReadable art : query.andTypeEquals(Configuration).asArtifacts()) {
         AtsConfiguration config = new AtsConfiguration();
         config.setName(art.getName());
         config.setArtifactId(art);
         config.setBranchId(BranchId.valueOf(art.getSoleAttributeValue(AtsConfiguredBranch, "0")));
         config.setIsDefault(art.getSoleAttributeValue(Default, false));
         configs.getConfigs().add(config);
      }
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsApi, orcsApi);
      AtsViews views = update.getConfigViews();
      // load views
      configs.setViews(views);
      // load color column config
      configs.setColorColumns(update.getColorColumns());
      // load valid state names
      configs.setValidStateNames(update.getValidStateNames());
      // load users
      for (IAtsUser user : atsApi.getUserService().getUsersFromDb()) {
         configs.getUsers().add((AtsUser) user);
      }

      // load admins
      configs.getAtsAdmins().addAll(query.andId(AtsUserGroups.AtsAdmin).asArtifact().getRelated(Users_User).getList());

      // load ats config objects
      Map<ArtifactId, ArtifactReadable> idToConfigArtifact =
         query.andTypeEquals(Arrays.asList(TeamDefinition, Version, ActionableItem)).asArtifactMap();

      for (ArtifactReadable configArt : idToConfigArtifact.values()) {
         if (configArt.isTypeEqual(TeamDefinition)) {
            JaxTeamDefinition teamDef = createJaxTeamDefinition(configArt);
            configs.addTeamDef(teamDef);
         } else if (configArt.isTypeEqual(ActionableItem)) {
            JaxActionableItem ai = createJaxActionableItem(configArt);
            configs.addAi(ai);
         } else if (configArt.isTypeEqual(Version)) {
            JaxVersion version = createJaxVersion(configArt);
            configs.addVersion(version);
         }
      }

      query.andTypeEquals(TeamDefinition).andRelatedRecursive(CoreRelationTypes.Default_Hierarchical__Child,
         AtsArtifactToken.TopTeamDefinition);

      // load team def tree

      addTeamDefinitionChildrenWIthRecurse(AtsArtifactToken.TopTeamDefinition, idToConfigArtifact, configs, teamDefIds);
      configs.setTopTeamDefinition(AtsArtifactToken.TopTeamDefinition);

      // load actionable items tree
      addActionableItemChildrenWIthRecurse(AtsArtifactToken.TopActionableItem, idToConfigArtifact, configs, aiIds);
      configs.setTopActionableItem(AtsArtifactToken.TopActionableItem);

      time.end(Units.SEC);
      return configs;
   }

   private JaxTeamDefinition addTeamDefinitionChildrenWIthRecurse(ArtifactId teamDefId, Map<ArtifactId, ArtifactReadable> idToArtifact, AtsConfigurations configs, List<ArtifactId> teamDefIds) {
      ArtifactReadable teamDef = idToArtifact.get(teamDefId);
      if (teamDef != null && teamDef.isOfType(TeamDefinition)) {
         JaxTeamDefinition jaxTeamDef = configs.getIdToTeamDef().get(teamDefId.getId());
         for (ArtifactId childId : teamDef.getChildren()) {
            if (teamDefIds.contains(childId)) {
               JaxTeamDefinition child =
                  addTeamDefinitionChildrenWIthRecurse(childId, idToArtifact, configs, teamDefIds);
               if (child != null) {
                  child.setParentId(teamDefId.getId());
                  jaxTeamDef.addChild(child);
               }
            }
         }
         // add team to version ids
         for (Long versionId : atsApi.getRelationResolver().getRelatedIds(teamDef, TeamDefinitionToVersion_Version)) {
            jaxTeamDef.addVersion(versionId);
            JaxVersion version = configs.getIdToVersion().get(versionId);
            version.setTeamDefId(teamDefId.getId());
         }
         // add team to ai ids
         for (Long aiId : atsApi.getRelationResolver().getRelatedIds(teamDef, TeamActionableItem_ActionableItem)) {
            JaxActionableItem jai = configs.getIdToAi().get(aiId);
            if (jai != null) {
               jaxTeamDef.addAi(aiId);
               jai.setTeamDefId(teamDefId.getId());
            }
         }
         return jaxTeamDef;
      }
      return null;
   }

   private JaxTeamDefinition createJaxTeamDefinition(ArtifactReadable teamDefArt) {
      JaxTeamDefinition jaxTeamDef = new JaxTeamDefinition();
      jaxTeamDef.setName(teamDefArt.getName());
      jaxTeamDef.setId(teamDefArt.getId());
      jaxTeamDef.setGuid(teamDefArt.getGuid());
      jaxTeamDef.setActive(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Active, true));
      jaxTeamDef.setWorkType(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.WorkType, ""));
      for (ArtifactToken ai : atsApi.getRelationResolver().getRelated(teamDefArt, TeamActionableItem_ActionableItem)) {
         jaxTeamDef.getAis().add(ai.getId());
      }
      return jaxTeamDef;
   }

   private JaxVersion createJaxVersion(ArtifactReadable verArt) {
      JaxVersion jaxVersion = new JaxVersion();
      jaxVersion.setName(verArt.getName());
      jaxVersion.setId(verArt.getId());
      jaxVersion.setGuid(verArt.getGuid());
      jaxVersion.setActive(verArt.getSoleAttributeValue(Active, true));
      return jaxVersion;
   }

   private JaxActionableItem createJaxActionableItem(ArtifactReadable aiArt) {
      JaxActionableItem jaxAi = new JaxActionableItem();
      jaxAi.setName(aiArt.getName());
      jaxAi.setId(aiArt.getId());
      jaxAi.setGuid(aiArt.getGuid());
      jaxAi.setDescription(aiArt.getSoleAttributeValue(Description, ""));
      jaxAi.setActive(aiArt.getSoleAttributeValue(Active, true));
      return jaxAi;
   }

   private JaxActionableItem addActionableItemChildrenWIthRecurse(ArtifactId aiId, Map<ArtifactId, ArtifactReadable> idToArtifact, AtsConfigurations configs, List<ArtifactId> aiIds) {
      ArtifactReadable aiArt = idToArtifact.get(aiId);
      if (aiArt != null && aiArt.isOfType(ActionableItem)) {
         JaxActionableItem jaxAi = configs.getIdToAi().get(aiId.getId());
         for (ArtifactId childId : aiArt.getChildren()) {
            if (aiIds.contains(childId)) {
               JaxActionableItem child = addActionableItemChildrenWIthRecurse(childId, idToArtifact, configs, aiIds);
               if (child != null) {
                  child.setParentId(aiId.getId());
                  jaxAi.addChild(child);
               }
            }
         }
         return jaxAi;
      }
      return null;
   }

   @Override
   public XResultData configAtsDatabase(AtsApi atsApi) {
      if (isAtsBaseCreated()) {
         XResultData results = new XResultData();
         results.error("ATS base config has already been completed");
         return results;
      }
      AtsDatabaseConfig config = new AtsDatabaseConfig(atsApi, orcsApi);
      return config.run();
   }
}