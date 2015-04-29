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
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.util.ILocationInfo;
import org.eclipse.graphiti.util.LocationInfo;

/**
 * @author Ryan D. Brooks
 */
public class OrcsTypesToolBehaviorProvider extends DefaultToolBehaviorProvider {

   public OrcsTypesToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
      super(diagramTypeProvider);
   }

   @Override
   public ILocationInfo getLocationInfo(PictogramElement pe, ILocationInfo locationInfo) {
      Object domainObject = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
      if (domainObject instanceof Object && locationInfo != null) {
         return new LocationInfo((Shape) pe, locationInfo.getGraphicsAlgorithm());
      }
      return super.getLocationInfo(pe, locationInfo);
   }
}