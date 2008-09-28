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

package org.eclipse.osee.framework.ui.skynet.skywalker;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class ArtifactGraphContentProvider implements IGraphEntityContentProvider {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactGraphContentProvider.class);
   // private static final Collection<Artifact>EMPTY_LIST = new ArrayList<Artifact>(0);
   private SkyWalkerOptions options;

   /**
    * @param levels
    */
   public ArtifactGraphContentProvider(SkyWalkerOptions options) {
      super();
      this.options = options;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getConnectedTo(java.lang.Object)
    */
   public Object[] getConnectedTo(Object entity) {
      List<Artifact> otherItems = new LinkedList<Artifact>();

      // Don't want to create any links to artifacts that are NOT in displayArtifacts
      try {
         Artifact artifact = (Artifact) entity;
         List<RelationType> validTypes =
               RelationTypeManager.getValidTypes(artifact.getArtifactType(), artifact.getBranch());
         for (RelationType relationType : validTypes) {
            if (options.isValidRelationType(relationType)) {
               for (Artifact art : artifact.getRelatedArtifacts(relationType)) {
                  if (options.isValidArtifactType(art.getArtifactType()) && displayArtifacts.contains(art)) {
                     otherItems.add(art);
                  }
               }
            }
         }
      } catch (OseeCoreException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return otherItems.toArray();
   }
   private Set<Artifact> displayArtifacts = new HashSet<Artifact>();

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getElements(java.lang.Object)
    */
   public Object[] getElements(Object inputElement) {
      // Only perform this method for top level artifact
      if (inputElement.equals(options.getArtifact())) {
         displayArtifacts.clear();
         displayArtifacts.add((Artifact) inputElement);
         getDescendants(displayArtifacts, (Artifact) inputElement, options.getLevels());
         return displayArtifacts.toArray();
      }
      return null;
   }

   private void getDescendants(Collection<Artifact> displayArtifacts, Artifact artifact, int level) {
      // System.out.println("getDecendants level: " + level + " artifact => " + artifact);
      if (level == 0) {
         return;
      } else {
         try {
            List<RelationType> validTypes =
                  RelationTypeManager.getValidTypes(artifact.getArtifactType(), artifact.getBranch());
            for (RelationType relationType : validTypes) {
               if (options.isValidRelationType(relationType)) {
                  for (Artifact art : artifact.getRelatedArtifacts(relationType)) {
                     if (options.isValidArtifactType(art.getArtifactType())) {
                        displayArtifacts.add(art);
                        getDescendants(displayArtifacts, art, level - 1);
                     }
                  }
               }
            }
         } catch (OseeCoreException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getWeight(java.lang.Object,
    *      java.lang.Object)
    */
   public double getWeight(Object entity1, Object entity2) {
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#dispose()
    */
   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
    *      java.lang.Object, java.lang.Object)
    */
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
