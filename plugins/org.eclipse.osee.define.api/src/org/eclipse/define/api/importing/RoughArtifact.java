/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.define.api.importing;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @see RoughArtifactTest
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public class RoughArtifact {
   private RoughArtifact roughParent;
   private ReqNumbering number;
   private String guid;
   private RoughArtifactKind roughArtifactKind;
   private final RoughAttributeSet attributes;
   private final Collection<RoughArtifact> children;
   private ArtifactTypeToken primaryArtifactType;
   private ArtifactTypeToken type = ArtifactTypeToken.SENTINEL;
   private final XResultData results;
   private final OrcsApi orcsApi;

   public RoughArtifact(OrcsApi orcsApi, XResultData results, RoughArtifactKind roughArtifactKind, String name) {
      this.attributes = new RoughAttributeSet();
      this.children = new ArrayList<>();
      this.roughArtifactKind = roughArtifactKind;
      this.results = results;
      this.orcsApi = orcsApi;
      setName(name);
   }

   public RoughArtifact(OrcsApi orcsApi, XResultData results, ArtifactTypeToken type, String name) {
      this.attributes = new RoughAttributeSet();
      this.children = new ArrayList<>();
      this.roughArtifactKind = RoughArtifactKind.TYPESET;
      this.type = type;
      this.results = results;
      this.orcsApi = orcsApi;
      setName(name);
   }

   public RoughArtifact(OrcsApi orcsApi, XResultData results, RoughArtifactKind roughArtifactKind) {
      this(orcsApi, results, roughArtifactKind, "unnamed");
   }

   public void setName(String name) {
      attributes.setAttribute(CoreAttributeTypes.Name.getName(), name);
   }

   public void clear() {
      this.attributes.clear();
      this.children.clear();
      guid = null;
      number = null;
      roughParent = null;
      primaryArtifactType = null;
   }

   public Set<String> getAttributeTypeNames() {
      return attributes.getAttributeTypeNames();
   }

   public boolean hasHierarchicalRelation() {
      return number != null;
   }

   public void addChild(RoughArtifact child) {
      child.roughParent = this;
      children.add(child);
   }

   public boolean hasParent() {
      return roughParent != null;
   }

   public RoughArtifact getRoughParent() {
      return roughParent;
   }

   public void addAttribute(AttributeTypeToken attrType, String value) {
      addAttribute(attrType.getName(), value);
   }

   /**
    * This method will set as a singleton collection the value for the typeName, over writing whatever was there
    */
   public void setAttribute(String typeName, String value) {
      attributes.setAttribute(typeName, value);
   }

   public void addAttribute(String typeName, String value) {
      if (isEnumeration(typeName)) {
         if (isMultipleEnum(typeName, value)) {
            attributes.addAttribute(typeName, getEnumValues(value));
         } else {
            attributes.addAttribute(typeName, value.trim());
         }
      } else {
         attributes.addAttribute(typeName, value);
      }
   }

   public void addAttribute(String name, URI uri) {
      attributes.addAttribute(name, uri);
   }

   public void addAttribute(AttributeTypeToken attributeType, URI uri) {
      addAttribute(attributeType.getName(), uri);
   }

   public Collection<URI> getURIAttributes() {
      return attributes.getURIAttributes();
   }

   private String[] getEnumValues(String value) {
      String[] data = value.split(",");
      for (int index = 0; index < data.length; index++) {
         data[index] = data[index].trim();
      }
      return data;
   }

   private boolean isEnumeration(String typeName) {
      boolean result = false;
      AttributeTypes attributeTypes = orcsApi.getOrcsTypes().getAttributeTypes();
      AttributeTypeToken type = attributeTypes.getByName(typeName);
      result = attributeTypes.isEnumerated(type);
      return result;
   }

   private boolean isMultipleEnum(String typeName, String value) {
      boolean result = false;
      try {
         AttributeTypes attributeTypes = orcsApi.getOrcsTypes().getAttributeTypes();
         AttributeTypeToken type = attributeTypes.getByName(typeName);
         if (attributeTypes.isEnumerated(type) && attributeTypes.getMaxOccurrences(type) > 1 && value.contains(",")) {
            result = true;
         }
      } catch (OseeCoreException ex) {
         results.errorf(ex.toString());
      }
      return result;
   }

   public boolean isChild(RoughArtifact otherArtifact) {
      return number.isChild(otherArtifact.number);
   }

   @Override
   public String toString() {
      return getName();
   }

   public void setSectionNumber(String number) {
      this.number = new ReqNumbering(number);
   }

   public ReqNumbering getSectionNumber() {
      return this.number;
   }

   public RoughAttributeSet getAttributes() {
      return attributes;
   }

   public Collection<RoughArtifact> getChildren() {
      return children;
   }

   public Collection<RoughArtifact> getDescendants() {
      Collection<RoughArtifact> decendants = new ArrayList<>();
      for (RoughArtifact child : getChildren()) {
         if (equals(child.roughParent)) {
            decendants.add(child);
         }
      }
      return decendants;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public RoughArtifactKind getRoughArtifactKind() {
      return roughArtifactKind;
   }

   public void setRoughArtifactKind(RoughArtifactKind roughArtifactKind) {
      this.roughArtifactKind = roughArtifactKind;
   }

   public String getName() {
      return attributes.getSoleAttributeValue(CoreAttributeTypes.Name.getName());
   }

   public String getRoughAttribute(String attributeName) {
      return attributes.getSoleAttributeValue(attributeName);
   }

   public ArtifactTypeToken getPrimaryArtifactType() {
      return primaryArtifactType;
   }

   public void setPrimaryArtifactType(ArtifactTypeToken primaryArtifactType) {
      this.primaryArtifactType = primaryArtifactType;
   }

   public ArtifactTypeToken getType() {
      return type;
   }

   public void setType(ArtifactTypeToken type) {
      this.type = type;
   }

   public XResultData getResults() {
      return this.results;
   }

   public OrcsApi getOrcsApi() {
      return this.orcsApi;
   }
}