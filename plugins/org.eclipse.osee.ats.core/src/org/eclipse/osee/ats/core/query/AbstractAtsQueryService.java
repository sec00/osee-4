/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsQueryService implements IAtsQueryService {

   protected final JdbcService jdbcService;
   private final AtsApi atsApi;

   public AbstractAtsQueryService(JdbcService jdbcService, AtsApi atsApi) {
      this.jdbcService = jdbcService;
      this.atsApi = atsApi;
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItemsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (ArtifactToken art : atsApi.getQueryService().getArtifacts(ids, atsApi.getAtsBranch())) {
         if (atsApi.getStoreService().isOfType(art, AtsArtifactTypes.AbstractWorkflowArtifact)) {
            IAtsWorkItem workItem = atsApi.getWorkItemFactory().getWorkItem(art);
            if (workItem != null) {
               workItems.add(workItem);
            }
         }
      }
      return workItems;
   }

   @Override
   public Collection<ArtifactToken> getArtifactsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      return atsApi.getQueryService().getArtifacts(ids, atsApi.getAtsBranch());
   }

   @Override
   public List<ArtifactId> getArtifactIdsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      return ids;
   }

   @Override
   public List<ArtifactToken> getArtifactTokensFromQuery(String query, Object... data) {
      List<ArtifactToken> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactToken.valueOf(stmt.getLong("art_id"),
         stmt.getString("name"), BranchId.valueOf(stmt.getLong("branch_id")))), query, data);
      return ids;
   }

   @Override
   public void runUpdate(String query, Object... data) {
      jdbcService.getClient().runPreparedUpdate(query, data);
   }

   @Override
   public List<IAtsWorkItem> getWorkItemListByIds(String ids) {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (ArtifactToken art : getArtifactListByIdsStr(ids)) {
         IAtsWorkItem workItem = atsApi.getWorkItemFactory().getWorkItem(art);
         if (workItem != null) {
            workItems.add(workItem);
         }
      }
      return workItems;
   }

   /**
    * @param idList id,id,id
    */
   @Override
   public List<ArtifactToken> getArtifactListByIdsStr(String idList) {
      List<ArtifactToken> actions = new ArrayList<>();
      for (String id : idList.split(",")) {
         id = id.replaceAll("^ +", "");
         id = id.replaceAll(" +$", "");
         ArtifactToken action = getArtifactById(id);
         if (action != null) {
            actions.add(action);
         }
      }
      return actions;
   }

   @Override
   public List<String> getIdsFromStr(String idList) {
      List<String> ids = new ArrayList<>();
      for (String id : idList.split(",")) {
         id = id.replaceAll("^ +", "");
         id = id.replaceAll(" +$", "");
         ids.add(id);
      }
      return ids;
   }

   @Override
   public ArtifactToken getArtifactById(String id) {
      ArtifactToken action = null;
      if (GUID.isValid(id)) {
         action = getArtifactByGuid(id);
      }
      Long pid = null;
      if (Strings.isNumeric(id)) {
         pid = Long.parseLong(id);
      }
      if (pid != null) {
         action = getArtifact(pid);
      }
      if (action == null) {
         action = getArtifactByAtsId(id);
      }
      return action;
   }

   private ArtifactToken getArtifactByAtsId(String id) {
      return atsApi.getArtifactByAtsId(id);
   }

   private ArtifactToken getArtifact(Long id) {
      return atsApi.getArtifact(id);
   }

   public ArtifactToken getArtifactByGuid(String guid) {
      return atsApi.getArtifactByGuid(guid);
   }

}