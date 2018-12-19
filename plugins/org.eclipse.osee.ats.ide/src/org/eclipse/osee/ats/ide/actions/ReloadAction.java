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
package org.eclipse.osee.ats.ide.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ReloadAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact sma;

   public ReloadAction(AbstractWorkflowArtifact sma) {
      super();
      String title = "Reload \"" + sma.getArtifactTypeName() + "\"";
      setText(title);
      setToolTipText(getText());
      this.sma = sma;
   }

   @Override
   public void runWithException() {
      Set<Artifact> relatedArts = new HashSet<>();
      relatedArts.add(sma);
      if (sma.isTeamWorkflow()) {
         relatedArts.addAll(ReviewManager.getReviews((TeamWorkFlowArtifact) sma));
      }
      if (sma instanceof TeamWorkFlowArtifact) {
         Collection<IAtsTask> tasks = AtsClientService.get().getTaskService().getTasks((TeamWorkFlowArtifact) sma);
         relatedArts.addAll(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(tasks));
      }
      ArtifactQuery.reloadArtifacts(relatedArts);
      // Don't need to re-open editor cause event handler will do that
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(PluginUiImage.REFRESH);
   }

}