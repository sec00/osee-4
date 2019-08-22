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
package org.eclipse.osee.ats.api.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStoreService {

   public static final String ART_TYPE_FROM_ID_QUERY =
      "select art_id, art_type_id from osee_artifact where art_id in (%s)";

   IAtsChangeSet createAtsChangeSet(String comment, IAtsUser user);

   List<IAtsWorkItem> reload(Collection<IAtsWorkItem> workItems);

   boolean isDeleted(IAtsObject atsObject);

   boolean isAttributeTypeValid(IAtsObject atsObject, AttributeTypeToken attributeType);

   boolean isAttributeTypeValid(ArtifactId artifact, AttributeTypeToken attributeType);

   /**
    * Uses artifact type inheritance to retrieve all TeamWorkflow artifact types
    */
   Set<ArtifactTypeToken> getTeamWorkflowArtifactTypes();

   AttributeTypeToken getAttributeType(String attrTypeName);

   ArtifactTypeToken getArtifactType(ArtifactId artifact);

   ArtifactTypeToken getArtifactType(IAtsObject atsObject);

   boolean isDateType(AttributeTypeId attributeType);

   boolean isOfType(ArtifactId artifact, ArtifactTypeId... artifactType);

   ArtifactTypeToken getArtifactType(Long artTypeId);

   void executeChangeSet(String comment, IAtsObject atsObject);

   void executeChangeSet(String comment, Collection<? extends IAtsObject> atsObjects);

   Collection<AttributeTypeToken> getAttributeTypes();

   boolean isChangedInDb(IAtsWorkItem workItem);

   boolean isOfType(IAtsObject atsObject, ArtifactTypeToken artifactType);

   void clearCaches(IAtsWorkItem workItem);

   AttributeTypeToken getAttributeType(Long attrTypeId);

   boolean isArtifactTypeInheritsFrom(ArtifactTypeToken artifactType, ArtifactTypeToken baseArtifactType);

   Result setTransactionAssociatedArtifact(TransactionId trans, IAtsTeamWorkflow teamWf);

   boolean isDeleted(ArtifactId artifact);

   TransactionId getTransactionId(IAtsWorkItem workItem);

   default boolean isInDb(IAtsWorkItem workItem) {
      return getTransactionId(workItem).isValid();
   }

   CustomizeData getCustomizationByGuid(String customize_guid);

   boolean isProductionDb();

   JdbcService getJdbcService();

   default Map<ArtifactId, ArtifactTypeToken> getArtifactTypes(Collection<ArtifactId> artIds) {
      String query = String.format(ART_TYPE_FROM_ID_QUERY,
         org.eclipse.osee.framework.jdk.core.util.Collections.toString(artIds, ",", ArtifactId::getIdString));

      Map<ArtifactId, ArtifactTypeToken> artIdToType = new HashMap<>();
      getJdbcService().getClient().runQuery(stmt -> artIdToType.put(ArtifactId.valueOf(stmt.getLong("art_id")),
         getArtifactType(stmt.getLong("art_type_id"))), query);
      return artIdToType;
   }

   boolean isOfType(IAtsObject atsObject, ArtifactTypeToken... artifactType);

   boolean inheritsFrom(ArtifactTypeToken artType, ArtifactTypeToken... artifactType);
}