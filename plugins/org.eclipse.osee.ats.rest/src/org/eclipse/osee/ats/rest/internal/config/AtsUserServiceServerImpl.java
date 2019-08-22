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
package org.eclipse.osee.ats.rest.internal.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.users.AbstractAtsUserService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsUserServiceServerImpl extends AbstractAtsUserService {

   private OrcsApi orcsApi;
   ArtifactReadable atsAdminArt;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public String getCurrentUserId() {
      return SystemUser.OseeSystem.getUserId();
   }

   @Override
   public boolean isAtsAdmin(IAtsUser user) {
      if (atsAdminArt == null) {
         atsAdminArt = getArtifact(AtsUserGroups.AtsAdmin);
      }
      return atsAdminArt.areRelated(CoreRelationTypes.Users_User, getArtifact((IAtsObject) user));
   }

   private ArtifactReadable getArtifact(IAtsObject atsObject) {
      if (atsObject.getStoreObject() instanceof ArtifactReadable) {
         return (ArtifactReadable) atsObject.getStoreObject();
      }
      return getArtifact(atsObject.getArtifactId());
   }

   private ArtifactReadable getArtifact(ArtifactId artifactId) {
      return getQuery().andId(artifactId).getResults().getExactlyOne();
   }

   private ArtifactReadable getArtifactOrSentinel(ArtifactId artifactId) {
      return getQuery().andId(artifactId).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
   }

   private QueryBuilder getQuery() {
      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
   }

   @Override
   public boolean isAtsAdmin(boolean useCache) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isAtsAdmin() {
      throw new UnsupportedOperationException();
   }

   private Supplier<List<IAtsUser>> usersCache =
      Suppliers.memoizeWithExpiration(getConfigurationsSupplier(), 5, TimeUnit.MINUTES);

   private Supplier<List<IAtsUser>> getConfigurationsSupplier() {
      return new Supplier<List<IAtsUser>>() {
         @Override
         public List<IAtsUser> get() {
            List<IAtsUser> users = new ArrayList<>();
            for (IAtsUser atsUser : getUsersFromDb()) {
               userIdToAtsUser.put(atsUser.getUserId(), atsUser);
               nameToAtsUser.put(atsUser.getName(), atsUser);
               users.add(atsUser);
            }
            return users;
         }
      };
   }

   private AtsUser createFromArtifact(ArtifactReadable userArt) {
      AtsUser atsUser = new AtsUser();
      atsUser.setName(userArt.getName());
      atsUser.setStoreObject(userArt);
      atsUser.setUserId(userArt.getSoleAttributeAsString(CoreAttributeTypes.UserId, ""));
      atsUser.setEmail(userArt.getSoleAttributeAsString(CoreAttributeTypes.Email, ""));
      atsUser.setActive(userArt.getSoleAttributeValue(CoreAttributeTypes.Active, true));
      atsUser.setId(userArt.getId());
      return atsUser;
   }

   @Override
   public List<IAtsUser> getUsers() {
      return usersCache.get();
   }

   @Override
   public List<IAtsUser> getUsersFromDb() {
      List<IAtsUser> users = new ArrayList<>();
      for (ArtifactId art : getQuery().andTypeEquals(CoreArtifactTypes.User).getResults()) {
         ArtifactReadable userArt = (ArtifactReadable) art;
         AtsUser atsUser = createFromArtifact(userArt);
         users.add(atsUser);
      }
      return users;
   }

   @Override
   public void reloadCache() {
      usersCache = Suppliers.memoizeWithExpiration(getConfigurationsSupplier(), 5, TimeUnit.MINUTES);
      super.reloadCache();
   }

   @Override
   protected IAtsUser loadUserFromDbByUserId(String userId) {
      ArtifactReadable userArt =
         getQuery().andTypeEquals(CoreArtifactTypes.User).andAttributeIs(CoreAttributeTypes.UserId,
            userId).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      if (userArt.isValid()) {
         return createFromArtifact(userArt);
      }
      return null;
   }

   @Override
   protected IAtsUser loadUserFromDbByUserName(String name) {
      ArtifactReadable userArt =
         getQuery().andTypeEquals(CoreArtifactTypes.User).andNameEquals(name).getResults().getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL);
      if (userArt.isValid()) {
         return createFromArtifact(userArt);
      }
      return null;
   }

   @Override
   protected IAtsUser loadUserByAccountId(Long accountId) {
      IAtsUser user = null;
      ArtifactId userArt = getArtifactOrSentinel(ArtifactId.valueOf(accountId));
      if (userArt.isValid()) {
         user = createFromArtifact((ArtifactReadable) userArt);
      }
      return user;
   }

   @Override
   public IAtsUser getUserByArtifactId(ArtifactId id) {
      ArtifactReadable userArt = getQuery().andId(id).getResults().getExactlyOne();
      return createFromArtifact(userArt);
   }
      if (Strings.isInValid(accountId)) {
         return null;
      }
}