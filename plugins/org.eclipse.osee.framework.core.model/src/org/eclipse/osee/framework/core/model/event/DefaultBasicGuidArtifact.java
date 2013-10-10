/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.event;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;

/**
 * @author Donald G. Dunne
 */
public class DefaultBasicGuidArtifact extends BaseIdentity<String> implements IBasicGuidArtifact {
   private static final long serialVersionUID = -4997763989583925345L;
   private final String branchGuid;
   private Long artTypeGuid;

   public DefaultBasicGuidArtifact(String branchGuid, Long artTypeGuid, String artGuid) {
      super(artGuid);
      this.branchGuid = branchGuid;
      this.artTypeGuid = artTypeGuid;
   }

   @Override
   public String getBranchGuid() {
      return branchGuid;
   }

   @Override
   public Long getArtTypeGuid() {
      return artTypeGuid;
   }

   @Override
   public String toString() {
      return String.format("[%s]", getGuid());
   }

   @Override
   public int hashCode() {
      // NOTE This hashcode MUST match that of Artifact class
      return super.hashCode();
   }

   /**
    * Note: DefaultBasicGuidArtifact class does not implement the hashCode, but instead uses the one implemented by
    * Identity. It can not use the branch guid due to the need for IArtifactTokens to match Artifact instances. In
    * addition, the event system requires that the DefaultBasicGuidArtifact and Artifact hashcode matches.
    */
   @Override
   public boolean equals(Object obj) {
      boolean equals = super.equals(obj);
      if (!equals && obj instanceof IBasicGuidArtifact) {
         IBasicGuidArtifact other = (IBasicGuidArtifact) obj;

         if (artTypeGuid == null || other.getArtTypeGuid() == null) {
            equals = false;
         }
         equals = artTypeGuid.equals(other.getArtTypeGuid());

         if (equals && branchGuid == null || other.getBranchGuid() == null) {
            equals = false;
         } else if (equals) {
            equals = branchGuid.equals(other.getBranchGuid());
         }

         if (equals && getGuid() == null || other.getGuid() == null) {
            equals = false;
         } else if (equals) {
            equals = getGuid().equals(other.getGuid());
         }
      }
      return equals;
   }

   public void setArtTypeGuid(Long artTypeGuid) {
      this.artTypeGuid = artTypeGuid;
   }

   public boolean is(IArtifactType... artifactTypes) {
      for (IArtifactType artifactType : artifactTypes) {
         if (artifactType.getGuid().equals(getArtTypeGuid())) {
            return true;
         }
      }
      return false;
   }
}
