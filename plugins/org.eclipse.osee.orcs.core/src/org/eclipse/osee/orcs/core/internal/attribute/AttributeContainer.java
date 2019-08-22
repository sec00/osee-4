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
package org.eclipse.osee.orcs.core.internal.attribute;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.orcs.core.ds.Attribute;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeContainer extends ArtifactToken {

   <T> void add(AttributeTypeToken<T> attributeType, Attribute<T> attribute);

   <T> void remove(AttributeTypeToken<T> type, Attribute<T> attribute);

   boolean isLoaded();

   void setLoaded(boolean value);

   String getExceptionString();

   boolean areAttributesDirty();

   <T> int getAttributeCount(AttributeTypeToken<T> type);

   <T> int getAttributeCount(AttributeTypeToken<T> type, DeletionFlag deletionFlag);

   <T> boolean isAttributeTypeValid(AttributeTypeToken<T> attributeType);

   Collection<AttributeTypeToken<?>> getValidAttributeTypes();

   Collection<AttributeTypeToken<?>> getExistingAttributeTypes();
}