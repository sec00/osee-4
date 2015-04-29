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

import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.notification.DefaultNotificationService;

/**
 * @author Ryan D. Brooks
 */
public class NonEmfNotificationService extends DefaultNotificationService {
   private final ConcurrentHashMap<Object, PictogramElement[]> boToPesMap;
   private final ConcurrentHashMap<PictogramElement, Object> pesToBoMap;

   private static final PictogramElement[] emptyPes = new PictogramElement[0];

   public NonEmfNotificationService(IDiagramTypeProvider diagramTypeProvider) {
      super(diagramTypeProvider);
      boToPesMap = new ConcurrentHashMap<Object, PictogramElement[]>();
      pesToBoMap = new ConcurrentHashMap<PictogramElement, Object>();
   }

   @Override
   public PictogramElement[] calculateRelatedPictogramElements(Object[] changedBOs) {
      if (changedBOs.length == 1) {
         PictogramElement[] pes = boToPesMap.get(changedBOs[0]);
         if (pes == null) {
            return emptyPes;
         }
         return pes;
      }

      int peCount = 0;
      for (Object changedBO : changedBOs) {
         PictogramElement[] pes = boToPesMap.get(changedBO);
         peCount += pes.length;
      }

      PictogramElement[] allPes = new PictogramElement[peCount];
      int index = 0;
      for (Object changedBO : changedBOs) {
         PictogramElement[] pes = boToPesMap.get(changedBO);
         for (PictogramElement pe : pes) {
            allPes[index++] = pe;
         }
      }
      return allPes;
   }

   public Object getBusinessObjectForPictogramElement(PictogramElement pictogramElement) {
      return pesToBoMap.get(pictogramElement);
   }

   public void link(Object businessObject, PictogramElement... pictogramElements) {
      boToPesMap.put(businessObject, pictogramElements);
      for (PictogramElement pe : pictogramElements) {
         pesToBoMap.put(pe, businessObject);
      }
   }
}
