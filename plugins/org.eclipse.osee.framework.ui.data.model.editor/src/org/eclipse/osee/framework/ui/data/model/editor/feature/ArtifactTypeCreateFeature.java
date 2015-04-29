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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactTypeCreateFeature extends AbstractCreateFeature {

   public ArtifactTypeCreateFeature(IFeatureProvider fp) {
      super(fp, "ArtifactType", "Create an artifact type");
   }

   @Override
   public boolean canCreate(ICreateContext context) {
      return true;
   }

   @Override
   public Object[] create(ICreateContext context) {
      Resource resource = context.getTargetContainer().eResource();

      // Create a new chess board and add it to an EMF resource
      XArtifactType artifactType = OseeDslFactory.eINSTANCE.createXArtifactType();
      resource.getContents().add(artifactType);

      // Delegate to the add feature to add the corresponding graphical representation
      addGraphicalRepresentation(context, artifactType);
      return new Object[] {artifactType};
   }
}