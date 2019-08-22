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
package org.eclipse.osee.ats.ide.search.internal.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.IAtsConfigCacheQuery;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryService;
import org.eclipse.osee.ats.core.query.AtsConfigCacheQueryImpl;
import org.eclipse.osee.ats.core.query.AtsWorkItemFilter;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.IAtsClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceImpl extends AbstractAtsQueryService {

   private final IAtsClient atsClient;
   private static final Pattern namespacePattern = Pattern.compile("\"namespace\"\\s*:\\s*\"(.*?)\"");

   public AtsQueryServiceImpl(IAtsClient atsClient, JdbcService jdbcService) {
      super(jdbcService, atsClient.getServices());
      this.atsClient = atsClient;
   }

   @Override
   public IAtsQuery createQuery(WorkItemType workItemType, WorkItemType... workItemTypes) {
      Conditions.checkNotNull(workItemType, "workItemType");
      AtsQueryImpl query = new AtsQueryImpl(atsClient);
      query.isOfType(workItemType);
      if (workItemTypes != null) {
         for (WorkItemType type : workItemTypes) {
            query.isOfType(type);
         }
      }
      return query;
   }

   @Override
   public IAtsConfigQuery createQuery(ArtifactTypeToken... artifactType) {
      AtsConfigQueryImpl query = new AtsConfigQueryImpl(atsClient);
      query.isOfType(artifactType);
      return query;
   }

   @Override
   public IAtsWorkItemFilter createFilter(Collection<? extends IAtsWorkItem> workItems) {
      return new AtsWorkItemFilter(workItems, atsClient.getServices());
   }

   @Override
   public ArrayList<AtsSearchData> getSavedSearches(IAtsUser atsUser, String namespace) {
      ArrayList<AtsSearchData> searches = new ArrayList<>();
      ArtifactId userArt = atsUser.getStoreObject();
      for (IAttribute<String> attr : atsClient.getAttributeResolver().getAttributes(userArt,
         AtsAttributeTypes.QuickSearch)) {
         String jsonValue = attr.getValue();
         if (jsonValue.contains("\"" + namespace + "\"")) {
            try {
               AtsSearchData data = fromJson(namespace, jsonValue);
               if (data != null) {
                  searches.add(data);
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      return searches;
   }

   @Override
   public void saveSearch(IAtsUser atsUser, AtsSearchData data) {
      ArtifactId userArt = atsUser.getStoreObject();
      IAtsChangeSet changes =
         atsClient.getStoreService().createAtsChangeSet("Save ATS Search", atsClient.getUserService().getCurrentUser());

      try {
         IAttribute<String> attr = getAttrById(userArt, data.getId());
         if (attr == null) {
            changes.addAttribute((IAtsObject) atsUser, AtsAttributeTypes.QuickSearch, getStoreString(data));
         } else {
            changes.setAttribute(userArt, attr, getStoreString(data));
         }
         if (!changes.isEmpty()) {
            changes.execute();
         }
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to store ATS Search", ex);
      }

   }

   private IAttribute<String> getAttrById(ArtifactId artifact, Long attrId) {
      for (IAttribute<String> attr : atsClient.getAttributeResolver().getAttributes(artifact,
         AtsAttributeTypes.QuickSearch)) {
         String jsonValue = attr.getValue();
         try {
            AtsSearchData data = fromJson(jsonValue);
            if (attrId.equals(data.getId())) {
               return attr;
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return null;
   }

   @Override
   public void removeSearch(IAtsUser atsUser, AtsSearchData data) {
      ArtifactId userArt = atsUser.getStoreObject();
      IAtsChangeSet changes = atsClient.getStoreService().createAtsChangeSet("Remove ATS Search",
         atsClient.getUserService().getCurrentUser());

      try {
         IAttribute<String> attr = getAttrById(userArt, data.getId());
         if (attr != null) {
            changes.deleteAttribute(userArt, attr);
            changes.execute();
         }
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to remove ATS Search", ex);
      }
   }

   @Override
   public AtsSearchData getSearch(IAtsUser atsUser, Long id) {
      try {
         ArtifactId userArt = atsUser.getStoreObject();
         IAttribute<String> attr = getAttrById(userArt, id);
         if (attr != null) {
            AtsSearchData existing = fromJson(attr.getValue());
            if (existing != null) {
               return existing;
            }
         }
         return null;
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to get ATS Search", ex);
      }
   }

   private String getStoreString(AtsSearchData data) throws Exception {
      return JsonUtil.getMapper().writeValueAsString(data);
   }

   private AtsSearchData fromJson(String jsonValue) {
      Matcher m = namespacePattern.matcher(jsonValue);
      if (m.find()) {
         return fromJson(m.group(1), jsonValue);
      }
      return null;
   }

   private AtsSearchData fromJson(String namespace, String jsonValue) {
      AtsSearchData data = null;
      try {
         for (IAtsSearchDataProvider provider : atsClient.getSearchDataProviders()) {
            if (provider.getSupportedNamespaces().contains(namespace)) {
               data = provider.fromJson(namespace, jsonValue);
               if (data != null) {
                  break;
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.logf(Activator.class, Level.WARNING, ex,
            "Can't deserialize ATS Quick Search value [%s] for namespace [%s]", jsonValue, namespace);
      }
      return data;
   }

   @Override
   public AtsSearchData getSearch(String jsonStr) {
      return fromJson(jsonStr);
   }

   @Override
   public AtsSearchData createSearchData(String namespace, String searchName) {
      AtsSearchData data = null;
      try {
         for (IAtsSearchDataProvider provider : atsClient.getSearchDataProviders()) {
            if (provider.getSupportedNamespaces().contains(namespace)) {
               data = provider.createSearchData(namespace, searchName);
               if (data != null) {
                  break;
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.logf(Activator.class, Level.WARNING, ex,
            "Can't create ATS Quick Search namespace [%s] and searchName [%s]", namespace, searchName);
      }
      return data;
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(List<ArtifactId> ids, BranchId branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFrom(ids, branch));
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(BranchId branch, boolean includeInherited, ArtifactTypeToken... artifactType) {
      List<ArtifactTypeToken> types = Arrays.asList(artifactType);
      if (includeInherited) {
         if (artifactType.length == 1) {
            return Collections.castAll(ArtifactQuery.getArtifactListFromTypeWithInheritence(types.iterator().next(),
               branch, DeletionFlag.EXCLUDE_DELETED));
         } else {
            throw new UnsupportedOperationException("Not supported on client");
         }
      }
      return Collections.castAll(ArtifactQuery.getArtifactListFromTypes(types, branch, DeletionFlag.EXCLUDE_DELETED));
   }

   @Override
   public ArtifactToken getArtifactToken(ArtifactId artifactId) {
      return ArtifactQuery.getArtifactTokenFromId(atsClient.getAtsBranch(), artifactId);
   }

   @Override
   public Collection<ArtifactToken> getRelatedToTokens(BranchId branch, ArtifactId artifact, RelationTypeSide relationType, ArtifactTypeId artifactType) {
      HashCollection<ArtifactId, ArtifactToken> tokenMap = ArtifactQuery.getArtifactTokenListFromRelated(
         atsClient.getAtsBranch(), java.util.Collections.singleton(artifact), artifactType, relationType);
      Collection<ArtifactToken> result = tokenMap.getValues(artifact);
      if (result != null) {
         return result;
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public Artifact getArtifact(Long id) {
      Conditions.assertTrue(id > 0, "Art Id must be > 0, not %s", id);
      try {
         return ArtifactQuery.getArtifactFromId(id, atsClient.getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public <T extends IAtsObject> Artifact getArtifact(T atsObject) {
      Artifact result = null;
      try {
         if (atsObject.getStoreObject() instanceof Artifact) {
            result = AtsClientService.get().getQueryServiceClient().getArtifact(atsObject);
         } else {
            result = getArtifact(atsObject.getId());
            if (result != null) {
               atsObject.setStoreObject(result);
            }
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public <T extends ArtifactId> Artifact getArtifact(T artifact) {
      Artifact result = null;
      try {
         if (artifact instanceof Artifact) {
            result = AtsClientService.get().getQueryServiceClient().getArtifact(artifact);
         } else if (artifact instanceof IAtsObject) {
            IAtsObject atsObject = (IAtsObject) artifact;
            if (atsObject.getStoreObject() instanceof Artifact) {
               result = AtsClientService.get().getQueryServiceClient().getArtifact(atsObject);
            } else {
               result = getArtifact(atsObject.getId());
            }
         } else {
            result = getArtifact(artifact.getId());
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public Artifact getArtifact(ArtifactId artifact, BranchId branch) {
      return getArtifact(artifact, branch, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public Artifact getArtifact(ArtifactId artifact, BranchId branch, DeletionFlag deletionFlag) {
      return ArtifactQuery.getArtifactOrNull(artifact, branch, deletionFlag);
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(Collection<Long> ids) {
      List<ArtifactId> artifactIds = new ArrayList<>(ids.size());
      for (Long id : ids) {
         artifactIds.add(ArtifactId.valueOf(id));
      }
      return Collections.castAll(
         ArtifactQuery.getArtifactListFrom(artifactIds, atsClient.getAtsBranch(), DeletionFlag.EXCLUDE_DELETED));
   }

   @Override
   public ArtifactToken getArtifactByName(ArtifactTypeId artType, String name) {
      return ArtifactQuery.checkArtifactFromTypeAndName(artType, name, atsClient.getAtsBranch());
   }

   @Override
   public ArtifactToken getArtifactByNameOrSentinel(ArtifactTypeId artType, String name) {

      if (ArtifactQuery.checkArtifactFromTypeAndName(artType, name, atsClient.getAtsBranch()) != null) {
         return ArtifactQuery.checkArtifactFromTypeAndName(artType, name, atsClient.getAtsBranch());
      }
      return ArtifactReadable.SENTINEL;

   }

   @Override
   public ArtifactToken getHistoricalArtifactOrNull(ArtifactId artifact, TransactionToken transaction, DeletionFlag deletionFlag) {
      return ArtifactQuery.getHistoricalArtifactOrNull(artifact, transaction, deletionFlag);
   }

   @Override
   public ArtifactToken getArtifactByGuid(String guid) {
      return ArtifactQuery.getArtifactFromId(guid, atsClient.getAtsBranch());
   }

   @Override
   public ArtifactToken getArtifactByGuidOrSentinel(String guid) {
      if (ArtifactQuery.getArtifactFromId(guid, atsClient.getAtsBranch()) != null) {
         return ArtifactQuery.getArtifactFromId(guid, atsClient.getAtsBranch());
      }
      return ArtifactReadable.SENTINEL;
   }

   @Override
   public List<ArtifactToken> getArtifactListFromTypeWithInheritence(ArtifactTypeToken artifactType, BranchId branch, DeletionFlag deletionFlag) {
      return Collections.castAll(
         ArtifactQuery.getArtifactListFromTypeWithInheritence(artifactType, branch, deletionFlag));
   }

   @Override
   public IAtsConfigCacheQuery createConfigCacheQuery(ArtifactTypeToken... artifactType) {
      AtsConfigCacheQueryImpl query = new AtsConfigCacheQueryImpl(atsClient);
      query.isOfType(artifactType);
      return query;
   }

}