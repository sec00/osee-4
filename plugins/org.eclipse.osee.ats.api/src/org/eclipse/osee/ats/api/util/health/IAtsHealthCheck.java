/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util.health;

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsHealthCheck {

   public void check(ArtifactId artifact, IAtsWorkItem workItem, HealthCheckResults results, IAtsServices services);

   public default String getName() {
      return getClass().getSimpleName();
   }

   public default void error(HealthCheckResults results, IAtsWorkItem workItem, String format, Object... data) {
      results.log(workItem.getStoreObject(), getClass().getSimpleName(),
         String.format("Error: " + format + " for " + workItem.getAtsId(), data));
   }
}