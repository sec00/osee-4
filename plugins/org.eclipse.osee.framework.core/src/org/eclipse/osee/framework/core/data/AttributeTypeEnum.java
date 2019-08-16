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

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeEnum extends AttributeTypeString {
   private final String[] enumNames;

   public AttributeTypeEnum(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType, String[] enumNames) {
      super(id, name, mediaType, description, taggerType);
      this.enumNames = enumNames;
   }

   String[] getEnumNames() {
      return enumNames;
   }

   @Override
   public String valueFromStorageString(String storedValue) {
      return storedValue;
   }
}