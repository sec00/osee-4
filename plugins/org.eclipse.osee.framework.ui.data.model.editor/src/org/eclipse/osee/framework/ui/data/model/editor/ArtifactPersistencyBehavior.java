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

import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactPersistencyBehavior extends DefaultPersistencyBehavior {

   public ArtifactPersistencyBehavior(DiagramBehavior diagramBehavior) {
      super(diagramBehavior);
   }

   /**
    * override to provide alternative Persistency (i.e. using artifacts)
    * https://github.com/hallvard/ptolemy/blob/master/org.ptolemy.graphiti.generic/src/org/ptolemy/graphiti/generic/
    * editor/ActorDiagramPersistencyBehavior.java
    */
   @Override
   protected Set<Resource> save(TransactionalEditingDomain editingDomain, Map<Resource, Map<?, ?>> saveOptions, IProgressMonitor monitor) {
      Set<Resource> resources = super.save(editingDomain, saveOptions, monitor);

      return resources;
   }

   /**
    * add the diagram you create in loadDiagram() to the resource set/editing domain of the editor? The diagram needs to
    * be part of a resource the editor knows to be able to show it. (see
    * https://www.eclipse.org/forums/index.php/t/493540/)
    */
   @Override
   public Diagram loadDiagram(URI uri) {
      return super.loadDiagram(uri);
   }
}
