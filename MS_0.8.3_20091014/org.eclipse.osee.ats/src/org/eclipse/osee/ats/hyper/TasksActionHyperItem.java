/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.hyper;

import java.util.Collection;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class TasksActionHyperItem extends ActionHyperItem {

   private final Collection<TaskArtifact> taskArtifacts;

   /**
    * @param hyperartifact
    */
   public TasksActionHyperItem(Collection<TaskArtifact> taskArtifacts) {
      super(new TasksHyperViewArtifact(taskArtifacts));
      this.taskArtifacts = taskArtifacts;
      setRelationToolTip("Task");
   }

   @Override
   public void handleDoubleClick(HyperViewItem hyperViewItem) throws OseeCoreException {
      super.handleDoubleClick(hyperViewItem);
      WorldEditor.open(new WorldEditorSimpleProvider("Tasks", taskArtifacts));
   }

   @Override
   public void calculateCurrent(Artifact currentArtifact) {
      setCurrent(taskArtifacts.contains(currentArtifact));
   }

   @Override
   public Image getImage() throws OseeCoreException {
      return ImageManager.getImage(ArtifactTypeManager.getType(TaskArtifact.ARTIFACT_NAME));

   }

}
