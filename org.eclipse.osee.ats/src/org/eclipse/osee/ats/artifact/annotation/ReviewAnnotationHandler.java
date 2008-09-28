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

package org.eclipse.osee.ats.artifact.annotation;

import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.IArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation.Type;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class ReviewAnnotationHandler implements IArtifactAnnotation {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.artifact.annotation.IArtifactAnnotation#getAnnotations(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   public void getAnnotations(Artifact artifact, Set<ArtifactAnnotation> annotations) {
      if (artifact instanceof StateMachineArtifact) {
         try {
            if ((new SMAManager((StateMachineArtifact) artifact)).getReviewManager().getReviews().size() == 0) return;
            for (ReviewSMArtifact review : (new SMAManager((StateMachineArtifact) artifact)).getReviewManager().getReviews()) {
               if (!review.getSmaMgr().isCompleted()) {
                  annotations.add(new ArtifactAnnotation(Type.Warning, "ats.Review",
                        "Review(s) open against this workflow."));
                  return;
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }
}
