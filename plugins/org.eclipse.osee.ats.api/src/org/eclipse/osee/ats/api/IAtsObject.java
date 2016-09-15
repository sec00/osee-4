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
package org.eclipse.osee.ats.api;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
public interface IAtsObject extends Named, Id, HasDescription {

   default String toStringWithId() {
      return String.format("[%s][%s]", getName(), getId());
   }

   default ArtifactToken getStoreObject() {
      return null;
   }

   default void setStoreObject(ArtifactToken artifact) {
      // do nothing
   }

   @Override
   default String getDescription() {
      return getName();
   }
}