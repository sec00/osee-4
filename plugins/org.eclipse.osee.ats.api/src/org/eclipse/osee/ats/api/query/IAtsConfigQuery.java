/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigQuery {

   IAtsConfigQuery andAttr(IAttributeType attributeType, String value, QueryOption... queryOption);

   <T extends IAtsConfigObject> ResultSet<T> getResults();

   Collection<Integer> getItemIds() throws OseeCoreException;

   <T extends IAtsConfigObject> Collection<T> getItems();

   IAtsConfigQuery isOfType(IArtifactType artifactType);

   IAtsConfigQuery andAttr(IAttributeType attributeType, Collection<String> values, QueryOption... queryOptions) throws OseeCoreException;

   IAtsConfigQuery andUuids(Long... uuids);

   <T extends ArtifactId> ResultSet<T> getResultArtifacts();

}
