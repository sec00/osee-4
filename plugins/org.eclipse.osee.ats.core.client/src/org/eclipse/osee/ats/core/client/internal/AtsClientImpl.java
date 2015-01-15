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
package org.eclipse.osee.ats.core.client.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProviders;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IVersionFactory;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.ai.ActionableItemManager;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.core.client.branch.internal.AtsBranchServiceImpl;
import org.eclipse.osee.ats.core.client.config.IAtsClientVersionService;
import org.eclipse.osee.ats.core.client.internal.config.ActionableItemFactory;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.client.internal.config.AtsConfigCacheProvider;
import org.eclipse.osee.ats.core.client.internal.config.TeamDefinitionFactory;
import org.eclipse.osee.ats.core.client.internal.config.VersionFactory;
import org.eclipse.osee.ats.core.client.internal.ev.AtsEarnedValueImpl;
import org.eclipse.osee.ats.core.client.internal.query.AtsQueryServiceIimpl;
import org.eclipse.osee.ats.core.client.internal.review.AtsReviewServiceImpl;
import org.eclipse.osee.ats.core.client.internal.store.ActionableItemArtifactReader;
import org.eclipse.osee.ats.core.client.internal.store.ActionableItemArtifactWriter;
import org.eclipse.osee.ats.core.client.internal.store.AtsArtifactStore;
import org.eclipse.osee.ats.core.client.internal.store.AtsVersionCache;
import org.eclipse.osee.ats.core.client.internal.store.AtsVersionServiceImpl;
import org.eclipse.osee.ats.core.client.internal.store.TeamDefinitionArtifactReader;
import org.eclipse.osee.ats.core.client.internal.store.TeamDefinitionArtifactWriter;
import org.eclipse.osee.ats.core.client.internal.store.VersionArtifactReader;
import org.eclipse.osee.ats.core.client.internal.store.VersionArtifactWriter;
import org.eclipse.osee.ats.core.client.internal.user.AtsUserServiceImpl;
import org.eclipse.osee.ats.core.client.internal.workdef.AtsWorkDefinitionCacheProvider;
import org.eclipse.osee.ats.core.client.internal.workdef.AtsWorkItemArtifactProviderImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsRelationResolverServiceImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.core.client.program.internal.AtsProgramService;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.client.team.AtsTeamDefinitionService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.column.IAtsColumnUtilities;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.eclipse.osee.ats.core.util.IAtsActionFactory;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionAdminImpl;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionCache;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public class AtsClientImpl implements IAtsClient {

   private IAtsStateFactory stateFactory;
   private IAtsWorkDefinitionService workDefService;
   private IAtsWorkItemArtifactService workItemArtifactProvider;
   private final AtsConfigProxy configProxy = new AtsConfigProxy();
   private IAtsClientVersionService versionService;
   private IAtsArtifactStore artifactStore;
   private CacheProvider<AtsArtifactConfigCache> configCacheProvider;
   private IAtsWorkDefinitionAdmin workDefAdmin;
   private IActionableItemFactory actionableItemFactory;
   private ITeamDefinitionFactory teamDefFactory;
   private IVersionFactory versionFactory;
   private CacheProvider<AtsWorkDefinitionCache> workDefCacheProvider;
   private IAtsEarnedValueService earnedValueService;
   private IAtsUserService userService;
   private IAtsUserServiceClient userServiceClient;
   private IAtsWorkItemService workItemService;
   private IAtsBranchService branchService;
   private IAtsReviewService reviewService;
   private IAttributeResolver attributeResolverService;
   private ITeamWorkflowProviders teamWorkflowProvider;
   private ISequenceProvider sequenceProvider;
   private IAtsActionFactory actionFactory;
   private IAtsLogFactory atsLogFactory;
   private IAtsStateFactory atsStateFactory;
   private IAtsWorkStateFactory workStateFactory;
   private IAtsLogFactory logFactory;
   private IAtsColumnUtilities columnUtilities;
   private IAtsUtilService utilService;
   private JdbcService jdbcService;
   private IAtsWorkItemFactory workItemFactory;
   private IAtsConfigItemFactory configItemFactory;
   private ActionableItemManager actionableItemManager;
   private IRelationResolver relationResolver;
   private IAtsProgramService programService;
   private IAtsTeamDefinitionService teamDefinitionService;
   private IAtsQueryService atsQueryService;

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setAtsWorkDefinitionService(IAtsWorkDefinitionService workDefService) {
      this.workDefService = workDefService;
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(workDefService, "IAtsWorkDefinitionService");
      Map<Class<? extends IAtsConfigObject>, IAtsArtifactWriter<? extends IAtsConfigObject>> writers =
         new HashMap<Class<? extends IAtsConfigObject>, IAtsArtifactWriter<? extends IAtsConfigObject>>();

      Map<IArtifactType, IAtsArtifactReader<? extends IAtsConfigObject>> readers =
         new HashMap<IArtifactType, IAtsArtifactReader<? extends IAtsConfigObject>>();

      writers.put(IAtsActionableItem.class, new ActionableItemArtifactWriter());
      writers.put(IAtsTeamDefinition.class, new TeamDefinitionArtifactWriter());
      writers.put(IAtsVersion.class, new VersionArtifactWriter());

      userService = new AtsUserServiceImpl();
      userServiceClient = (IAtsUserServiceClient) userService;

      artifactStore = new AtsArtifactStore(readers, writers);
      configCacheProvider = new AtsConfigCacheProvider(artifactStore);
      earnedValueService = new AtsEarnedValueImpl();

      configItemFactory = new ConfigItemFactory(this);
      AtsVersionCache versionCache = new AtsVersionCache(configCacheProvider);
      versionService = new AtsVersionServiceImpl(this, configCacheProvider, versionCache);

      actionableItemFactory = new ActionableItemFactory();
      teamDefFactory = new TeamDefinitionFactory();
      workItemFactory = new WorkItemFactory(this);
      versionFactory = new VersionFactory(versionService);

      readers.put(AtsArtifactTypes.ActionableItem, new ActionableItemArtifactReader(actionableItemFactory,
         teamDefFactory, versionFactory, userServiceClient));
      readers.put(AtsArtifactTypes.TeamDefinition, new TeamDefinitionArtifactReader(actionableItemFactory,
         teamDefFactory, versionFactory, versionService, userServiceClient));
      readers.put(AtsArtifactTypes.Version, new VersionArtifactReader(actionableItemFactory, teamDefFactory,
         versionFactory, versionService));

      teamWorkflowProvider = TeamWorkFlowManager.getTeamWorkflowProviders();

      workDefCacheProvider = new AtsWorkDefinitionCacheProvider(workDefService);
      workItemArtifactProvider = new AtsWorkItemArtifactProviderImpl();
      workItemService = new AtsWorkItemServiceImpl(workItemArtifactProvider);
      attributeResolverService = new AtsAttributeResolverServiceImpl();
      relationResolver = new AtsRelationResolverServiceImpl();

      workDefAdmin =
         new AtsWorkDefinitionAdminImpl(workDefCacheProvider, workItemService, workDefService, teamWorkflowProvider,
            attributeResolverService);
      branchService = new AtsBranchServiceImpl(this);
      reviewService = new AtsReviewServiceImpl(this);

      atsLogFactory = AtsCoreFactory.newLogFactory();
      atsStateFactory = AtsCoreFactory.newStateFactory(getServices(), atsLogFactory);

      actionableItemManager = new ActionableItemManager(getConfig(), attributeResolverService);
      sequenceProvider = new ISequenceProvider() {

         @Override
         public long getNext(String sequenceName) {
            return jdbcService.getClient().getNextSequence(sequenceName);
         }
      };
      utilService = AtsCoreFactory.getUtilService(attributeResolverService);

      programService = new AtsProgramService(configProxy);
      teamDefinitionService = new AtsTeamDefinitionService(configProxy, configItemFactory);

      actionFactory =
         new ActionFactory(workItemFactory, utilService, sequenceProvider, workItemService, actionableItemManager,
            userService, attributeResolverService, atsStateFactory, configProxy, getServices());

   }

   @Override
   public IVersionFactory getVersionFactory() {
      return versionFactory;
   }

   public void stop() {
      if (workDefAdmin != null) {
         workDefAdmin.clearCaches();
      }
      workDefAdmin = null;

      if (configCacheProvider != null) {
         configCacheProvider.invalidate();
         configCacheProvider = null;
      }

      if (workDefCacheProvider != null) {
         workDefCacheProvider.invalidate();
         workDefCacheProvider = null;
      }
      versionService = null;
      artifactStore = null;
      actionableItemFactory = null;
      teamDefFactory = null;
      versionFactory = null;

   }

   @Override
   public <T extends IAtsConfigObject> Artifact storeConfigObject(T configObject, IAtsChangeSet changes) throws OseeCoreException {
      AtsArtifactConfigCache atsConfigCache = getConfigCache();
      return artifactStore.store(atsConfigCache, configObject, changes);
   }

   @Override
   public <T extends IAtsConfigObject> T getConfigObject(Artifact artifact) throws OseeCoreException {
      AtsArtifactConfigCache atsConfigCache = getConfigCache();
      return artifactStore.load(atsConfigCache, artifact);
   }

   @Override
   public Artifact getConfigArtifact(IAtsConfigObject atsConfigObject) throws OseeCoreException {
      return getConfigCache().getArtifact(atsConfigObject);
   }

   @Override
   public List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects) throws OseeCoreException {
      return getConfigCache().getArtifacts(atsObjects);
   }

   @Override
   public <T extends IAtsConfigObject> Collection<T> getConfigObjects(Collection<? extends Artifact> artifacts, Class<T> clazz) throws OseeCoreException {
      IAtsConfig config = getConfig();
      List<T> objects = new ArrayList<T>();
      for (Artifact art : artifacts) {
         objects.addAll(config.getByTag(art.getGuid(), clazz));
      }
      return objects;
   }

   @Override
   public void invalidateConfigCache() {
      configCacheProvider.invalidate();
   }

   @Override
   public void reloadConfigCache() throws OseeCoreException {
      configCacheProvider.invalidate();
      configCacheProvider.get();
   }

   @Override
   public void reloadWorkDefinitionCache() throws OseeCoreException {
      workDefCacheProvider.invalidate();
      workDefCacheProvider.get();
   }

   @Override
   public void reloadAllCaches() throws OseeCoreException {
      reloadConfigCache();
      reloadWorkDefinitionCache();
      getUserService().clearCache();
   }

   @Override
   public void invalidateAllCaches() {
      invalidateConfigCache();
      invalidateWorkDefinitionCache();
      versionService.invalidateVersionCache();
   }

   @Override
   public void invalidateWorkDefinitionCache() {
      workDefCacheProvider.invalidate();
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name) throws OseeCoreException {
      return createTeamDefinition(GUID.create(), name, AtsUtilClient.createConfigObjectUuid());
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String guid, String name, long uuid) throws OseeCoreException {
      IAtsTeamDefinition item = teamDefFactory.createTeamDefinition(guid, name, uuid);
      AtsArtifactConfigCache cache = getConfigCache();
      cache.cache(item);
      return item;
   }

   @Override
   public IAtsActionableItem createActionableItem(String name) throws OseeCoreException {
      return createActionableItem(GUID.create(), name, AtsUtilClient.createConfigObjectUuid());
   }

   @Override
   public IAtsActionableItem createActionableItem(String guid, String name, long uuid) throws OseeCoreException {
      IAtsActionableItem item = actionableItemFactory.createActionableItem(guid, name, uuid);
      AtsArtifactConfigCache cache = getConfigCache();
      cache.cache(item);
      return item;
   }

   @Override
   public IAtsWorkDefinitionAdmin getWorkDefinitionAdmin() throws OseeStateException {
      return workDefAdmin;
   }

   @Override
   public IAtsConfig getConfig() throws OseeStateException {
      return configProxy;
   }

   @Override
   public IAtsClientVersionService getVersionService() throws OseeStateException {
      return versionService;
   }

   @Override
   public IAtsUserServiceClient getUserServiceClient() throws OseeStateException {
      return userServiceClient;
   }

   @Override
   public IAtsUserService getUserService() throws OseeStateException {
      return userService;
   }

   private AtsArtifactConfigCache getConfigCache() throws OseeCoreException {
      return configCacheProvider.get();
   }

   private final class AtsConfigProxy implements IAtsConfig {

      @Override
      public <A extends IAtsConfigObject> List<A> getByTag(String tag, Class<A> clazz) throws OseeCoreException {
         return getConfigCache().getByTag(tag, clazz);
      }

      @Override
      public <A extends IAtsConfigObject> A getSoleByTag(String tag, Class<A> clazz) throws OseeCoreException {
         return getConfigCache().getSoleByTag(tag, clazz);
      }

      @Override
      public <A extends IAtsConfigObject> List<A> get(Class<A> clazz) throws OseeCoreException {
         return getConfigCache().get(clazz);
      }

      @Override
      public <A extends IAtsConfigObject> A getSoleByGuid(String guid, Class<A> clazz) throws OseeCoreException {
         return getConfigCache().getSoleByGuid(guid, clazz);
      }

      @Override
      public IAtsConfigObject getSoleByGuid(String guid) throws OseeCoreException {
         return getConfigCache().getSoleByGuid(guid);
      }

      @Override
      public void getReport(XResultData rd) throws OseeCoreException {
         getConfigCache().getReport(rd);
      }

      @Override
      public void invalidate(IAtsConfigObject configObject) throws OseeCoreException {
         getConfigCache().invalidate(configObject);
      }

      @SuppressWarnings("unchecked")
      @Override
      public <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz) throws OseeCoreException {
         A object = getConfigCache().getSoleByUuid(uuid, clazz);
         if (object == null) {
            object = (A) getConfigItemFactory().getConfigObject(getArtifact(uuid));
            if (object != null) {
               getConfigCache().cache(object);
            }
         }
         return object;
      }

      @Override
      public IAtsConfigObject getSoleByUuid(long uuid) throws OseeCoreException {
         return getConfigCache().getSoleByUuid(uuid);
      }

      @Override
      public <A extends IAtsConfigObject> List<A> getById(long id, Class<A> clazz) {
         return getConfigCache().getById(id, clazz);
      }

   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(Object object) throws OseeCoreException {
      Artifact results = null;
      if (object instanceof Artifact) {
         results = (Artifact) object;
      } else if (object instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) object;
         if (atsObject.getStoreObject() != null) {
            results = (Artifact) atsObject.getStoreObject();
         } else {
            if (atsObject instanceof Artifact) {
               results = (Artifact) atsObject;
            } else {
               try {
                  results = AtsArtifactQuery.getArtifactFromId(atsObject.getGuid());
               } catch (ArtifactDoesNotExist ex) {
                  // do nothing
               }
            }
         }
      }
      return results;
   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(long uuid) throws OseeCoreException {
      Artifact result = null;
      try {
         result = ArtifactQuery.getArtifactFromId((int) uuid, AtsUtilCore.getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public IAtsWorkItemService getWorkItemService() throws OseeStateException {
      return workItemService;
   }

   @Override
   public IAtsEarnedValueService getEarnedValueService() throws OseeStateException {
      return earnedValueService;
   }

   @Override
   public IAtsWorkItemArtifactService getWorkItemArtifactService() throws OseeStateException {
      return workItemArtifactProvider;
   }

   @Override
   public IAtsBranchService getBranchService() throws OseeCoreException {
      return branchService;
   }

   @Override
   public AbstractWorkflowArtifact getWorkflowArtifact(IAtsObject atsObject) throws OseeCoreException {
      return (AbstractWorkflowArtifact) getArtifact(atsObject);
   }

   @Override
   public IAtsReviewService getReviewService() throws OseeCoreException {
      return reviewService;
   }

   @Override
   public IAttributeResolver getAttributeResolver() {
      return attributeResolverService;
   }

   @Override
   public ISequenceProvider getSequenceProvider() {
      return sequenceProvider;
   }

   @Override
   public IAtsStateFactory getStateFactory() {
      if (stateFactory == null) {
         stateFactory = AtsCoreFactory.newStateFactory(getServices(), getLogFactory());
      }
      return stateFactory;
   }

   @Override
   public IAtsWorkStateFactory getWorkStateFactory() {
      if (workStateFactory == null) {
         workStateFactory = AtsCoreFactory.getWorkStateFactory(getUserService());
      }
      return workStateFactory;
   }

   @Override
   public IAtsLogFactory getLogFactory() {
      if (logFactory == null) {
         logFactory = AtsCoreFactory.getLogFactory();
      }
      return logFactory;
   }

   @Override
   public IAtsColumnUtilities getColumnUtilities() {
      final IAtsEarnedValueService fEarnedValueService = earnedValueService;
      if (columnUtilities == null) {
         columnUtilities =
            AtsCoreFactory.getColumnUtilities(getReviewService(), getWorkItemService(),
               new IAtsEarnedValueServiceProvider() {

                  @Override
                  public IAtsEarnedValueService getEarnedValueService() throws OseeStateException {
                     return fEarnedValueService;
                  }
               });
      }
      return columnUtilities;
   }

   @Override
   public IAtsUtilService getUtilService() {
      return utilService;
   }

   @Override
   public void sendNotifications(final AtsNotificationCollector notifications) {
      if (AtsUtilClient.isEmailEnabled()) {
         Jobs.startJob(new Job("Send Notifications") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               AtsJaxRsService.get().getNotify().sendNotifications(notifications);
               return Status.OK_STATUS;
            }
         }, false);
      }
   }

   @Override
   public String getConfigValue(String key) {
      String result = null;
      Artifact atsConfig = ArtifactQuery.getArtifactFromToken(AtsArtifactToken.AtsConfig, AtsUtilCore.getAtsBranch());
      if (atsConfig != null) {
         for (Object obj : atsConfig.getAttributeValues(CoreAttributeTypes.GeneralStringData)) {
            String str = (String) obj;
            if (str.startsWith(key)) {
               result = str.replaceFirst(key + "=", "");
               break;
            }
         }
      }
      return result;
   }

   @Override
   public IAtsServices getServices() {
      return this;
   }

   @Override
   public IAtsWorkDefinitionService getWorkDefService() {
      return workDefService;
   }

   @Override
   public ChangeType getChangeType(IAtsAction fromAction) {
      return ChangeTypeUtil.getChangeType((Artifact) fromAction.getStoreObject());
   }

   @Override
   public String getAtsId(IAtsAction action) {
      return getAtsId(action);
   }

   @Override
   public Collection<IArtifactType> getArtifactTypes() {
      List<IArtifactType> types = new ArrayList<IArtifactType>();
      types.addAll(ArtifactTypeManager.getAllTypes());
      return types;
   }

   @Override
   public void setChangeType(IAtsObject atsObject, ChangeType changeType, IAtsChangeSet changes) {
      ChangeTypeUtil.setChangeType((Artifact) atsObject.getStoreObject(), changeType);
   }

   @Override
   public IAtsActionFactory getActionFactory() {
      return actionFactory;
   }

   @Override
   public IAtsConfigItemFactory getConfigItemFactory() {
      return configItemFactory;
   }

   @Override
   public IRelationResolver getRelationResolver() {
      return relationResolver;
   }

   @Override
   public IAtsProgramService getProgramService() {
      return programService;
   }

   @Override
   public IAtsTeamDefinitionService getTeamDefinitionService() {
      return teamDefinitionService;
   }

   @Override
   public Artifact getArtifact(String guid) {
      Artifact result = null;
      try {
         result = ArtifactQuery.getArtifactFromId(guid, AtsUtilCore.getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public IAtsQueryService getQueryService() {
      if (atsQueryService == null) {
         atsQueryService = new AtsQueryServiceIimpl(this);
      }
      return atsQueryService;
   }

   @Override
   public IAtsWorkItemFactory getWorkItemFactory() {
      return workItemFactory;
   }

   @Override
   public Artifact getArtifactById(String id) {
      Artifact result = null;
      if (GUID.isValid(id)) {
         result = getArtifactByGuid(id);
      }
      if (result == null && Strings.isNumeric(id)) {
         result = getArtifact(Long.valueOf(id));
      }
      if (result == null) {
         result = getArtifactByAtsId(id);
      }
      return result;
   }

   @Override
   public Artifact getArtifactByAtsId(String id) {
      Artifact result = null;
      try {
         result = ArtifactQuery.getArtifactFromAttribute(AtsAttributeTypes.AtsId, id, AtsUtilCore.getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public Artifact getArtifactByGuid(String guid) throws OseeCoreException {
      return getArtifact(guid);
   }

}
