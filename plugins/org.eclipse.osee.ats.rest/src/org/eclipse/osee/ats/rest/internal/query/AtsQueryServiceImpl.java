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
package org.eclipse.osee.ats.rest.internal.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsOrcsScriptQuery;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryService;
import org.eclipse.osee.ats.core.query.AtsWorkItemFilter;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceImpl extends AbstractAtsQueryService {

   private final IAtsServer atsServer;

   public AtsQueryServiceImpl(IAtsServer atsServer, JdbcService jdbcService) {
      super(jdbcService, atsServer.getServices());
      this.atsServer = atsServer;
   }

   private QueryBuilder getQuery() {
      return atsServer.getOrcsApi().getQueryFactory().fromBranch(atsServer.getAtsBranch());
   }

   @Override
   public IAtsQuery createQuery(WorkItemType workItemType, WorkItemType... workItemTypes) {
      AtsQueryImpl query = new AtsQueryImpl(atsServer);
      query.isOfType(workItemType);
      for (WorkItemType type : workItemTypes) {
         query.isOfType(type);
      }
      return query;
   }

   @Override
   public IAtsConfigQuery createQuery(IArtifactType... artifactType) {
      AtsConfigQueryImpl query = new AtsConfigQueryImpl(atsServer);
      query.isOfType(artifactType);
      return query;
   }

   @Override
   public IAtsWorkItemFilter createFilter(Collection<? extends IAtsWorkItem> workItems) {
      return new AtsWorkItemFilter(workItems, atsServer.getServices());
   }

   @Override
   public ArrayList<AtsSearchData> getSavedSearches(IAtsUser atsUser, String namespace) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public void saveSearch(IAtsUser atsUser, AtsSearchData data) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public void removeSearch(IAtsUser atsUser, AtsSearchData data) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public AtsSearchData getSearch(IAtsUser atsUser, Long id) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public AtsSearchData getSearch(String jsonStr) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public AtsSearchData createSearchData(String namespace, String searchName) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(List<ArtifactId> ids, BranchId branch) {
      return Collections.castAll(
         atsServer.getOrcsApi().getQueryFactory().fromBranch(branch).andIds(ids).getResults().getList());
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(BranchId branch, IArtifactType... artifactType) {
      return Collections.castAll(atsServer.getOrcsApi().getQueryFactory().fromBranch(branch).andTypeEquals(
         artifactType).getResults().getList());
   }

   @Override
   public IAtsOrcsScriptQuery createOrcsScriptQuery(String query, Object... data) {
      return new AtsOrcsScriptQuery(String.format(query, data), atsServer);
   }

   @Override
   public ArtifactToken getArtifactToken(ArtifactId artifactId) {
      return getQuery().andId(artifactId).loadArtifactToken();
   }

   @Override
   public Collection<ArtifactToken> getRelatedToTokens(BranchId branch, ArtifactId artifact, RelationTypeSide relationType, ArtifactTypeId artifactType) {
      HashCollection<ArtifactId, ArtifactToken> tokenMap = TokenSearchOperations.getArtifactTokenListFromRelated(branch,
         java.util.Collections.singleton(artifact), artifactType, relationType, atsServer.getOrcsApi(), jdbcService);
      Collection<ArtifactToken> result = tokenMap.getValues(artifact);
      if (result != null) {
         return result;
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public <T extends ArtifactId> ArtifactReadable getArtifact(T artifact) {
      ArtifactReadable result = null;
      try {
         if (artifact instanceof ArtifactReadable) {
            result = (ArtifactReadable) artifact;
         } else if (artifact instanceof IAtsObject) {
            IAtsObject atsObject = (IAtsObject) artifact;
            if (atsObject.getStoreObject() instanceof ArtifactReadable) {
               result = (ArtifactReadable) atsObject.getStoreObject();
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
   public <T extends IAtsObject> ArtifactReadable getArtifact(T atsObject) {
      ArtifactReadable result = null;
      try {
         if (atsObject.getStoreObject() instanceof ArtifactReadable) {
            result = (ArtifactReadable) atsObject.getStoreObject();
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
   public ArtifactReadable getArtifact(Long id) {
      return getQuery().andId(id).getResults().getAtMostOneOrNull();
   }

   @Override
   public ArtifactReadable getArtifact(ArtifactId artifact, BranchId branch) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(Collection<Long> ids) {
      Collection<ArtifactToken> artifacts = new LinkedList<>();
      Iterator<ArtifactReadable> iterator = getQuery().andUuids(ids).getResults().iterator();
      while (iterator.hasNext()) {
         artifacts.add(iterator.next());
      }
      return artifacts;
   }

   @Override
   public ArtifactToken getArtifactByName(IArtifactType artifactType, String name) {
      return getQuery().andIsOfType(artifactType).andNameEquals(name).getResults().getAtMostOneOrNull();
   }
}