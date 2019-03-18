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
package org.eclipse.osee.ats.api.config;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;

/**
 * @author Donald G. Dunne
 */
public class JaxVersion extends JaxAtsConfigObject {

   @JsonSerialize(using = ToStringSerializer.class)
   Long teamDefId;

   public Long getTeamDefId() {
      return teamDefId;
   }

   public void setTeamDefId(Long teamDefId) {
      this.teamDefId = teamDefId;
   }

   @Override
   public ArtifactTypeId getArtifactType() {
      return AtsArtifactTypes.Version;
   }

}
