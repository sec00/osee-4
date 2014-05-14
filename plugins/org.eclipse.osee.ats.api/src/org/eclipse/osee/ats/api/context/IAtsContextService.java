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
package org.eclipse.osee.ats.api.context;

import java.util.Set;

public interface IAtsContextService {

   public abstract void addContext(String uuid, String name);

   public abstract Set<String> getContextUuids();

   public abstract String getContextName(String uuid);

   public abstract String getUserContextUuid(String userId);

   public abstract void setUserContextUuid(String userId, String uuid);

   public abstract Set<String> getUserIds();

   public void writeToStore() throws Exception;

   public void read() throws Exception;

}