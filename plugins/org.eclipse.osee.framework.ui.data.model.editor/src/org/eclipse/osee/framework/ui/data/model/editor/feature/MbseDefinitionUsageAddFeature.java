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

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.data.model.editor.NonEmfNotificationService;

/**
 * @author Ryan D. Brooks
 */
public class MbseDefinitionUsageAddFeature extends AbstractAddShapeFeature {

   // the additional size of the invisible rectangle at the right border
   // (this also equals the half width of the anchor to paint there)
   public static final int INVISIBLE_RECT_RIGHT = 6;

   private final NonEmfNotificationService notification;

   public MbseDefinitionUsageAddFeature(IFeatureProvider fp, NonEmfNotificationService notification) {
      super(fp);
      this.notification = notification;
   }

   @Override
   public boolean canAdd(IAddContext context) {
      return true;
   }

   @Override
   public PictogramElement add(IAddContext context) {
      Artifact def = (Artifact) context.getNewObject();
      ContainerShape target = context.getTargetContainer();

      // CONTAINER SHAPE WITH ROUNDED RECTANGLE
      final IPeCreateService peCreateService = Graphiti.getPeCreateService();
      final ContainerShape containerShape = peCreateService.createContainerShape(target, true);

      // check whether the context has a size (e.g. from a create feature)
      // otherwise define a default size for the shape
      final int width = context.getWidth() <= 0 ? 100 : context.getWidth();

      final IGaService gaService = Graphiti.getGaService();

      Ellipse ellipse = gaService.createEllipse(containerShape);
      gaService.setLocationAndSize(ellipse, context.getX(), context.getY(), width, width);
      ellipse.setStyle(StyleUtil.getStyleForEClass(getDiagram()));

      // SHAPE WITH LINE
      {
         // create shape for line
         final Shape shape = peCreateService.createShape(containerShape, false);

         // create and set graphics algorithm
         final Polyline polyline = gaService.createPlainPolyline(shape, new int[] {10, 20, (int) (width * .88), 20});
         polyline.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
      }

      // create shape for text
      final Shape shape = peCreateService.createShape(containerShape, false);
      // SHAPE WITH TEXT
      {

         // create and set text graphics algorithm
         final Text text = gaService.createPlainText(shape, def.getName());
         text.setStyle(StyleUtil.getStyleForEClassText(getDiagram()));
         gaService.setLocationAndSize(text, 0, 0, width, 20);

         // provide information to support direct-editing directly
         // after object creation (must be activated additionally)
         final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
         // set container shape for direct editing after object creation
         directEditingInfo.setMainPictogramElement(containerShape);
         // set shape and graphics algorithm where the editor for
         // direct editing shall be opened after object creation
         directEditingInfo.setPictogramElement(shape);
         directEditingInfo.setGraphicsAlgorithm(text);
      }

      notification.link(def, new PictogramElement[] {containerShape, shape});

      // call the layout feature
      layoutPictogramElement(containerShape);

      return containerShape;
   }
}