/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.criteria;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.IRelationType;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaRelation extends CriteriaArtifact {

   private final Collection<Long> ids;
   private final Collection<? extends IRelationType> types;

   public CriteriaRelation(Collection<Long> ids, Collection<? extends IRelationType> types) {
      super();
      this.ids = ids;
      this.types = types;
   }

   public Collection<Long> getIds() {
      return ids != null ? ids : Collections.<Long> emptyList();
   }

   public Collection<? extends IRelationType> getTypes() {
      return types != null ? types : Collections.<IRelationType> emptyList();
   }

   @Override
   public String toString() {
      return "CriteriaRelation [queryId=" + getQueryId() + ", ids=" + ids + ", types=" + types + "]";
   }

}
