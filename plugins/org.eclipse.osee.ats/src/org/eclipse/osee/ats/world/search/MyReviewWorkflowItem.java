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
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class MyReviewWorkflowItem extends UserSearchItem {

   private final ReviewState reviewState;

   public enum ReviewState {
      InWork,
      All
   };

   public MyReviewWorkflowItem(String name, User user, ReviewState reviewState) {
      super(name, user, AtsImage.REVIEW);
      this.reviewState = reviewState;
   }

   public MyReviewWorkflowItem(MyReviewWorkflowItem myReviewWorkflowItem) {
      super(myReviewWorkflowItem, AtsImage.REVIEW);
      this.reviewState = myReviewWorkflowItem.reviewState;
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException {

      Set<Artifact> assigned = AtsUtil.getAssigned(user);
      Set<Artifact> artifacts = new HashSet<Artifact>(50);
      // Because user can be assigned directly to review or through being assigned to task, add in
      // all the original artifacts.
      artifacts.addAll(assigned);

      if (reviewState == ReviewState.InWork) {
         artifacts.addAll(RelationManager.getRelatedArtifacts(assigned, 1, AtsRelationTypes.SmaToTask_Sma));
      } else {
         artifacts.addAll(ArtifactQuery.getArtifactListFromAttribute(AtsAttributeTypes.State,
            "%<" + user.getUserId() + ">%", AtsUtil.getAtsBranch()));
      }

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         if (artifact instanceof AbstractReviewArtifact) {
            if (reviewState == ReviewState.All || reviewState == ReviewState.InWork && !((AbstractWorkflowArtifact) artifact).isCancelledOrCompleted()) {
               artifactsToReturn.add(artifact);
            }
         }
      }
      return artifactsToReturn;
   }

   @Override
   public WorldUISearchItem copy() {
      return new MyReviewWorkflowItem(this);
   }

}
