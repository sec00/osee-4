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
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author John Misinco
 */
public class CriteriaAttributeTypeNotExists extends Criteria {

   private final Collection<AttributeTypeId> attributeTypes;
   private final String value;

   public CriteriaAttributeTypeNotExists(Collection<AttributeTypeId> attributeTypes) {
      this.attributeTypes = attributeTypes;
      value = null;
   }

   public CriteriaAttributeTypeNotExists(AttributeTypeId attributeType) {
      this(attributeType, null);
   }

   public CriteriaAttributeTypeNotExists(AttributeTypeId attributeType, String value) {
      this.attributeTypes = Arrays.asList(attributeType);
      this.value = value;
   }

   public Collection<AttributeTypeId> getTypes() {
      return attributeTypes;
   }

   public String getValue() {
      return value;
   }

   @Override
   public boolean checkValid(Options options) {
      Conditions.checkNotNull(getTypes(), "attribute types");
      return true;
   }

   @Override
   public String toString() {
      return "CriteriaAttributeTypeNotExists [attributeType=" + attributeTypes + "]";
   }
}
