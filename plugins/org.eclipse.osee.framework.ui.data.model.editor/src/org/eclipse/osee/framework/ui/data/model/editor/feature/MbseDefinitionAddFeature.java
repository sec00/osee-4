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
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
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
public class MbseDefinitionAddFeature extends AbstractAddShapeFeature {

   // the additional size of the invisible rectangle at the right border
   // (this also equals the half width of the anchor to paint there)
   public static final int INVISIBLE_RECT_RIGHT = 6;

   private final NonEmfNotificationService notification;

   public MbseDefinitionAddFeature(IFeatureProvider fp, NonEmfNotificationService notification) {
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
      Diagram targetDiagram = (Diagram) context.getTargetContainer();

      // CONTAINER SHAPE WITH ROUNDED RECTANGLE
      final IPeCreateService peCreateService = Graphiti.getPeCreateService();
      final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

      // check whether the context has a size (e.g. from a create feature)
      // otherwise define a default size for the shape
      final int width = context.getWidth() <= 0 ? 100 : context.getWidth();
      final int height = context.getHeight() <= 0 ? 50 : context.getHeight();

      final IGaService gaService = Graphiti.getGaService();
      RoundedRectangle roundedRectangle; // need to access it later
      {
         // create invisible outer rectangle expanded by
         // the width needed for the anchor
         final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
         gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), width + INVISIBLE_RECT_RIGHT,
            height);

         // create and set visible rectangle inside invisible rectangle
         roundedRectangle = gaService.createPlainRoundedRectangle(invisibleRectangle, 5, 5);
         roundedRectangle.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
         gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);

      }

      // SHAPE WITH LINE
      {
         // create shape for line
         final Shape shape = peCreateService.createShape(containerShape, false);

         // create and set graphics algorithm
         final Polyline polyline = gaService.createPlainPolyline(shape, new int[] {0, 20, width, 20});
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

      // add a chopbox anchor to the shape
      peCreateService.createChopboxAnchor(containerShape);

      // create an additional box relative anchor at middle-right
      final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
      boxAnchor.setRelativeWidth(1.0);
      boxAnchor.setRelativeHeight(0.38); // Use golden section

      // anchor references visible rectangle instead of invisible rectangle
      boxAnchor.setReferencedGraphicsAlgorithm(roundedRectangle);

      // assign a graphics algorithm for the box relative anchor
      final Ellipse ellipse = gaService.createPlainEllipse(boxAnchor);

      // anchor is located on the right border of the visible rectangle
      // and touches the border of the invisible rectangle
      final int w = INVISIBLE_RECT_RIGHT;
      gaService.setLocationAndSize(ellipse, -w, -w, 2 * w, 2 * w);
      ellipse.setStyle(StyleUtil.getStyleForEClass(getDiagram()));

      notification.link(def, new PictogramElement[] {containerShape, shape});

      // call the layout feature
      layoutPictogramElement(containerShape);

      return containerShape;
   }
}