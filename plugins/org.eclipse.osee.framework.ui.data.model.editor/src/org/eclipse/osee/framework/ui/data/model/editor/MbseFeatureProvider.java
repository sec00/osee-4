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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.ModelDefinition;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.ModelDefinitionUsage;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.data.model.editor.feature.MbseCreateFeature;
import org.eclipse.osee.framework.ui.data.model.editor.feature.MbseDefinitionAddFeature;
import org.eclipse.osee.framework.ui.data.model.editor.feature.MbseDefinitionUsageAddFeature;
import org.eclipse.osee.framework.ui.data.model.editor.feature.MbseRenameFeature;
import org.eclipse.osee.framework.ui.data.model.editor.feature.MbseUpdateFeature;

/**
 * @author Ryan D. Brooks
 */
public class MbseFeatureProvider extends DefaultFeatureProvider {
   private final IAddFeature modelDefinitionAddFeature;
   private final IAddFeature modelDefinitionUsageAddFeature;
   private final NonEmfNotificationService notification;
   private final MbseUpdateFeature updateFeature;
   private final MbseCreateFeature definitionCreateFeature;
   private final MbseCreateFeature definitionUsageCreateFeature;

   public MbseFeatureProvider(IDiagramTypeProvider dtp, NonEmfNotificationService notification) {
      super(dtp);
      this.notification = notification;
      getDirectEditingInfo().setActive(true);
      modelDefinitionAddFeature = new MbseDefinitionAddFeature(this, notification);
      modelDefinitionUsageAddFeature = new MbseDefinitionUsageAddFeature(this, notification);
      updateFeature = new MbseUpdateFeature(this);

      definitionCreateFeature =
         new MbseCreateFeature(this, "Model Definition", "Create a model definition", ModelDefinition);
      definitionUsageCreateFeature =
         new MbseCreateFeature(this, "Model Definition Usage", "Create a model definition usage", ModelDefinitionUsage);
   }

   @Override
   public ICreateFeature[] getCreateFeatures() {
      return new ICreateFeature[] {definitionCreateFeature, definitionUsageCreateFeature};
   }

   @Override
   public IAddFeature getAddFeature(IAddContext context) {
      Object obj = context.getNewObject();
      if (obj instanceof Artifact) {
         Artifact artifact = (Artifact) obj;
         if (artifact.isOfType(CoreArtifactTypes.ModelDefinition)) {
            return modelDefinitionAddFeature;
         } else if (artifact.isOfType(CoreArtifactTypes.ModelDefinitionUsage)) {
            return modelDefinitionUsageAddFeature;
         }
      }
      return super.getAddFeature(context);
   }

   @Override
   public ICustomFeature[] getCustomFeatures(ICustomContext context) {
      return new ICustomFeature[] {new MbseRenameFeature(this)};
   }

   @Override
   public Object getBusinessObjectForPictogramElement(PictogramElement pictogramElement) {
      return notification.getBusinessObjectForPictogramElement(pictogramElement);
   }

   @Override
   public Object[] getAllBusinessObjectsForPictogramElement(PictogramElement pictogramElement) {
      return new Object[] {getBusinessObjectForPictogramElement(pictogramElement)};
   }

   @Override
   public IUpdateFeature getUpdateFeature(IUpdateContext context) {
      if (updateFeature.canUpdate(context)) {
         return updateFeature;
      }
      return super.getUpdateFeature(context);
   }
}