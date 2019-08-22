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
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;

/**
 * @author Donald G. Dunne
 */
public interface IAttributeResolver {

   boolean isAttributeNamed(String attributeName);

   String getUnqualifiedName(String attributeName);

   void setXWidgetNameBasedOnAttributeName(String attributeName, IAtsWidgetDefinition widgetDef);

   String getDescription(String attributeName);

   <T> T getSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken<T> attributeType, T defaultReturnValue);

   List<String> getAttributesToStringList(IAtsObject atsObject, AttributeTypeToken attributeType);

   boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeToken<?> attributeType);

   String getSoleAttributeValueAsString(IAtsObject atsObject, AttributeTypeToken<?> attributeType, String defaultReturnValue);

   int getAttributeCount(IAtsObject atsObject, AttributeTypeToken<?> attributeType);

   int getAttributeCount(ArtifactId artifact, AttributeTypeToken<?> attributeType);

   void addAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value);

   <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact);

   <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem);

   <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem, AttributeTypeToken<T> attributeType);

   <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact, AttributeTypeToken<T> attributeType);

   void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType);

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr);

   <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, AttributeTypeId attributeType, T value);

   void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, IAtsChangeSet changes);

   void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes);

   void addAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes);

   void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes);

   <T> void setValue(IAtsWorkItem workItem, IAttribute<T> attr, AttributeTypeId attributeType, T value, IAtsChangeSet changes);

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr, IAtsChangeSet changes);

   AttributeTypeToken getAttributeType(String atrributeName);

   void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value);

   <T> T getSoleAttributeValue(ArtifactId artifact, AttributeTypeToken<T> attributeType, T defaultValue);

   <T> Collection<T> getAttributeValues(ArtifactId artifact, AttributeTypeToken attributeType);

   <T> Collection<T> getAttributeValues(IAtsObject atsObject, AttributeTypeToken attributeType);

   String getSoleAttributeValueAsString(ArtifactId artifact, AttributeTypeToken<?> attributeType, String defaultReturnValue);

   int getAttributeCount(IAtsWorkItem workItem, AttributeTypeToken attributeType);

   default public String getAttributesToStringUniqueList(IAtsObject atsObject, AttributeTypeToken attributeType, String separator) {
      Set<String> strs = new HashSet<>();
      strs.addAll(getAttributesToStringList(atsObject, attributeType));
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(separator, strs);
   }

   List<String> getAttributesToStringList(ArtifactId customizeStoreArt, AttributeTypeToken attributeType);

   ArtifactId getSoleArtifactIdReference(IAtsObject atsObject, AttributeTypeToken artifactReferencedAttributeType, ArtifactId defaultValue);

   ArtifactId getSoleArtifactIdReference(ArtifactToken art, AttributeTypeToken artifactReferencedAttributeType, ArtifactId defaultValue);

   Collection<ArtifactId> getArtifactIdReferences(ArtifactToken artifact, AttributeTypeToken artifactReferencedAttributeType);

}