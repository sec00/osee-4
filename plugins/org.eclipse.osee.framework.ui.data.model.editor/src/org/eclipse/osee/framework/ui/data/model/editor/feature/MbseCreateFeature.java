/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor.feature;

import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Ryan D. Brooks
 */
public class MbseCreateFeature extends AbstractCreateFeature {

   private final IArtifactType artifactType;

   public MbseCreateFeature(IFeatureProvider fp, String name, String description, IArtifactType artifactType) {
      super(fp, name, description);
      this.artifactType = artifactType;
   }

   @Override
   public boolean canCreate(ICreateContext context) {
      return true;
   }

   @Override
   public Object[] create(ICreateContext context) {

      Artifact parentArtifact;
      Object obj = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());

      if (obj == null) {
         URI uri = getDiagram().eResource().getURI();
         String[] segments = uri.segments();
         IOseeBranch branch = BranchManager.getBranchByUuid(Long.parseLong(segments[0]));
         parentArtifact = ArtifactQuery.getArtifactFromId(Long.parseLong(segments[2]), branch);
      } else {
         parentArtifact = (Artifact) obj;
      }

      String name = AWorkbench.popupTextInput("Name new " + getName(), getDescription(), "", null);
      Artifact def = ArtifactTypeManager.addArtifact(artifactType, parentArtifact.getBranch(), name);
      parentArtifact.addChild(def);
      parentArtifact.persist("Create new " + artifactType.getName() + ": " + name);

      // Delegate to the add feature to add the corresponding graphical representation
      addGraphicalRepresentation(context, def);

      getFeatureProvider().getDirectEditingInfo().setActive(true);

      return new Object[] {def};
   }
}