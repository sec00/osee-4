/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats;

import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage.Location;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactImageProvider extends ArtifactImageProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider#init()
    */
   @Override
   public void init() throws OseeCoreException {
      ImageManager.registerProvider(this, ArtifactTypeManager.getType(VersionArtifact.ARTIFACT_NAME));
      ImageManager.registerProvider(this, ArtifactTypeManager.getType(TaskArtifact.ARTIFACT_NAME));
      ImageManager.registerProvider(this, ArtifactTypeManager.getType(PeerToPeerReviewArtifact.ARTIFACT_NAME));
      ImageManager.registerProvider(this, ArtifactTypeManager.getType(DecisionReviewArtifact.ARTIFACT_NAME));
      for (String artName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {
         try {
            ImageManager.registerProvider(this, ArtifactTypeManager.getType(artName));
         } catch (OseeTypeDoesNotExist ex) {
            OseeLog.log(AtsPlugin.class, Level.INFO, ex);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider#getImage(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public String setupImage(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(VersionArtifact.ARTIFACT_NAME)) {
         if (artifact.getSoleAttributeValue("ats.Next Version", false)) {
            return ImageManager.setupImage(artifact, AtsImage.NEXT, Location.BOT_RIGHT);
         }
         if (artifact.getSoleAttributeValue("ats.Released", false)) {
            return ImageManager.setupImage(artifact, AtsImage.RELEASED, Location.TOP_RIGHT);
         }
      }

      if (artifact instanceof StateMachineArtifact) {
         StateMachineArtifact stateMachine = (StateMachineArtifact) artifact;
         if (stateMachine.isSubscribed(UserManager.getUser())) {
            // was 8,6
            return ImageManager.setupImage(artifact, AtsImage.SUBSCRIBED_OVERLAY, Location.BOT_RIGHT);
         }
         if (stateMachine.isFavorite(UserManager.getUser())) {
            // was 7,0
            return ImageManager.setupImage(artifact, AtsImage.FAVORITE_OVERLAY, Location.TOP_RIGHT);
         }
      }

      return super.setupImage(artifact);
   }
}