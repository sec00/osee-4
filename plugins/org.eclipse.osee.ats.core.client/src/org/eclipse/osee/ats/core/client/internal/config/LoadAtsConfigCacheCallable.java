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
package org.eclipse.osee.ats.core.client.internal.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.IAtsUserAdmin;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactStore;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.dsl.BooleanDefUtil;
import org.eclipse.osee.ats.dsl.ModelUtil;
import org.eclipse.osee.ats.dsl.UserRefUtil;
import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.VersionDef;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class LoadAtsConfigCacheCallable implements Callable<AtsArtifactConfigCache> {

   private final IAtsArtifactStore artifactStore;
   private final Map<String, IAtsTeamDefinition> newTeams = new HashMap<String, IAtsTeamDefinition>();
   private final Map<String, IAtsVersion> newVersions = new HashMap<String, IAtsVersion>();
   private final Map<String, IAtsActionableItem> newAIs = new HashMap<String, IAtsActionableItem>();

   public LoadAtsConfigCacheCallable(IAtsArtifactStore artifactStore) {
      this.artifactStore = artifactStore;
   }

   @Override
   public AtsArtifactConfigCache call() throws Exception {
      AtsArtifactConfigCache cache = new AtsArtifactConfigCache();

      IOseeBranch atsBranch = AtsUtilCore.getAtsBranch();
      if (AtsUtilCore.isArtifactConfig()) {
         List<IArtifactType> typesToLoad = getTypesToLoad();
         List<Artifact> artifactListFromType =
            ArtifactQuery.getArtifactListFromType(typesToLoad, atsBranch, DeletionFlag.EXCLUDE_DELETED);

         for (Artifact artifact : artifactListFromType) {
            loadAtsConfigCacheArtifacts(artifactStore, cache, artifact);
         }
      } else {
         Artifact artifact = ArtifactQuery.getArtifactFromToken(AtsArtifactToken.AtsBranchConfig, atsBranch);
         loadAtsConfigCacheFromDsl(cache, artifact);
      }
      return cache;
   }

   /**
    * Load ATS config from single artifact containing ATS DSL for Versions, AIs and Team Defs
    */
   private void loadAtsConfigCacheFromDsl(AtsArtifactConfigCache cache, Artifact artifact) throws Exception {
      String dslStr = artifact.getSoleAttributeValue(AtsAttributeTypes.DslSheet, "");
      AtsDsl atsDsl = ModelUtil.loadModel("AtsConfig.ats", dslStr);
      loadUsers(atsDsl.getUserDef(), cache);
      loadTeamDefinitions(atsDsl.getTeamDef(), cache, null);
      loadActionableItems(atsDsl.getActionableItemDef(), cache, null);
   }

   private void loadActionableItems(EList<ActionableItemDef> aiDefs, AtsArtifactConfigCache cache, IAtsActionableItem parent) {
      for (ActionableItemDef dslAIDef : aiDefs) {
         String dslAIName = Strings.unquote(dslAIDef.getName());
         String guid = dslAIDef.getGuid();
         if (guid == null || !GUID.isValid(guid)) {
            throw new OseeArgumentException("Invalid guid [%s] specified for DSL Actionable Item Definition [%s]",
               guid, dslAIDef);
         }
         // System.out.println("   - Importing Actionable Item " + dslAIName);
         ActionableItem newAi = (ActionableItem) cache.getSoleByGuid(dslAIDef.getGuid(), IAtsActionableItem.class);
         if (newAi == null) {
            newAi = new ActionableItem(dslAIName, dslAIDef.getGuid());
            cache.cache(newAi);
         }
         if (parent != null && !parent.equals(newAi)) {
            parent.getChildrenActionableItems().add(newAi);
         }
         newAIs.put(newAi.getName(), newAi);
         newAi.setActive(BooleanDefUtil.get(dslAIDef.getActive(), true));
         newAi.setActionable(BooleanDefUtil.get(dslAIDef.getActionable(), true));
         for (String staticId : dslAIDef.getStaticId()) {
            newAi.getStaticIds().add(staticId);
            cache.cacheByTag(staticId, newAi);
         }
         newAi.getLeads().addAll(getUsers(dslAIDef.getLead()));
         // TODO Not supported in DSL yet
         // newAi.getOwnsers().addAll(getUsers(dslAIDef.getOwner()));
         if (dslAIDef.getTeamDef() != null) {
            if (dslAIDef.getTeamDef() == null) {
               throw new OseeStateException(String.format("No Team Definition defined for Actionable Item [%s]",
                  dslAIName));
            }
            newAi.setTeamDefinition(newTeams.get(dslAIDef.getTeamDef()));
         }
         loadAccessContextIds(newAi, dslAIDef.getAccessContextId());
         loadActionableItems(dslAIDef.getChildren(), cache, newAi);
      }
   }

   private void loadUsers(EList<UserDef> userDefs, AtsArtifactConfigCache cache) {
      for (UserDef dslUser : userDefs) {
         IAtsUserAdmin userAdmin = AtsClientService.get().getUserAdmin();
         IAtsUser user = userAdmin.getUserById(dslUser.getUserId());
         if (user == null) {
            String userId = dslUser.getUserId();
            if (!Strings.isValid(userId)) {
               throw new OseeArgumentException("Invalid userId [%s] specified for DSL User [%s]", userId, dslUser);
            }
            user =
               userAdmin.createUser(userId, dslUser.getName(), dslUser.getEmail(),
                  BooleanDefUtil.get(dslUser.getActive(), true), BooleanDefUtil.get(dslUser.getAdmin(), false));
            userAdmin.cache(user);
         }
      }
   }

   private void loadTeamDefinitions(EList<TeamDef> teamDefs, AtsArtifactConfigCache cache, IAtsTeamDefinition parentTeam) {
      for (TeamDef dslTeamDef : teamDefs) {
         String dslTeamName = Strings.unquote(dslTeamDef.getName());
         //         System.out.println("   - Importing Team " + dslTeamName);
         TeamDefinition newTeam = null;
         String guid = dslTeamDef.getGuid();
         if (guid == null || !GUID.isValid(guid)) {
            throw new OseeArgumentException("Invalid guid [%s] specified for DSL Team Definition [%s]", guid,
               dslTeamDef);
         }
         if (dslTeamDef.getTeamDefOption().contains("GetOrCreate")) {
            newTeam = (TeamDefinition) cache.getSoleByGuid(dslTeamDef.getGuid(), IAtsTeamDefinition.class);
         }
         if (newTeam == null) {
            newTeam = new TeamDefinition(dslTeamName, dslTeamDef.getGuid());
            cache.cache(newTeam);
         }

         if (parentTeam != null && !parentTeam.equals(newTeam)) {
            parentTeam.getChildrenTeamDefinitions().add(newTeam);
         }
         newTeams.put(newTeam.getName(), newTeam);

         newTeam.setActive(BooleanDefUtil.get(dslTeamDef.getActive(), true));
         //         newTeam.setSoleAttributeValue(CoreAttributeTypes.Active, BooleanDefUtil.get(dslTeamDef.getActive(), true));
         for (String staticId : dslTeamDef.getStaticId()) {
            newTeam.getStaticIds().add(staticId);
            cache.cacheByTag(staticId, newTeam);
         }
         newTeam.getLeads().addAll(getUsers(dslTeamDef.getLead()));
         newTeam.getMembers().addAll(getUsers(dslTeamDef.getMember()));
         newTeam.getPrivilegedMembers().addAll(getUsers(dslTeamDef.getPrivileged()));
         if (Strings.isValid(dslTeamDef.getWorkDefinition())) {
            newTeam.setWorkflowDefinition(dslTeamDef.getWorkDefinition());
         }
         if (Strings.isValid(dslTeamDef.getRelatedTaskWorkDefinition())) {
            newTeam.setRelatedTaskWorkDefinition(dslTeamDef.getRelatedTaskWorkDefinition());
         }
         loadAccessContextIds(newTeam, dslTeamDef.getAccessContextId());
         loadVersionDefinitions(dslTeamDef.getVersion(), cache, newTeam);
         // process children
         loadTeamDefinitions(dslTeamDef.getChildren(), cache, newTeam);
      }
   }

   private void loadVersionDefinitions(EList<VersionDef> versionDefs, AtsArtifactConfigCache cache, TeamDefinition teamDef) throws OseeCoreException {

      Map<String, Version> nameToVerArt = new HashMap<String, Version>();
      for (VersionDef dslVersionDef : versionDefs) {
         String dslVerName = Strings.unquote(dslVersionDef.getName());
         // System.out.println("   - Importing Version " + dslVerName);

         String guid = dslVersionDef.getGuid();
         if (guid == null || !GUID.isValid(guid)) {
            throw new OseeArgumentException("Invalid guid [%s] specified for DSL Version Definition [%s]", guid,
               dslVersionDef);
         }
         Version newVer = (Version) cache.getSoleByGuid(guid, IAtsVersion.class);
         if (newVer == null) {
            newVer = new Version(AtsClientService.get().getAtsVersionService(), dslVerName, dslVersionDef.getGuid());
            cache.cache(newVer);
         }

         teamDef.getVersions().add(newVer);
         nameToVerArt.put(newVer.getName(), newVer);
         newVersions.put(newVer.getName(), newVer);
         newVer.setAllowCommitBranch(BooleanDefUtil.get(dslVersionDef.getAllowCommitBranch(), true));
         newVer.setAllowCreateBranch(BooleanDefUtil.get(dslVersionDef.getAllowCreateBranch(), true));
         newVer.setNextVersion(BooleanDefUtil.get(dslVersionDef.getNext(), false));
         newVer.setReleased(BooleanDefUtil.get(dslVersionDef.getReleased(), false));
         if (Strings.isValid(dslVersionDef.getBaselineBranchUuid())) {
            newVer.setBaselineBranchUuid(dslVersionDef.getBaselineBranchUuid());
         }
         for (String staticId : dslVersionDef.getStaticId()) {
            newVer.getStaticIds().add(staticId);
            cache.cacheByTag(staticId, newVer);
         }
      }
      // Handle parallel versions
      for (VersionDef dslVersionDef : versionDefs) {
         String aiName = Strings.unquote(dslVersionDef.getName());
         Version verArt = nameToVerArt.get(aiName);
         for (String parallelVerStr : dslVersionDef.getParallelVersion()) {
            // System.out.println(String.format("   - Importing Parallel Version [%s] -> Child [%s]", aiName, parallelVerStr));
            Version childVer = nameToVerArt.get(parallelVerStr);
            verArt.getParallelVersions().add(childVer);
         }
      }
   }

   private void loadAccessContextIds(IAtsConfigObject configObj, EList<String> contextIds) throws OseeCoreException {
      for (String accessContextId : contextIds) {
         // TBD: Not supported by AtsDsl
         // configObj.getAccessContextIds.add(accessContextId);
      }
   }

   private Set<IAtsUser> getUsers(EList<UserRef> userRefs) throws OseeCoreException {
      Set<IAtsUser> users = new HashSet<IAtsUser>();
      if (userRefs != null) {
         IAtsUserAdmin userAdmin = AtsClientService.get().getUserAdmin();
         for (String userId : UserRefUtil.getUserIds(userRefs)) {
            IAtsUser user = userAdmin.getUserById(userId);
            users.add(user);
         }
         for (String userName : UserRefUtil.getUserNames(userRefs)) {
            IAtsUser user = userAdmin.getUserByName(Strings.unquote(userName));
            users.add(user);
         }
      }
      return users;
   }

   private List<IArtifactType> getTypesToLoad() {
      return Arrays.asList(AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.ActionableItem, AtsArtifactTypes.Version);
   }

   private void loadAtsConfigCacheArtifacts(IAtsArtifactStore artifactStore, AtsArtifactConfigCache cache, Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.TeamDefinition)) {
         IAtsTeamDefinition teamDef = artifactStore.load(cache, artifact);

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, teamDef);
         }
      }
      if (artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
         IAtsActionableItem ai = artifactStore.load(cache, artifact);

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, ai);
         }
      }
      if (artifact.isOfType(AtsArtifactTypes.Version)) {
         IAtsVersion version = artifactStore.load(cache, artifact);

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            cache.cacheByTag(staticId, version);
         }
      }
   }
}