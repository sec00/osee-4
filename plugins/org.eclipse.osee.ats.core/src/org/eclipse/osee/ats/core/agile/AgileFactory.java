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
package org.eclipse.osee.ats.core.agile;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class AgileFactory {

   private AgileFactory() {
      // Utilitiy class
   }

   public static IAgileTeam createAgileTeam(Log logger, AtsApi atsApi, JaxNewAgileTeam newTeam) {
      org.eclipse.osee.framework.core.data.ArtifactId userArt =
         atsApi.getArtifact(atsApi.getUserService().getCurrentUser());

      ArtifactId agileTeamArt = atsApi.getArtifact(newTeam.getUuid());
      if (agileTeamArt == null) {

         IAtsChangeSet changes = atsApi.createChangeSet("Create new Agile Team");

         agileTeamArt =
            changes.createArtifact(AtsArtifactTypes.AgileTeam, newTeam.getName(), GUID.create(), newTeam.getUuid());
         changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Active, true);
         ArtifactId topAgileFolder = AgileFolders.getOrCreateTopAgileFolder(atsApi, userArt, changes);
         if (topAgileFolder.notEqual(atsApi.getRelationResolver().getParent(agileTeamArt))) {
            changes.unrelateFromAll(CoreRelationTypes.Default_Hierarchical__Parent, agileTeamArt);
            changes.addChild(topAgileFolder, agileTeamArt);
         }

         Set<ArtifactId> atsTeamArts = new HashSet<>();
         changes.setRelations(agileTeamArt, AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam, atsTeamArts);

         changes.execute();
      }
      return getAgileTeam(logger, atsApi, agileTeamArt);
   }

   public static IAgileTeam updateAgileTeam(Log logger, AtsApi atsApi, JaxAgileTeam team) {
      ArtifactId userArt = atsApi.getArtifact(atsApi.getUserService().getCurrentUser());

      IAtsChangeSet changes = atsApi.createChangeSet("Update new Agile Team");

      ArtifactToken agileTeamArt = atsApi.getArtifact(team.getUuid());
      if (agileTeamArt == null) {
         throw new OseeStateException("Agile Team not found with Uuid [%d]", team.getUuid());
      }
      if (Strings.isValid(team.getName()) && !team.getName().equals(agileTeamArt.getName())) {
         changes.setName(agileTeamArt, team.getName());
      }
      if (Strings.isValid(team.getDescription()) && !team.getDescription().equals(
         atsApi.getAttributeResolver().getSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Description, ""))) {
         changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Description, team.getDescription());
      }
      changes.setSoleAttributeValue(agileTeamArt, AtsAttributeTypes.Active, team.isActive());
      ArtifactId topAgileFolder = AgileFolders.getOrCreateTopAgileFolder(atsApi, userArt, changes);
      if (topAgileFolder.notEqual(atsApi.getRelationResolver().getParent(agileTeamArt))) {
         changes.unrelateFromAll(CoreRelationTypes.Default_Hierarchical__Parent, agileTeamArt);
         changes.addChild(topAgileFolder, agileTeamArt);
      }

      Set<ArtifactId> atsTeamArts = new HashSet<>();
      for (long atsTeamUuid : team.getAtsTeamUuids()) {
         ArtifactId atsTeamArt = atsApi.getArtifact(atsTeamUuid);
         if (atsTeamArt != null && atsApi.getStoreService().isOfType(atsTeamArt, AtsArtifactTypes.TeamDefinition)) {
            atsTeamArts.add(atsTeamArt);
         } else {
            throw new OseeArgumentException("UUID %d is not a valid Ats Team Definition", atsTeamUuid);
         }
      }
      changes.setRelations(agileTeamArt, AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam, atsTeamArts);

      changes.execute();
      return getAgileTeam(logger, atsApi, agileTeamArt);
   }

   public static IAgileTeam getAgileTeam(Log logger, AtsApi atsApi, Object artifact) {
      IAgileTeam team = null;
      if (artifact instanceof ArtifactId) {
         ArtifactToken art = atsApi.getArtifact((ArtifactId) artifact);
         team = new AgileTeam(logger, atsApi, art);
      }
      return team;
   }

   public static IAgileFeatureGroup createAgileFeatureGroup(Log logger, AtsApi atsApi, long teamUuid, String name, String guid, Long uuid) {
      JaxAgileFeatureGroup feature = new JaxAgileFeatureGroup();
      feature.setName(name);
      feature.setUuid(uuid);
      feature.setTeamUuid(teamUuid);
      feature.setActive(true);
      return createAgileFeatureGroup(logger, atsApi, feature);
   }

   public static IAgileFeatureGroup createAgileFeatureGroup(Log logger, AtsApi atsApi, JaxAgileFeatureGroup newFeatureGroup) {
      ArtifactId userArt = atsApi.getArtifact(atsApi.getUserService().getCurrentUser());

      IAtsChangeSet changes = atsApi.createChangeSet("Create new Agile Feature Group");

      ArtifactId featureGroupArt = changes.createArtifact(AtsArtifactTypes.AgileFeatureGroup, newFeatureGroup.getName(),
         GUID.create(), newFeatureGroup.getUuid());
      changes.setSoleAttributeValue(featureGroupArt, AtsAttributeTypes.Active, newFeatureGroup.isActive());

      ArtifactId featureGroupFolder =
         AgileFolders.getOrCreateTopFeatureGroupFolder(atsApi, newFeatureGroup.getTeamUuid(), userArt, changes);
      changes.addChild(featureGroupFolder, featureGroupArt);

      ArtifactId team = AgileFolders.getTeamFolder(atsApi, newFeatureGroup.getTeamUuid());
      changes.relate(team, AtsRelationTypes.AgileTeamToFeatureGroup_FeatureGroup, featureGroupArt);

      changes.execute();
      return getAgileFeatureGroup(logger, atsApi, featureGroupArt);
   }

   public static IAgileFeatureGroup getAgileFeatureGroup(Log logger, AtsApi atsApi, ArtifactId artifact) {
      return new AgileFeatureGroup(logger, atsApi, atsApi.getArtifact(artifact));
   }

   public static IAgileSprint createAgileSprint(Log logger, AtsApi atsApi, long teamUuid, String name, String guid, Long uuid) {

      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create new Agile Sprint", AtsCoreUsers.SYSTEM_USER);

      ArtifactToken sprintArt = changes.createArtifact(AtsArtifactTypes.AgileSprint, name, guid, uuid);
      IAgileSprint sprint = atsApi.getWorkItemFactory().getAgileSprint(sprintArt);

      atsApi.getActionFactory().setAtsId(sprint, TeamDefinitions.getTopTeamDefinition(atsApi.getQueryService()),
         changes);

      // Initialize state machine
      atsApi.getActionFactory().initializeNewStateMachine(sprint, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER),
         new Date(), atsApi.getUserService().getCurrentUser(), changes);

      changes.add(sprintArt);

      ArtifactId teamFolder = AgileFolders.getTeamFolder(atsApi, teamUuid);
      ArtifactId agileSprintFolderArt = AgileFolders.getOrCreateTopSprintFolder(atsApi, teamUuid, changes);
      changes.relate(agileSprintFolderArt, CoreRelationTypes.Default_Hierarchical__Child, sprintArt);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToSprint_Sprint, sprintArt);

      changes.execute();
      return getAgileSprint(logger, atsApi, sprintArt);
   }

   public static IAgileSprint getAgileSprint(Log logger, AtsApi atsApi, ArtifactId artifact) {
      ArtifactToken artifact2 = atsApi.getArtifact(artifact);
      return new AgileSprint(logger, atsApi, artifact2);
   }

   public static IAgileBacklog createAgileBacklog(Log logger, AtsApi atsApi, long teamUuid, String name, String guid, Long uuid) {

      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create new Agile Backlog", AtsCoreUsers.SYSTEM_USER);

      ArtifactToken backlogArt = changes.createArtifact(AtsArtifactTypes.AgileBacklog, name, guid, uuid);
      IAgileBacklog sprint = atsApi.getWorkItemFactory().getAgileBacklog(backlogArt);

      atsApi.getActionFactory().setAtsId(sprint, TeamDefinitions.getTopTeamDefinition(atsApi.getQueryService()),
         changes);

      // Initialize state machine
      atsApi.getActionFactory().initializeNewStateMachine(sprint, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER),
         new Date(), atsApi.getUserService().getCurrentUser(), changes);

      changes.add(backlogArt);

      ArtifactId teamFolder = AgileFolders.getTeamFolder(atsApi, teamUuid);
      changes.relate(teamFolder, AtsRelationTypes.AgileTeamToBacklog_Backlog, backlogArt);
      changes.relate(teamFolder, CoreRelationTypes.Default_Hierarchical__Child, backlogArt);

      changes.execute();
      return getAgileBacklog(logger, atsApi, backlogArt);
   }

   public static IAgileBacklog getAgileBacklog(Log logger, AtsApi atsApi, Object artifact) {
      return new AgileBacklog(logger, atsApi, (ArtifactToken) artifact);
   }

}