/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Shawn F. Cook
 */
public class UniqueNameRule extends AbstractValidationRule {

   private final IArtifactType artifactType;
   private final Collection<IdPair> idPairs = new LinkedList<>();

   public UniqueNameRule(IArtifactType artifactType) {
      this.artifactType = artifactType;
   }

   public boolean hasArtifactType(ArtifactType artType) {
      return artType.inheritsFrom(artifactType);
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;
      if (hasArtifactType(artToValidate.getArtifactType())) {
         // validate that no other artifact of the given Artifact Type has the same name.
         List<Artifact> arts = ArtifactQuery.getArtifactListFromTypeWithInheritence(artifactType,
            artToValidate.getBranch(), DeletionFlag.EXCLUDE_DELETED);
         for (Artifact art : arts) {
            if (art.getName().equalsIgnoreCase(artToValidate.getName()) && !art.getId().equals(
               artToValidate.getId()) && !hasIdPairAlreadyBeenEvaluated(art.getId(), artToValidate.getId())) {
               /**************************************************************************
                * Special case: Allow duplicate names of artifacts if<br/>
                * 1) Artifact name is numeric <br/>
                * 2) Artifact type is different<br/>
                */
               if (Strings.isNumeric(
                  artToValidate.getName()) && !artToValidate.getArtifactType().equals(art.getArtifactType())) {
                  continue;
               }
               /**************************************************************************
                * Allow for a Software Requirement parent to have an Implementation Details <br/>
                * child of the same name.
                */
               if (isImplementationDetailsChild(artToValidate, art) || isImplementationDetailsChild(art,
                  artToValidate)) {
                  continue;
               }
               errorMessages.add(ValidationReportOperation.getRequirementHyperlink(
                  artToValidate) + " and " + ValidationReportOperation.getRequirementHyperlink(
                     art) + " have same name value:\"" + artToValidate.getName() + " \"");
               validationPassed = false;
               addIdPair(art.getId(), artToValidate.getId());
            }
         }
      }
      return new ValidationResult(errorMessages, validationPassed);
   }

   private boolean isImplementationDetailsChild(Artifact childArtifact, Artifact parentArtifact) {
      return parentArtifact.getArtifactType().equals(CoreArtifactTypes.SoftwareRequirement) && //
         (childArtifact.isOfType(CoreArtifactTypes.AbstractImplementationDetails) && //
            childArtifact.getParent().equals(parentArtifact));
   }

   private void addIdPair(Long idA, Long idB) {
      idPairs.add(new IdPair(idA, idB));
   }

   private boolean hasIdPairAlreadyBeenEvaluated(Long idA, Long idB) {
      for (IdPair idPair : idPairs) {
         if (idPair.getIdA().equals(idA) && idPair.getIdB().equals(idB) || idPair.getIdA().equals(
            idB) && idPair.getIdB().equals(idA)) {
            return true;
         }
      }
      return false;
   }

   private class IdPair {
      private final Long idA, idB;

      public IdPair(Long idA, Long idB) {
         this.idA = idA;
         this.idB = idB;
      }

      public Long getIdA() {
         return idA;
      }

      public Long getIdB() {
         return idB;
      }
   }

   @Override
   public String getRuleDescription() {
      return "<b>Unique Names Check: </b>Ensure no two artifacts have the same name value";
   }

   @Override
   public String getRuleTitle() {
      return "Unique Names Check:";
   }
}
