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
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.osee.framework.ui.data.model.editor.feature.MbseRenameFeature;

/**
 * @author Ryan D. Brooks
 */
public class MbseToolBehaviorProvider extends DefaultToolBehaviorProvider {
   private final ICustomFeature renameFeature;

   public MbseToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
      super(diagramTypeProvider);
      renameFeature = new MbseRenameFeature(getFeatureProvider());
   }

   @Override
   public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
      if (renameFeature.canExecute(context)) {
         return renameFeature;
      }

      return super.getDoubleClickFeature(context);
   }
}