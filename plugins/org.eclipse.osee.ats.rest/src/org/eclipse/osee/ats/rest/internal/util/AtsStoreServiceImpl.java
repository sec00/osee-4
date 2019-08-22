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
package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreServiceImpl implements IAtsStoreService {

   private final IAttributeResolver attributeResolver;
   private final OrcsApi orcsApi;
   private final IAtsStateFactory stateFactory;
   private final IAtsLogFactory logFactory;
   private final IAtsNotifier notifier;
   private final IAtsServer atsApi;
   private final JdbcService jdbcService;

   public AtsStoreServiceImpl(IAttributeResolver attributeResolver, IAtsServer atsApi, OrcsApi orcsApi, IAtsStateFactory stateFactory, IAtsLogFactory logFactory, IAtsNotifier notifier, JdbcService jdbcService) {
      this.atsApi = atsApi;
      this.attributeResolver = attributeResolver;
      this.orcsApi = orcsApi;
      this.logFactory = logFactory;
      this.stateFactory = stateFactory;
      this.notifier = notifier;
      this.jdbcService = jdbcService;
   }

   @Override
   public JdbcService getJdbcService() {
      return jdbcService;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, IAtsUser asUser) {
      return new AtsChangeSet(atsApi, attributeResolver, orcsApi, stateFactory, logFactory, comment, asUser, notifier);
   }

   public QueryBuilder getQuery() {
      return orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch());
   }

   @Override
   public List<IAtsWorkItem> reload(Collection<IAtsWorkItem> inWorkWorkflows) {
      List<IAtsWorkItem> workItems = new ArrayList<>(inWorkWorkflows.size());
      List<ArtifactId> ids = AtsObjects.toArtifactIds(inWorkWorkflows);
      Iterator<ArtifactReadable> arts = getQuery().andIds(ids).getResults().iterator();
      while (arts.hasNext()) {
         workItems.add(atsApi.getWorkItemService().getWorkItem(arts.next()));
      }
      return workItems;
   }

   @Override
   public boolean isDeleted(IAtsObject atsObject) {
      return atsApi.getArtifact(atsObject).isDeleted();
   }

   /**
    * Uses artifact type inheritance to retrieve all TeamWorkflow artifact types
    */
   @Override
   public Set<ArtifactTypeToken> getTeamWorkflowArtifactTypes() {
      Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
      artifactTypes.addAll(
         orcsApi.getOrcsTypes().getArtifactTypes().getAllDescendantTypes(AtsArtifactTypes.TeamWorkflow));
      return artifactTypes;
   }

   @Override
   public boolean isAttributeTypeValid(IAtsObject atsObject, AttributeTypeToken attributeType) {
      return isAttributeTypeValid(atsObject.getStoreObject(), attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(ArtifactId artifact, AttributeTypeToken attributeType) {
      return ((ArtifactReadable) artifact).isAttributeTypeValid(attributeType);
   }

   @Override
   public AttributeTypeToken getAttributeType(String attrTypeName) {
      return orcsApi.getOrcsTypes().getAttributeTypes().getByName(attrTypeName);
   }

   @Override
   public ArtifactTypeToken getArtifactType(ArtifactId artifact) {
      if (artifact instanceof ArtifactReadable) {
         return ((ArtifactReadable) artifact).getArtifactType();
      }
      return getQuery().andId(artifact).asArtifactToken().getArtifactType();
   }

   @Override
   public boolean isDateType(AttributeTypeId attributeType) {
      return orcsApi.getOrcsTypes().getAttributeTypes().isDateType(attributeType);
   }

   @Override
   public boolean isOfType(ArtifactId artifact, ArtifactTypeId... artifactType) {
      if (artifact instanceof ArtifactReadable) {
         return ((ArtifactReadable) artifact).isOfType(artifactType);
      }
      return atsApi.getArtifact(artifact).isOfType(artifactType);
   }

   @Override
   public boolean isOfType(IAtsObject atsObject, ArtifactTypeToken... artifactType) {
      return atsApi.getArtifact(atsObject).isOfType(artifactType);
   }

   @Override
   public void executeChangeSet(String comment, IAtsObject atsObject) {
      executeChangeSet(comment, Collections.singleton(atsObject));
   }

   @Override
   public void executeChangeSet(String comment, Collection<? extends IAtsObject> atsObjects) {
      IAtsChangeSet changes = createAtsChangeSet(comment, atsApi.getUserService().getCurrentUser());
      for (IAtsObject atsObject : atsObjects) {
         changes.add(atsObject);
      }
      changes.execute();
   }

   @Override
   public ArtifactTypeToken getArtifactType(Long artTypeId) {
      return orcsApi.getOrcsTypes().getArtifactTypes().get(artTypeId);
   }

   @Override
   public Collection<AttributeTypeToken> getAttributeTypes() {
      return orcsApi.getOrcsTypes().getAttributeTypes().getAll();
   }

   /**
    * return false cause server always loads workItems fresh
    */
   @Override
   public boolean isChangedInDb(IAtsWorkItem workItem) {
      return false;
   }

   @Override
   public ArtifactTypeToken getArtifactType(IAtsObject atsObject) {
      return atsApi.getArtifact(atsObject).getArtifactType();
   }

   @Override
   public boolean isOfType(IAtsObject atsObject, ArtifactTypeToken artifactType) {
      return isOfType(atsObject.getStoreObject(), artifactType);
   }

   @Override
   public void clearCaches(IAtsWorkItem workItem) {
      ((WorkItem) workItem).clearCaches();
   }

   @Override
   public boolean isArtifactTypeInheritsFrom(ArtifactTypeToken artifactType, ArtifactTypeToken baseArtifactType) {
      return orcsApi.getOrcsTypes().getArtifactTypes().inheritsFrom(artifactType, baseArtifactType);
   }

   @Override
   public AttributeTypeToken getAttributeType(Long attrTypeId) {
      return orcsApi.getOrcsTypes().getAttributeTypes().get(attrTypeId);
   }

   @Override
   public Result setTransactionAssociatedArtifact(TransactionId trans, IAtsTeamWorkflow teamWf) {
      orcsApi.getTransactionFactory().setTransactionCommitArtifact(trans, teamWf.getStoreObject());
      return Result.TrueResult;
   }

   @Override
   public TransactionId getTransactionId(IAtsWorkItem workItem) {
      return atsApi.getArtifact(workItem.getId()).getTransaction();
   }

   @Override
   public boolean isDeleted(ArtifactId artifact) {
      return atsApi.getArtifact(artifact).isDeleted();
   }

   @Override
   public CustomizeData getCustomizationByGuid(String customize_guid) {
      CustomizeData cust = null;
      ArtifactReadable customizeStoreArt = getQuery().and(CoreAttributeTypes.XViewerCustomization, customize_guid,
         QueryOption.CONTAINS_MATCH_OPTIONS).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      if (customizeStoreArt.isValid()) {
         for (String custXml : atsApi.getAttributeResolver().getAttributesToStringList(customizeStoreArt,
            CoreAttributeTypes.XViewerCustomization)) {
            if (custXml.contains(customize_guid)) {
               cust = new CustomizeData(custXml);
               break;
            }
         }
      }
      return cust;
   }

   @Override
   public boolean isProductionDb() {
      return jdbcService.getClient().getConfig().isProduction();
   }

   @Override
   public boolean inheritsFrom(ArtifactTypeToken artType, ArtifactTypeToken... artifactType) {
      return orcsApi.getOrcsTypes().getArtifactTypes().inheritsFrom(artType, artifactType);
   }
}