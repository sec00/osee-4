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

import java.util.Collection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeSearchItem extends WorldSearchItem {

   private final String artifactTypeName;

   public ArtifactTypeSearchItem(String name, String artifactTypeName) {
      super(name);
      this.artifactTypeName = artifactTypeName;
   }

   public ArtifactTypeSearchItem(ArtifactTypeSearchItem artifactTypeSearchItem) {
      super(artifactTypeSearchItem);
      this.artifactTypeName = artifactTypeSearchItem.artifactTypeName;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      if (artifactTypeName == null) throw new IllegalArgumentException("Inavlid search \"" + getName() + "\"");
      return ArtifactQuery.getArtifactsFromType(artifactTypeName, AtsPlugin.getAtsBranch());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldSearchItem#copy()
    */
   @Override
   public WorldSearchItem copy() {
      return new ArtifactTypeSearchItem(this);
   }

}
