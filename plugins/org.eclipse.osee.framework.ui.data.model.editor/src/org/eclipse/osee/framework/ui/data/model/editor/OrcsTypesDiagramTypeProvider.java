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

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * Graphiti Diagram Type Provider which is part of the Graphiti diagram type agent
 * 
 * @author Ryan D. Brooks
 */
public class OrcsTypesDiagramTypeProvider extends AbstractDiagramTypeProvider {
   private final IToolBehaviorProvider[] toolBehaviorProviders;

   public OrcsTypesDiagramTypeProvider() {
      setFeatureProvider(new OrcsTypesFeatureProvider(this));
      toolBehaviorProviders = new IToolBehaviorProvider[] {new OrcsTypesToolBehaviorProvider(this)};
   }

   @Override
   public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
      return toolBehaviorProviders;
   }
}
