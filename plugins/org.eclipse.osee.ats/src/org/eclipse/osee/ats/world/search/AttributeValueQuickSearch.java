/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AttributeValueQuickSearch {
   private final Collection<String> values;
   private final IAttributeType attributeType;

   public AttributeValueQuickSearch(IAttributeType attributeType, Collection<String> values) {
      this.attributeType = attributeType;
      this.values = values;
   }

   /**
    * Will match any quick-search token of given value
    */
   public Collection<Artifact> performSearch() throws OseeCoreException {
      return performSearch(false);
   }

   /**
    * Must match full value of given value
    */
   public Collection<Artifact> performSearch(boolean exactMatch) throws OseeCoreException {
      List<Artifact> result = new ArrayList<Artifact>();
      for (String pcrId : values) {
         if (values != null && values.size() > 0) {
            result =
               ArtifactQuery.getArtifactListFromAttributeKeywords(AtsUtil.getAtsBranch(), pcrId, false,
                  EXCLUDE_DELETED, false, attributeType);
            if (exactMatch) {
               List<Artifact> removeArts = new ArrayList<Artifact>();
               for (Artifact artifact : result) {
                  boolean found = false;
                  for (String value : values) {
                     if (artifact.getAttributesToStringList(attributeType).contains(value)) {
                        found = true;
                        break;
                     }
                  }
                  if (!found) {
                     removeArts.add(artifact);
                  }
               }
               result.removeAll(removeArts);
            }
         }
      }
      return result;
   }

}