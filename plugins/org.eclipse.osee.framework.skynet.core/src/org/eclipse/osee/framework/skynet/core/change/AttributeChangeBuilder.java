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
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;

/**
 * @author Jeff C. Phillips
 */
public final class AttributeChangeBuilder extends ChangeBuilder {
   private final String isValue;
   private String wasValue;
   private final long attrId;
   private final AttributeTypeToken attributeType;
   private final ModificationType artModType;
   private final String isUri;
   private String wasUri;

   public AttributeChangeBuilder(BranchId branch, ArtifactTypeId artifactType, GammaId sourceGamma, long artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical, String isValue, String wasValue, long attrId, AttributeTypeToken attributeType, ModificationType artModType, String isUri, String wasUri) {
      super(branch, artifactType, sourceGamma, artId, txDelta, modType, isHistorical);
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.attrId = attrId;
      this.attributeType = attributeType;
      this.artModType = artModType;
      this.isUri = isUri;
      this.wasUri = wasUri;
   }

   public ModificationType getArtModType() {
      return artModType;
   }

   public void setWasValue(String wasValue) {
      this.wasValue = wasValue;
   }

   public String getIsValue() {
      return isValue;
   }

   public String getWasValue() {
      return wasValue;
   }

   public long getAttrId() {
      return attrId;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public String getIsUri() {
      return isUri;
   }

   public String getWasUri() {
      return wasUri;
   }

   public void setWasUri(String wasUri) {
      this.wasUri = wasUri;
   }

}