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
package org.eclipse.osee.ats.core.version;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Active;
import java.rmi.activation.Activator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G Dunne
 */
public class AtsVersionServiceImpl implements IAtsVersionService {

   private final AtsApi atsApi;
   private final EventAdmin eventAdmin;

   public AtsVersionServiceImpl(AtsApi atsApi, EventAdmin eventAdmin) {
      super();
      this.atsApi = atsApi;
      this.eventAdmin = eventAdmin;
   }

   @Override
   public IAtsVersion getVersion(ArtifactId versionId) {
      IAtsVersion version = null;
      if (versionId instanceof IAtsVersion) {
         version = (IAtsVersion) versionId;
      } else {
         ArtifactToken art = atsApi.getQueryService().getArtifact(versionId);
         if (art.isOfType(AtsArtifactTypes.Version)) {
            version = createVersion(art);
         }
      }
      return version;
   }

   @Override
   public Version createVersion(ArtifactToken verArt) {
      Version version = new Version(verArt, atsApi);
      version.setName(verArt.getName());
      version.setId(verArt.getId());
      version.setGuid(verArt.getGuid());
      version.setActive(atsApi.getAttributeResolver().getSoleAttributeValue(verArt, Active, true));
      version.setAllowCreateBranch(
         atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.AllowCreateBranch, false));
      version.setAllowCommitBranch(
         atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.AllowCommitBranch, false));
      version.setReleased(
         atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.Released, false));
      version.setLocked(
         atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.VersionLocked, false));
      version.setNextVersion(
         atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.NextVersion, false));
      version.setBaselineBranch(BranchId.valueOf(
         atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.BaselineBranchId, "-1")));
      return version;
   }

   @Override
   public IAtsVersion getTargetedVersion(IAtsWorkItem workItem) {
      IAtsVersion version = null;
      IAtsTeamWorkflow team = workItem.getParentTeamWorkflow();
      if (team != null) {
         version = getTargetedVersionByTeamWf(team);
      }
      return version;
   }

   @Override
   public IAtsVersion getFoundInVersion(IAtsWorkItem workItem) {
      try {
         ArtifactId artId = atsApi.getRelationResolver().getRelatedOrNull(workItem,
            AtsRelationTypes.TeamWorkflowToFoundInVersion_Version);
         if (artId != null && artId.isValid()) {
            IAtsVersion foundInVersion = atsApi.getVersionService().getVersion(artId);
            return foundInVersion;
         }
         return null;
      } catch (Exception e) {
         return null;
      }
   }

   @Override
   public IAtsVersion getTargetedVersionByTeamWf(IAtsTeamWorkflow team) {
      if (team == null) {
         throw new OseeArgumentException("Team Workflow can not be null %s", team);
      }
      Collection<ArtifactToken> versions = atsApi.getRelationResolver().getRelated(team.getStoreObject(),
         AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      IAtsVersion version = null;
      if (!versions.isEmpty()) {
         if (versions.size() > 1) {
            OseeLog.log(Activator.class, Level.SEVERE,
               "Multiple targeted versions for artifact " + team.toStringWithId());
         } else {
            version = atsApi.getVersionService().getVersion(versions.iterator().next());
         }
      }
      return version;
   }

   @Override
   public void removeTargetedVersion(IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
      changes.unrelateAll(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
   }

   @Override
   public IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version, IAtsChangeSet changes) {
      Collection<ArtifactToken> previousVersions =
         atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);

      ArtifactId previousVersion = ArtifactId.SENTINEL;
      if (!previousVersions.isEmpty()) {
         previousVersion = ArtifactId.valueOf(previousVersions.iterator().next().getId());
      }
      changes.setRelation(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, version);
      changes.addExecuteListener(getPostPersistExecutionListener(teamWf, version, previousVersion));
      return version;
   }

   protected IExecuteListener getPostPersistExecutionListener(IAtsTeamWorkflow teamWf, IAtsVersion newVersion, ArtifactId previousVersion) {
      return new IExecuteListener() {

         @Override
         public void changesStored(IAtsChangeSet changes) {
            try {
               HashMap<String, Object> properties = new HashMap<>();
               properties.put(AtsTopicEvent.WORK_ITEM_IDS_KEY, teamWf.getIdString());
               properties.put(AtsTopicEvent.NEW_ATS_VERSION_ID,
                  newVersion == null ? ArtifactId.SENTINEL.getIdString() : newVersion.getIdString());
               properties.put(AtsTopicEvent.PREVIOUS_ATS_VERSION_ID, previousVersion.getIdString());

               Event event = new Event(AtsTopicEvent.TARGETED_VERSION_MODIFIED, properties);

               eventAdmin.postEvent(event);

            } catch (OseeCoreException ex) {
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }
      };
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    */
   @Override
   public boolean isReleased(IAtsTeamWorkflow teamWf) {
      boolean released = false;
      IAtsVersion verArt = getTargetedVersion(teamWf);
      if (verArt != null) {
         released = verArt.isReleased();
      }
      return released;
   }

   @Override
   public boolean isVersionLocked(IAtsTeamWorkflow teamWf) {
      boolean locked = false;
      IAtsVersion verArt = getTargetedVersion(teamWf);
      if (verArt != null) {
         locked = verArt.isLocked();
      }
      return locked;
   }

   @Override
   public boolean hasTargetedVersion(IAtsWorkItem workItem) {
      return getTargetedVersion(workItem) != null;
   }

   @Override
   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      Object verArt = atsApi.getQueryService().getArtifact(version);
      if (verArt == null) {
         throw new OseeStateException("Version [%s] does not exist.", version);
      }
      Object teamDefArt = atsApi.getQueryService().getArtifact(teamDef);
      if (teamDefArt == null) {
         throw new OseeStateException("Team Definition [%s] does not exist.", teamDef);
      }
      if (!atsApi.getRelationResolver().areRelated(version, AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition,
         teamDef)) {
         changes.relate(version, AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, teamDefArt);
      }
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version) {
      return atsApi.getRelationResolver().getRelatedOrNull(version,
         AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, IAtsTeamDefinition.class);
   }

   @Override
   public IAtsVersion getById(ArtifactId id) {
      IAtsVersion version = null;
      ArtifactToken verArt = atsApi.getQueryService().getArtifact(id.getId());
      if (verArt != null) {
         version = atsApi.getVersionService().getVersion(verArt);
      }
      return version;
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTargetedForTeamWorkflows(IAtsVersion verArt) {
      List<IAtsTeamWorkflow> teamWorkflows = new LinkedList<>();
      for (IAtsTeamWorkflow teamWf : atsApi.getRelationResolver().getRelated(verArt,
         AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow, IAtsTeamWorkflow.class)) {
         teamWorkflows.add(teamWf);
      }
      return teamWorkflows;
   }

   @Override
   public BranchId getBranch(IAtsVersion version) {
      String branchId =
         atsApi.getAttributeResolver().getSoleAttributeValue(version, AtsAttributeTypes.BaselineBranchId, "");
      if (branchId == null || branchId.isEmpty()) {
         return BranchId.SENTINEL;
      }
      return BranchId.valueOf(branchId);
   }

   @Override
   public IAtsVersion createVersion(IAtsProgram program, String versionName, IAtsChangeSet changes) {
      IAtsVersion version = null;
      version = atsApi.getProgramService().getVersion(program, versionName);
      if (version == null) {
         version = atsApi.getVersionService().getVersion(changes.createArtifact(AtsArtifactTypes.Version, versionName));
      }
      return version;
   }

   @Override
   public IAtsVersion getVersion(IAtsProgram program, String versionName) {
      return atsApi.getProgramService().getVersion(program, versionName);
   }

   @Override
   public IAtsVersion createVersion(String title, long id, IAtsChangeSet changes) {
      return createVersion(title, id, changes, atsApi);
   }

   @Override
   public IAtsVersion createVersion(String name, IAtsChangeSet changes) {
      return createVersion(name, changes, atsApi);
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      return atsApi.getTeamDefinitionService().getVersions(teamDef);
   }

   @Override
   public IAtsVersion createVersion(String title, IAtsChangeSet changes, AtsApi atsApi) {
      return createVersion(title, Lib.generateArtifactIdAsInt(), changes, atsApi);
   }

   @Override
   public IAtsVersion createVersion(String name, long id, IAtsChangeSet changes, AtsApi atsApi) {
      ArtifactToken artifact = changes.createArtifact(AtsArtifactTypes.Version, name, id);
      return new Version(artifact, atsApi);
   }

   @Override
   public boolean isTeamUsesVersions(IAtsTeamDefinition teamDef) {
      return getTeamDefinitionHoldingVersions(teamDef) != null;
   }

   @Override
   public IAtsVersion getNextReleaseVersion(IAtsTeamDefinition teamDef) {
      IAtsVersion result = null;
      for (IAtsVersion version : getVersions(teamDef)) {
         if (version.isNextVersion()) {
            result = version;
            break;
         }
      }
      return result;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionHoldingVersions(IAtsTeamDefinition teamDef) {
      IAtsTeamDefinition teamDefHoldVer = null;
      if (getVersions(teamDef).size() > 0) {
         teamDefHoldVer = teamDef;
      } else {
         IAtsTeamDefinition parentTda = atsApi.getTeamDefinitionService().getParentTeamDef(teamDef);
         if (parentTda != null) {
            teamDefHoldVer = getTeamDefinitionHoldingVersions(parentTda);
         }
      }
      return teamDefHoldVer;
   }

   @Override
   public IAtsVersion getVersion(IAtsTeamDefinition teamDef, String name) {
      IAtsVersion result = null;
      for (IAtsVersion version : getVersions(teamDef)) {
         if (version.getName().equals(name)) {
            result = version;
            break;
         }
      }
      return result;
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef, VersionReleaseType releaseType, VersionLockedType lockedType) {
      return org.eclipse.osee.framework.jdk.core.util.Collections.setIntersection(
         getVersionsReleased(teamDef, releaseType), getVersionsLocked(teamDef, lockedType));
   }

   @Override
   public Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(IAtsTeamDefinition teamDef, VersionReleaseType releaseType, VersionLockedType lockedType) {
      IAtsTeamDefinition teamDefHoldVer = getTeamDefinitionHoldingVersions(teamDef);
      if (teamDef == null) {
         return new ArrayList<>();
      }
      return getVersions(teamDefHoldVer, releaseType, lockedType);
   }

   @Override
   public Collection<IAtsVersion> getVersionsLocked(IAtsTeamDefinition teamDef, VersionLockedType lockType) {
      ArrayList<IAtsVersion> versions = new ArrayList<>();
      for (IAtsVersion version : getVersions(teamDef)) {
         if (version.isLocked() && (lockType == VersionLockedType.Locked || lockType == VersionLockedType.Both)) {
            versions.add(version);
         } else if (!version.isLocked() && lockType == VersionLockedType.UnLocked || lockType == VersionLockedType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   @Override
   public Collection<IAtsVersion> getVersionsReleased(IAtsTeamDefinition teamDef, VersionReleaseType releaseType) {
      ArrayList<IAtsVersion> versions = new ArrayList<>();
      for (IAtsVersion version : getVersions(teamDef)) {
         if (version.isReleased() && (releaseType == VersionReleaseType.Released || releaseType == VersionReleaseType.Both)) {
            versions.add(version);
         } else if (!version.isReleased() && releaseType == VersionReleaseType.UnReleased || releaseType == VersionReleaseType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   @Override
   public Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(IAtsTeamDefinition teamDef) {
      IAtsTeamDefinition teamDefHoldVer = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
      return getVersions(teamDefHoldVer);
   }

   @Override
   public BranchId getBaselineBranchIdInherited(IAtsVersion version) {
      if (version.getBaselineBranch().isValid()) {
         return version.getBaselineBranch();
      } else {
         try {
            IAtsTeamDefinition teamDef = getTeamDefinition(version);
            if (teamDef != null) {
               return atsApi.getTeamDefinitionService().getTeamBranchId(teamDef);
            } else {
               return BranchId.SENTINEL;
            }
         } catch (OseeCoreException ex) {
            return BranchId.SENTINEL;
         }
      }
   }

   @Override
   public Result isAllowCommitBranchInherited(IAtsVersion version) {
      if (!version.isAllowCommitBranch()) {
         return new Result(false, "Version [" + this + "] not configured to allow branch commit.");
      }
      if (version.isInvalid()) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Date getEstimatedReleaseDate(IAtsVersion version) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(version, AtsAttributeTypes.EstimatedReleaseDate, null);
   }

   @Override
   public Date getReleaseDate(IAtsVersion version) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(version, AtsAttributeTypes.ReleaseDate, null);
   }

   @Override
   public Result isAllowCreateBranchInherited(IAtsVersion version) {
      if (!version.isAllowCreateBranch()) {
         return new Result(false, "Branch creation disabled for Version [" + this + "]");
      }
      if (version.isBranchInvalid()) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

}
