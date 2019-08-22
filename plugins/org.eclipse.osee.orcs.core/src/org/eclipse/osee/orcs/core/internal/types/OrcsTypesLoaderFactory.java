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
package org.eclipse.osee.orcs.core.internal.types;

import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypesLoaderFactory {

   OrcsTypesLoader createTypesLoader(OrcsSession session, OrcsTypesDataStore ds);

   OrcsTypesLoader createTypesLoader(OrcsSession session, IResource resource);

}