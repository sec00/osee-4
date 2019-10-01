/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public abstract class Attribute<T> implements Comparable<Attribute<T>>, IAttribute<T> {
   private WeakReference<Artifact> artifactRef;
   private IAttributeDataProvider attributeDataProvider;
   private AttributeId attrId = AttributeId.SENTINEL;
   private GammaId gammaId = GammaId.SENTINEL;
   private boolean dirty;
   private ModificationType modificationType;
   private boolean useBackingData;
   private AttributeTypeId attributeTypeToken;
   private ApplicabilityId applicabilityId;

   void internalInitialize(AttributeTypeId attributeType, Artifact artifact, ModificationType modificationType, ApplicabilityId applicabilityId, boolean markDirty, boolean setDefaultValue) {
      this.attributeTypeToken = attributeType;
      this.artifactRef = new WeakReference<>(artifact);
      internalSetModType(modificationType, false, markDirty);
      if (applicabilityId == null) {
         internalSetApplicabilityId(ApplicabilityId.BASE);
      } else {
         internalSetApplicabilityId(applicabilityId);
      }
      try {
         Class<? extends IAttributeDataProvider> providerClass =
            AttributeTypeManager.getAttributeProviderClass(AttributeTypeManager.getType(attributeTypeToken));
         Constructor<? extends IAttributeDataProvider> providerConstructor =
            providerClass.getConstructor(Attribute.class);
         attributeDataProvider = providerConstructor.newInstance(this);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      if (setDefaultValue) {
         setToDefaultValue();
      }
      uponInitialize();
   }

   private void internalSetApplicabilityId(ApplicabilityId applicabilityId) {
      this.applicabilityId = applicabilityId;
   }

   /**
    * Base implementation does nothing. Subclasses may override to do setup that depends on the attribute state data.
    */
   protected void uponInitialize() {
      // provided for subclass implementation
   }

   public AttributeChange createAttributeChangeFromSelf() {
      AttributeChange attributeChange = new AttributeChange();

      attributeChange.setAttrTypeGuid(getAttributeType().getId());
      attributeChange.setGammaId(gammaId);
      attributeChange.setAttributeId(attrId.getId().intValue());
      attributeChange.setModTypeGuid(AttributeEventModificationType.getType(modificationType).getGuid());
      for (Object obj : attributeDataProvider.getData()) {
         if (obj == null) {
            attributeChange.getData().add("");
         } else {
            attributeChange.getData().add(obj);
         }
      }

      return attributeChange;
   }

   public void internalInitialize(AttributeTypeId attributeType, Artifact artifact, ModificationType modificationType, ApplicabilityId applicabilityId, AttributeId attributeId, GammaId gammaId, boolean markDirty, boolean setDefaultValue) {
      internalInitialize(attributeType, artifact, modificationType, applicabilityId, markDirty, setDefaultValue);
      this.attrId = attributeId;
      this.gammaId = gammaId;
   }

   protected void markAsNewOrChanged() {
      if (isInDb()) {
         internalSetModType(ModificationType.MODIFIED, false, true);
      } else {
         internalSetModType(ModificationType.NEW, false, true);
      }
   }

   public boolean setValue(T value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute id [%s]", getId());
      checkIsRenameable(value);
      boolean response = subClassSetValue(value);
      if (response) {
         markAsNewOrChanged();
      }
      return response;
   }

   protected boolean setFromStringNoDirty(String value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute id [%s]", getId());
      return subClassSetValue(convertStringToValue(value));
   }

   public boolean setFromString(String value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute id [%s]", getId());
      return setValue(convertStringToValue(value));
   }

   private void checkIsRenameable(T value) {
      if (getAttributeType().equals(CoreAttributeTypes.Name) && !value.equals(getValue())) {
         // Confirm artifact is fit to rename
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            IStatus result = check.isRenamable(Arrays.asList(getArtifact()));
            if (!result.isOK()) {
               throw new OseeCoreException(result.getMessage());
            }
         }
      }
   }

   /**
    * @param value will be non-null
    */
   public abstract T convertStringToValue(String value);

   public String convertToStorageString(T rawValue) {
      return rawValue == null ? null : rawValue.toString();
   }

   public final void resetToDefaultValue() {
      setToDefaultValue();
   }

   protected void setToDefaultValue() {
      String defaultValue = getAttributeType().getDefaultValue();
      if (defaultValue != null) {
         setFromStringNoDirty(defaultValue);
      }
   }

   public boolean setValueFromInputStream(InputStream value) {
      try {
         boolean response = setFromString(Lib.inputStreamToString(value));
         if (response) {
            markAsNewOrChanged();
         }
         return response;
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   /**
    * Subclasses must provide an implementation of this method and in general should not override the other set value
    * methods. The value parameter will be non-null
    */
   protected abstract boolean subClassSetValue(T value);

   public String getDisplayableString() {
      return getAttributeDataProvider().getDisplayableString();
   }

   @Override
   public String toString() {
      try {
         return getDisplayableString();
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   public IAttributeDataProvider getAttributeDataProvider() {
      return attributeDataProvider;
   }

   public boolean isDirty() {
      return dirty;
   }

   public void setNotDirty() {
      setDirtyFlag(false);
   }

   private void setDirtyFlag(boolean dirty) {
      this.dirty = dirty;
      ArtifactCache.updateCachedArtifact(getArtifact());
   }

   public Artifact getArtifact() {
      if (artifactRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return artifactRef.get();
   }

   /**
    * @return the attribute name/value description
    */
   public String getNameValueDescription() {
      return getAttributeType().getName() + ": " + toString();
   }

   /**
    * @return attributeType Attribute Type Information
    */
   @Override
   public AttributeType getAttributeType() {
      return AttributeTypeManager.getType(attributeTypeToken);
   }

   public AttributeTypeId getAttributeTypeToken() {
      return attributeTypeToken;
   }

   /**
    * Currently this method provides support for quasi attribute type inheritance
    *
    * @return whether this attribute's type or any of its super-types are the specified type
    */
   public boolean isOfType(AttributeTypeId otherAttributeType) {
      return getAttributeType().equals(otherAttributeType);
   }

   public void resetModType() {
      internalSetModType(ModificationType.MODIFIED, false, true);
   }

   /**
    * Deletes the attribute
    */
   public final void setArtifactDeleted() {
      internalSetModType(ModificationType.ARTIFACT_DELETED, true, true);
   }

   /**
    * Deletes the attribute
    */
   public final void delete() {
      if (isInDb()) {
         internalSetModType(ModificationType.DELETED, true, true);
      } else {
         getArtifact().deleteAttribute(this);
      }
   }

   public ModificationType getModificationType() {
      return modificationType;
   }

   public boolean canDelete() {
      try {
         AttributeType attributeType = getAttributeType();
         return getArtifact().getAttributeCount(attributeType) > attributeType.getMinOccurrences();
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   /**
    * Purges attribute binary data
    */
   void purge() {
      getAttributeDataProvider().purge();
   }

   public void markAsPurged() {
      internalSetModType(ModificationType.DELETED, false, false);
   }

   /**
    * @return true if in data store
    */
   public boolean isInDb() {
      return getGammaId().isValid();
   }

   @Override
   public Long getId() {
      return attrId.getId();
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public ApplicabilityId getApplicabilityId() {
      return applicabilityId;
   }

   public void internalSetGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public void internalSetAttributeId(AttributeId attrId) {
      this.attrId = attrId;
   }

   public boolean isDeleted() {
      try {
         return modificationType.isDeleted();
      } catch (NullPointerException ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex,
            "Unexpected null modification type for artifact attribute [%d] gamma [%d] on artifact [%s]", getId(),
            getGammaId(), getArtifact().getSafeName());
      }
      return false;
   }

   /**
    * artifact.persist(); artifact.reloadAttributesAndRelations(); Will need to be called afterwards to see replaced
    * data in memory
    */
   public void replaceWithVersion(GammaId gammaId) {
      internalSetPersistenceData(gammaId, ModificationType.REPLACED_WITH_VERSION);
   }

   private void internalSetPersistenceData(GammaId gammaId, ModificationType modType) {
      internalSetModType(modType, true, true);
      internalSetGammaId(gammaId);
   }

   public void introduce(Attribute<?> sourceAttr) {
      internalSetPersistenceData(sourceAttr.getGammaId(), sourceAttr.getModificationType());
   }

   public boolean isUseBackingData() {
      return useBackingData;
   }

   public void internalSetModType(ModificationType modificationType, boolean useBackingData, boolean dirty) {
      this.modificationType = modificationType;
      this.useBackingData = useBackingData;
      setDirtyFlag(dirty);

   }

   @Override
   public int compareTo(Attribute<T> other) {
      return toString().compareTo(other.toString());
   }

}
