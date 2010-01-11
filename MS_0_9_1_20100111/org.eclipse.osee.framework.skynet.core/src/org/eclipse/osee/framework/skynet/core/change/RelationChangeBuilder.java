/*
 * Created on Sep 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;

/**
 * @author Jeff C. Phillips
 */
public class RelationChangeBuilder extends ChangeBuilder {
   private int bArtId;
   private int relLinkId;
   private String rationale;
   private RelationType relationType;

   public RelationChangeBuilder(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionRecord toTransactionId, TransactionRecord fromTransactionId, ModificationType modType, ChangeType changeType, int bArtId, int relLinkId, String rationale, RelationType relationType, boolean isHistorical) {
      super(branch, artifactType, sourceGamma, artId, toTransactionId, fromTransactionId, modType, changeType,
            isHistorical);
      this.bArtId = bArtId;
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.relationType = relationType;
   }

   @Override
   public Change build(Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      Artifact bArtifact;

      if (isHistorical()) {
         bArtifact = ArtifactCache.getHistorical(bArtId, getToTransactionId().getId());
      } else {
         bArtifact = ArtifactCache.getActive(bArtId, branch);
      }
      return new RelationChange(branch, getArtifactType(), getSourceGamma(), getArtId(), getToTransactionId(),
            getFromTransactionId(), getModType(), getChangeType(), bArtId, relLinkId, rationale,
            relationType, isHistorical(), loadArtifact(), bArtifact);
   }

}
