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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;

/**
 * @author Ryan D. Brooks
 */
public class GraphitiArtifactUpdateBehavior extends DefaultUpdateBehavior {

   public GraphitiArtifactUpdateBehavior(DiagramBehavior diagramBehavior) {
      super(diagramBehavior);
   }

   @Override
   protected void createEditingDomain(IDiagramEditorInput input) {
      super.createEditingDomain(input);
   }

   @Override
   protected Adapter createUpdateAdapter() {
      return super.createUpdateAdapter();
   }

}