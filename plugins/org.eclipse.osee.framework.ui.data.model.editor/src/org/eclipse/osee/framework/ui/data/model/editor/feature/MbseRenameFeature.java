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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Ryan D. Brooks
 */
public class MbseRenameFeature extends AbstractCustomFeature {

   private boolean hasDoneChanges = false;

   public MbseRenameFeature(IFeatureProvider fp) {
      super(fp);
   }

   @Override
   public String getName() {
      return "Re&name"; //$NON-NLS-1$
   }

   @Override
   public String getDescription() {
      return "Change the name"; //$NON-NLS-1$
   }

   @Override
   public boolean canExecute(ICustomContext context) {
      PictogramElement[] pes = context.getPictogramElements();
      if (pes != null && pes.length == 1) {
         Object bo = getBusinessObjectForPictogramElement(pes[0]);
         if (bo instanceof Artifact) {
            return true;
         }
      }
      return false;
   }

   @Override
   public void execute(ICustomContext context) {
      PictogramElement[] pes = context.getPictogramElements();
      if (pes != null && pes.length == 1) {
         Object bo = getBusinessObjectForPictogramElement(pes[0]);
         if (bo instanceof Artifact) {
            Artifact artifact = (Artifact) bo;
            String currentName = artifact.getName();

            String newName = AWorkbench.popupTextInput("Rename", getDescription(), currentName, null);
            if (newName != null && !newName.equals(currentName)) {
               this.hasDoneChanges = true;
               artifact.setName(newName);
               updatePictogramElement(pes[0]);
            }
         }
      }
   }

   @Override
   public boolean hasDoneChanges() {
      return this.hasDoneChanges;
   }
}
