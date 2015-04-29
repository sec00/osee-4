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
package org.eclipse.osee.framework.ui.data.model.editor;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.eclipse.osee.framework.ui.data.model.editor.feature.ArtifactTypeAddFeature;
import org.eclipse.osee.framework.ui.data.model.editor.feature.ArtifactTypeCreateFeature;

/**
 * @author Ryan D. Brooks
 */
public class OrcsTypesFeatureProvider extends DefaultFeatureProvider {

   public OrcsTypesFeatureProvider(IDiagramTypeProvider dtp) {
      super(dtp);
   }

   @Override
   public ICreateFeature[] getCreateFeatures() {
      return new ICreateFeature[] {new ArtifactTypeCreateFeature(this)};
   }

   @Override
   public IAddFeature getAddFeature(IAddContext context) {
      return new ArtifactTypeAddFeature(this);
   }
}