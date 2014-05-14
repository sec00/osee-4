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
package org.eclipse.osee.ats.core.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class AtsContext {

   private final Map<String, String> contextUuidsToNameMap = new HashMap<String, String>();
   private final Map<String, String> userIdtoContextUuid = new HashMap<String, String>();

   public AtsContext() {
   }

   public void addContext(String uuid, String name) {
      contextUuidsToNameMap.put(uuid, name);
   }

   public Set<String> getContextUuids() {
      return contextUuidsToNameMap.keySet();
   }

   public String getContextName(String uuid) {
      return contextUuidsToNameMap.get(uuid);
   }

   public String getUserContextUuid(String userId) {
      return userIdtoContextUuid.get(userId);
   }

   public void setUserContextUuid(String userId, String uuid) {
      userIdtoContextUuid.put(userId, uuid);
   }

   public Set<String> getUserIds() {
      return userIdtoContextUuid.keySet();
   }

}
