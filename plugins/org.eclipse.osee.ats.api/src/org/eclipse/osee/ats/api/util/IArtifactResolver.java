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
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactResolver {

   ArtifactId get(IAtsObject atsObject);

   <A extends ArtifactId> A get(IAtsWorkItem workItem, Class<?> clazz);

   <A extends ArtifactId> List<A> get(Collection<? extends IAtsWorkItem> workItems, Class<?> clazz);

   ArtifactTypeToken getArtifactType(IAtsWorkItem workItem);

}