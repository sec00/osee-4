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
package org.eclipse.osee.framework.core.model.internal.fields;

import org.eclipse.osee.framework.core.model.AbstractOseeField;

/**
 * @author Roberto E. Escobar
 */
public class AssociatedArtifactField extends AbstractOseeField<Long> {

   private Long artId;

   public AssociatedArtifactField(Long artId) {
      super();
      this.artId = artId;
   }

   @Override
   public Long get() {
      return artId;
   }

   @Override
   public void set(Long artId) {
      boolean wasDifferent = isDifferent(get(), artId);
      if (wasDifferent) {
         this.artId = artId;
      }
      isDirty |= wasDifferent;
   }

   private boolean isDifferent(Long artId1, Long artId2) {
      if (artId1 == null) {
         if (artId2 == null) {
            return false;
         } else {
            return true;
         }
      } else {
         if (artId2 == null) {
            return true;
         } else {
            return !(artId1.equals(artId2));
         }
      }
   }
}