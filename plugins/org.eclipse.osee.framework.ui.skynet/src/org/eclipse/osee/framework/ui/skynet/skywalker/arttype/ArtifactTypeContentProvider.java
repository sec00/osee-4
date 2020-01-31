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

package org.eclipse.osee.framework.ui.skynet.skywalker.arttype;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeContentProvider implements IGraphEntityContentProvider {

   private final Set<ArtifactTypeToken> parentTypes = new HashSet<>();
   private ArtifactTypeToken selectedArtType = null;

   public ArtifactTypeContentProvider() {
      super();
   }

   @Override
   public Object[] getConnectedTo(Object entity) {
      try {
         if (entity instanceof ArtifactTypeToken) {
            ArtifactTypeToken artifactType = (ArtifactTypeToken) entity;
            if (parentTypes.contains(artifactType)) {
               Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
               for (ArtifactTypeToken childType : artifactType.getDirectDescendantTypes()) {
                  if (parentTypes.contains(childType)) {
                     artifactTypes.add(childType);
                  }
               }
               return artifactTypes.toArray();
            } else if (parentTypes.contains(entity)) {
               Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
               for (ArtifactTypeToken childType : artifactType.getSuperTypes()) {
                  if (parentTypes.contains(childType)) {
                     artifactTypes.add(childType);
                  }
               }
               return artifactTypes.toArray();
            } else if (selectedArtType.equals(entity) && selectedArtType.notEqual(CoreArtifactTypes.Artifact)) {
               Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
               // parents
               for (ArtifactTypeToken childType : artifactType.getSuperTypes()) {
                  if (parentTypes.contains(childType)) {
                     artifactTypes.add(childType);
                  }
               }
               // children
               artifactTypes.addAll(artifactType.getDirectDescendantTypes());
               return artifactTypes.toArray();
            } else {
               return artifactType.getDirectDescendantTypes().toArray();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      try {
         if (inputElement instanceof ArtifactTypeToken) {
            ArtifactTypeToken artifactType = (ArtifactTypeToken) inputElement;
            Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
            getParents(artifactType, artifactTypes);
            if (!parentTypes.contains(artifactType)) {
               artifactTypes.add(artifactType);
               getDecendents(artifactType, artifactTypes);
            }
            return artifactTypes.toArray();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public void getParents(ArtifactTypeToken artifactType, Set<ArtifactTypeToken> parents) {
      for (ArtifactTypeToken artType : artifactType.getSuperTypes()) {
         parents.add(artType);
         parentTypes.add(artType);
         getParents(artType, parents);
      }
   }

   public void getDecendents(ArtifactTypeToken artifactType, Set<ArtifactTypeToken> decendents) {
      for (ArtifactTypeToken artType : artifactType.getDirectDescendantTypes()) {
         if (!parentTypes.contains(artType)) {
            decendents.add(artType);
            getDecendents(artType, decendents);
         }
      }
   }

   public double getWeight(Object entity1, Object entity2) {
      return 0;
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

   public Set<ArtifactTypeToken> getParentTypes() {
      return parentTypes;
   }

   public ArtifactTypeToken getSelectedArtType() {
      return selectedArtType;
   }

   public void setSelectedArtType(ArtifactTypeToken selectedArtType) {
      this.selectedArtType = selectedArtType;
   }

}
