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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractAttributeType<T> extends NamedIdDescription implements AttributeTypeToken {
   private final String mediaType;
   private final TaggerTypeToken taggerType;

   public AbstractAttributeType(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, name, description);
      this.mediaType = mediaType;
      this.taggerType = taggerType;
   }

   @Override
   public String getMediaType() {
      return mediaType;
   }

   public abstract T valueFromStorageString(String storedValue);

   public String storageStringFromValue(T value) {
      return value.toString();
   }

   public String getDisplayableString(T value) {
      return storageStringFromValue(value);
   }

   public TaggerTypeToken getTaggerType() {
      return taggerType;
   }
}