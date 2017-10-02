/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.intergration;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.UserGroups;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.OseeTypeDefinition;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UriGeneralStringData;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Parent;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.integrationRule;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.sort;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.verifyData;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.RelationReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Roberto E. Escobar
 */
public class LoaderTest {

   @Rule
   public TestRule db = integrationRule(this);

   @OsgiService
   public OrcsApi orcsApi;
   @OsgiService
   public JdbcService jdbcService;

   // @formatter:off
   @OsgiService private OrcsDataStore dataStore;
   @Mock private LoadDataHandler builder;
   @Captor private ArgumentCaptor<LoadDescription> descriptorCaptor;
   @Captor private ArgumentCaptor<ArtifactData> artifactCaptor;
   @Captor private ArgumentCaptor<AttributeData> attributeCaptor;
   @Captor private ArgumentCaptor<RelationData> relationCaptor;
   @Mock private OrcsSession session;
   // @formatter:on

   private static final ArtifactToken AtsAdminToken =
      TokenFactory.createArtifactToken(136750, "asdf", "AtsAdmin", CoreArtifactTypes.UserGroup);
   private static final ArtifactToken AtsTempAdminToken =
      TokenFactory.createArtifactToken(5367074, "qwerty", "AtsTempAdmin", CoreArtifactTypes.UserGroup);
   private HasCancellation cancellation;
   private DataLoaderFactory loaderFactory;
   private static ArtifactReadable OseeTypesFrameworkArt;
   private static AttributeId OseeTypesFrameworkNameAttrId;
   private static AttributeId OseeTypesFrameworkActiveAttrId;
   private static GammaId OseeTypesFrameworkActiveGammaId;
   private static GammaId OseeTypesFrameworkActiveGammaIdPlus1;
   private static GammaId OseeTypesFrameworkActiveGammaIdPlus2;
   private static GammaId OseeTypesFrameworkNameGammaId;
   private static String OseeTypesFrameworkGuid;
   private static ArtifactReadable OseeTypesClientDemoArt;
   private static AttributeId OseeTypesClientDemoActiveAttrId;
   private static AttributeId OseeTypesClientDemoActiveAttrIdPlus1;
   private static AttributeId OseeTypesClientDemoActiveAttrIdPlus2;
   private static AttributeId OseeTypesClientDemoNameAttrId;
   private static GammaId OseeTypesClientDemoActiveGammaId;
   private static GammaId OseeTypesClientDemoActiveGammaIdPlus1;
   private static GammaId OseeTypesClientDemoActiveGammaIdPlus2;
   private static GammaId OseeTypesClientDemoNameGammaId;
   private static String OseeTypesClientDemoGuid;
   private static final GammaId UserGroupsArtifactGammaId = GammaId.valueOf(43);
   private static final GammaId OseeTypesClientDemoGammaId = GammaId.valueOf(11);
   private static final GammaId OseeTypesFrameworkGammaId = GammaId.valueOf(8);
   private static AttributeId UserGroupsNameAttrId;
   private static GammaId UserGroupsNameGammaId;
   private static final Map<ArtifactToken, Integer> artTokenToRelationId = new HashMap<>();
   private static final Map<ArtifactToken, GammaId> artTokenToRelationGammaId = new HashMap<>();
   private final String UserGroupsGuid = CoreArtifactTokens.UserGroups.getGuid();
   private static final List<ArtifactToken> relationsArts =
      Arrays.asList(CoreArtifactTokens.Everyone, CoreArtifactTokens.DefaultHierarchyRoot, CoreArtifactTokens.OseeAdmin,
         CoreArtifactTokens.OseeAccessAdmin, AtsTempAdminToken, AtsAdminToken);
   private static GammaId defaultHierRootToUserGroupsRelationGammaId;
   private static GammaId userGroupsToOseeAdminRelationGammaId;
   // Transaction that OseeTypes_ClientDemo and OseeTypes_Framework were created in
   private final TransactionId tx5 = TransactionId.valueOf(5);
   // Transaction that User Groups was created in
   private final TransactionId tx7 = TransactionId.valueOf(7);
   // Transaction that AtsAdmin, AtsTempAdmin and OseeAccessAdmin were created in and related to User Groups
   private final TransactionId tx10 = TransactionId.valueOf(10);
   private static ArtifactId OseeTypesClientDemoId;
   private static ArtifactId OseeTypesFrameworkId;
   private static Collection<ArtifactId> artifactIds;

   public void setUp()  {
      JdbcClient jdbcClient = jdbcService.getClient();
      if (jdbcClient.getConfig().isProduction()) {
         throw new OseeStateException("Test should not be run against a Production Database");
      }

      if (OseeTypesFrameworkArt == null) {
         for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            CoreArtifactTypes.OseeTypeDefinition).getResults()) {
            if (art.getName().contains("Framework")) {
               OseeTypesFrameworkId = art;
               OseeTypesFrameworkGuid = art.getGuid();
               OseeTypesFrameworkArt = art;
               for (AttributeReadable<Object> attr : art.getAttributes()) {
                  if (attr.isOfType(CoreAttributeTypes.Active)) {
                     OseeTypesFrameworkActiveAttrId = attr;
                     OseeTypesFrameworkActiveGammaId = GammaId.valueOf(attr.getGammaId());
                     OseeTypesFrameworkActiveGammaIdPlus1 = GammaId.valueOf(attr.getGammaId() + 1);
                     OseeTypesFrameworkActiveGammaIdPlus2 = GammaId.valueOf(attr.getGammaId() + 2);
                  } else if (attr.isOfType(CoreAttributeTypes.Name)) {
                     OseeTypesFrameworkNameAttrId = attr;
                     OseeTypesFrameworkNameGammaId = GammaId.valueOf(attr.getGammaId());
                  }
               }
            } else if (art.getName().contains("OseeTypes_ClientDemo")) {
               OseeTypesClientDemoId = art;
               OseeTypesClientDemoGuid = art.getGuid();
               OseeTypesClientDemoArt = art;
               for (AttributeReadable<Object> attr : art.getAttributes()) {
                  if (attr.isOfType(CoreAttributeTypes.Active)) {
                     OseeTypesClientDemoActiveAttrId = attr;
                     OseeTypesClientDemoActiveAttrIdPlus1 =
                        AttributeId.valueOf(OseeTypesClientDemoActiveAttrId.getId() + 1);
                     OseeTypesClientDemoActiveAttrIdPlus2 =
                        AttributeId.valueOf(OseeTypesClientDemoActiveAttrId.getId() + 2);
                     OseeTypesClientDemoActiveGammaId = GammaId.valueOf(attr.getGammaId());
                     OseeTypesClientDemoActiveGammaIdPlus1 = GammaId.valueOf(attr.getGammaId() + 1);
                     OseeTypesClientDemoActiveGammaIdPlus2 = GammaId.valueOf(attr.getGammaId() + 2);
                  } else if (attr.isOfType(CoreAttributeTypes.Name)) {
                     OseeTypesClientDemoNameAttrId = attr;
                     OseeTypesClientDemoNameGammaId = GammaId.valueOf(attr.getGammaId());
                  }
               }
            }
         }

         ArtifactReadable userGroupFolder = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            CoreArtifactTokens.UserGroups).getResults().getExactlyOne();
         for (AttributeReadable<Object> attr : userGroupFolder.getAttributes()) {
            if (attr.isOfType(CoreAttributeTypes.Name)) {
               UserGroupsNameAttrId = attr;
               UserGroupsNameGammaId = GammaId.valueOf(attr.getGammaId());
            }
         }
         ArtifactReadable defaultHierRoot = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            CoreArtifactTokens.DefaultHierarchyRoot).getResults().getExactlyOne();
         for (RelationReadable relation : defaultHierRoot.getRelations(CoreRelationTypes.Default_Hierarchical__Child)) {
            if (relation.getArtIdB() == CoreArtifactTokens.UserGroups.getId().intValue()) {
               defaultHierRootToUserGroupsRelationGammaId = GammaId.valueOf(relation.getGammaId());
               break;
            }
         }
         for (RelationReadable rel : userGroupFolder.getRelations(CoreRelationTypes.Default_Hierarchical__Child)) {
            for (ArtifactToken token : relationsArts) {
               if (rel.getArtIdB() == token.getId().intValue() || rel.getArtIdA() == token.getId().intValue()) {
                  artTokenToRelationId.put(token, rel.getId().intValue());
                  artTokenToRelationGammaId.put(token, GammaId.valueOf(rel.getGammaId()));
               }
            }
            if (rel.getArtIdB() == CoreArtifactTokens.OseeAdmin.getId().intValue()) {
               userGroupsToOseeAdminRelationGammaId = GammaId.valueOf(rel.getGammaId());
            }
         }
         Assert.assertEquals(6, relationsArts.size());
      }

      artifactIds = Arrays.asList(OseeTypesFrameworkId, OseeTypesClientDemoId, UserGroups);

      MockitoAnnotations.initMocks(this);
      loaderFactory = dataStore.createDataModule(orcsApi.getOrcsTypes()).getDataLoaderFactory();

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);
   }

   /**
    * Only need one copy of the database for all 4 tests.
    */
   @org.junit.Test
   public void testAll()  {
      setUp();
      testLoad();
      setUp();
      testLoadByTypes();
      setUp();
      testLoadByIds();
      setUp();
      testLoadByGuids();
   }

   public void testLoad()  {
      DataLoader loader = loaderFactory.newDataLoader(session, COMMON, artifactIds);
      loader.withLoadLevel(LoadLevel.ALL);
      verifyArtsAttrAndRelData(loader);
   }

   private void verifyArtsAttrAndRelData(DataLoader loader) {

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(7)).onData(attributeCaptor.capture());
      verify(builder, times(6)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      verifyArts(arts);

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      verifyData(attrs.next(), OseeTypesFrameworkActiveAttrId, OseeTypesFrameworkId, NEW, Active.getId(), COMMON, tx5,
         OseeTypesFrameworkActiveGammaId, true, "");
      verifyData(attrs.next(), AttributeId.valueOf(OseeTypesFrameworkActiveAttrId.getId() + 1), OseeTypesFrameworkId,
         NEW, Name.getId(), COMMON, tx5, OseeTypesFrameworkActiveGammaIdPlus1,
         "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
      verifyData(attrs.next(), AttributeId.valueOf(OseeTypesFrameworkActiveAttrId.getId() + 2), OseeTypesFrameworkId,
         NEW, UriGeneralStringData.getId(), COMMON, tx5, OseeTypesFrameworkActiveGammaIdPlus2, "",
         "attr://" + OseeTypesFrameworkActiveGammaIdPlus2.getIdString() + "/" + OseeTypesFrameworkGuid + ".zip");

      verifyData(attrs.next(), OseeTypesClientDemoActiveAttrId, OseeTypesClientDemoId, NEW, Active.getId(), COMMON, tx5,
         OseeTypesClientDemoActiveGammaId, true, "");
      verifyData(attrs.next(), OseeTypesClientDemoActiveAttrIdPlus1, OseeTypesClientDemoId, NEW, Name.getId(), COMMON,
         tx5, OseeTypesClientDemoActiveGammaIdPlus1, "org.eclipse.osee.client.demo.OseeTypes_ClientDemo", "");
      verifyData(attrs.next(), OseeTypesClientDemoActiveAttrIdPlus2, OseeTypesClientDemoId, NEW,
         UriGeneralStringData.getId(), COMMON, tx5, OseeTypesClientDemoActiveGammaIdPlus2, "",
         "attr://" + OseeTypesClientDemoActiveGammaIdPlus2.getIdString() + "/" + OseeTypesClientDemoGuid + ".zip");

      verifyData(attrs.next(), UserGroupsNameAttrId, UserGroups, NEW, Name.getId(), COMMON, tx7, UserGroupsNameGammaId,
         "User Groups", "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyRels(rels);
   }

   private void verifyRels(Iterator<RelationData> rels) {
      verifyData(rels.next(), artTokenToRelationId.get(CoreArtifactTokens.Everyone), UserGroups,
         CoreArtifactTokens.Everyone, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         artTokenToRelationGammaId.get(CoreArtifactTokens.Everyone));
      verifyData(rels.next(), artTokenToRelationId.get(CoreArtifactTokens.DefaultHierarchyRoot),
         CoreArtifactTokens.DefaultHierarchyRoot, UserGroups, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON,
         tx7, artTokenToRelationGammaId.get(CoreArtifactTokens.DefaultHierarchyRoot));
      verifyData(rels.next(), artTokenToRelationId.get(CoreArtifactTokens.OseeAdmin), UserGroups,
         CoreArtifactTokens.OseeAdmin, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         artTokenToRelationGammaId.get(CoreArtifactTokens.OseeAdmin));
      verifyData(rels.next(), artTokenToRelationId.get(CoreArtifactTokens.OseeAccessAdmin), UserGroups,
         CoreArtifactTokens.OseeAccessAdmin, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx10,
         artTokenToRelationGammaId.get(CoreArtifactTokens.OseeAccessAdmin));
      verifyData(rels.next(), artTokenToRelationId.get(AtsAdminToken), UserGroups, AtsAdminToken, "", NEW,
         Default_Hierarchical__Parent.getGuid(), COMMON, tx10, artTokenToRelationGammaId.get(AtsAdminToken));
      verifyData(rels.next(), artTokenToRelationId.get(AtsTempAdminToken), UserGroups, AtsTempAdminToken, "", NEW,
         Default_Hierarchical__Parent.getGuid(), COMMON, tx10, artTokenToRelationGammaId.get(AtsTempAdminToken));
   }

   private void verifyArts(Iterator<ArtifactData> arts) {
      verifyData(arts.next(), UserGroups, UserGroupsGuid, NEW, Folder.getId(), COMMON, tx7, UserGroupsArtifactGammaId);
      verifyData(arts.next(), OseeTypesClientDemoId, OseeTypesClientDemoGuid, NEW, OseeTypeDefinition.getId(), COMMON,
         tx5, OseeTypesClientDemoGammaId);
      verifyData(arts.next(), OseeTypesFrameworkId, OseeTypesFrameworkGuid, NEW, OseeTypeDefinition.getId(), COMMON,
         tx5, OseeTypesFrameworkGammaId);
   }

   public void testLoadByTypes()  {
      DataLoader loader = loaderFactory.newDataLoader(session, COMMON, artifactIds);
      loader.withLoadLevel(LoadLevel.ALL);

      loader.withAttributeTypes(Name);
      loader.withRelationTypes(Default_Hierarchical__Parent);

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(3)).onData(attributeCaptor.capture());
      verify(builder, times(6)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      verifyArts(arts);

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      verifyData(attrs.next(), OseeTypesFrameworkNameAttrId, OseeTypesFrameworkId, NEW, Name.getId(), COMMON, tx5,
         OseeTypesFrameworkNameGammaId, "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
      verifyData(attrs.next(), OseeTypesClientDemoNameAttrId, OseeTypesClientDemoId, NEW, Name.getId(), COMMON, tx5,
         OseeTypesClientDemoNameGammaId, "org.eclipse.osee.client.demo.OseeTypes_ClientDemo", "");
      verifyData(attrs.next(), UserGroupsNameAttrId, UserGroups, NEW, Name.getId(), COMMON, tx7, UserGroupsNameGammaId,
         "User Groups", "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyRels(rels);
   }

   public void testLoadByIds()  {
      DataLoader loader = loaderFactory.newDataLoader(session, COMMON, artifactIds);
      loader.withLoadLevel(LoadLevel.ALL);

      List<Integer> activeAttrIds = new LinkedList<>();
      AttributeReadable<Object> frameworkActiveAttr = getActiveAttr(OseeTypesFrameworkArt);
      activeAttrIds.add(frameworkActiveAttr.getId().intValue());
      AttributeReadable<Object> clientDemoActiveAttr = getActiveAttr(OseeTypesClientDemoArt);
      activeAttrIds.add(clientDemoActiveAttr.getId().intValue());
      loader.withAttributeIds(activeAttrIds);

      loader.withRelationIds(2, 3);

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(2)).onData(attributeCaptor.capture());
      verify(builder, times(2)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      verifyArts(arts);

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      verifyData(attrs.next(), frameworkActiveAttr, OseeTypesFrameworkId, NEW, Active.getId(), COMMON, tx5,
         GammaId.valueOf(frameworkActiveAttr.getGammaId()), true, "");
      verifyData(attrs.next(), clientDemoActiveAttr, OseeTypesClientDemoId, NEW, Active.getId(), COMMON, tx5,
         GammaId.valueOf(clientDemoActiveAttr.getGammaId()), true, "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyData(rels.next(), 2, CoreArtifactTokens.DefaultHierarchyRoot, UserGroups, "", NEW,
         Default_Hierarchical__Parent.getGuid(), COMMON, tx7, defaultHierRootToUserGroupsRelationGammaId);
      verifyData(rels.next(), 3, UserGroups, CoreArtifactTokens.OseeAdmin, "", NEW,
         Default_Hierarchical__Parent.getGuid(), COMMON, tx7, userGroupsToOseeAdminRelationGammaId);
   }

   private AttributeReadable<Object> getActiveAttr(ArtifactReadable artifact) {
      for (AttributeReadable<Object> attr : artifact.getAttributes()) {
         if (attr.getAttributeType().equals(Active)) {
            return attr;
         }
      }
      return null;
   }

   public void testLoadByGuids()  {
      String[] ids = new String[] {OseeTypesFrameworkGuid, OseeTypesClientDemoGuid, UserGroupsGuid};
      DataLoader loader = loaderFactory.newDataLoaderFromGuids(session, COMMON, ids);
      loader.withLoadLevel(LoadLevel.ALL);
      verifyArtsAttrAndRelData(loader);
   }
}
