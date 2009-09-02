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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchControlled;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.IAccessControllable;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Robert A. Fisher
 */
public class Branch implements Comparable<Branch>, IAdaptable, IAccessControllable {
   private static final int SHORT_NAME_LIMIT = 25;
   public static final String COMMON_BRANCH_CONFIG_ID = "Common";
   private final String branchGuid;
   private final int branchId;
   private Branch parentBranch;
   private TransactionId parentTransactionId;
   private final int parentTransactionIdNumber;
   private String branchName;
   private boolean archived;
   private final int authorId;
   private int associatedArtifactId;
   private Artifact associatedArtifact;
   private final Timestamp creationDate;
   private final String creationComment;
   private BranchType branchType;
   private Branch sourceBranch;
   private Branch destBranch;
   private BranchState branchState;

   public Branch(String branchName, String branchGuid, int branchId, Branch parentBranch, int parentTransactionIdNumber, boolean archived, int authorId, Timestamp creationDate, String creationComment, int associatedArtifactId, BranchType branchType, BranchState branchState) {
      this.branchId = branchId;
      this.branchGuid = branchGuid;
      this.branchName = branchName;
      this.parentBranch = parentBranch;
      this.parentTransactionIdNumber = parentTransactionIdNumber;
      this.archived = archived;
      this.authorId = authorId;
      this.creationDate = creationDate;
      this.creationComment = creationComment;
      this.associatedArtifactId = associatedArtifactId;
      this.associatedArtifact = null;
      this.branchType = branchType;
      this.branchState = branchState;
   }

   void internalSetBranchParent(Branch parentBranch) throws OseeStateException {
      if (parentBranch == null) {
         this.parentBranch = parentBranch;
      } else {
         throw new OseeStateException("parent branch cannot be set twice");
      }
   }

   public String getGuid() {
      return branchGuid;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   void setBranchState(BranchState branchState) {
      this.branchState = branchState;
   }

   /**
    * @return Returns the branchId.
    */
   public int getBranchId() {
      // Should we persist the branch automatically here if the branchId is 0 for cases the software
      // has freshly created a branch ?
      return branchId;
   }

   public String getName() {
      return branchName;
   }

   /**
    * updates this object's name (but does not affect the datastore)
    * 
    * @param branchName The branchName to set.
    */
   public void setName(String branchName) {
      this.branchName = branchName;
   }

   /**
    * @return Returns the short branch name if provided else returns null.
    */
   public String getShortName() {
      if (Strings.isValid(getName())) {
         return Strings.truncate(getName(), SHORT_NAME_LIMIT);
      } else {
         return Strings.emptyString();
      }
   }

   private void kickRenameEvents() throws OseeCoreException {
      OseeEventManager.kickBranchEvent(this, BranchEventType.Renamed, branchId);
   }

   /**
    * Sets the branch name to the given value and stores this change in the data-store
    * 
    * @param branchName The branchName to set.
    */
   public void rename(String branchName) throws OseeCoreException {
      setName(branchName);
      ConnectionHandler.runPreparedUpdate("UPDATE osee_branch SET branch_name = ? WHERE branch_id = ?", branchName,
            branchId);
      kickRenameEvents();
   }

   public void setAssociatedArtifact(Artifact artifact) throws OseeCoreException {
      // TODO: this method should allow the artifact to be on any branch, not just common
      if (artifact.getBranch() != BranchManager.getCommonBranch()) {
         throw new OseeArgumentException(
               "Setting associated artifact for branch only valid for common branch artifact.");
      }

      ConnectionHandler.runPreparedUpdate("UPDATE osee_branch SET associated_art_id = ? WHERE branch_id = ?",
            artifact.getArtId(), branchId);

      associatedArtifact = artifact;
      associatedArtifactId = artifact.getArtId();
   }

   @Override
   public String toString() {
      return getName();
   }

   public Branch getParentBranch() {
      return parentBranch;
   }

   public boolean hasParentBranch() {
      return getParentBranch() != null;
   }

   public boolean hasTopLevelBranch() {
      return !isTopLevelBranch();
   }

   /**
    * @return the top level branch that is an ancestor of this branch (which could be itself)
    */
   public Branch getTopLevelBranch() {
      Branch branchCursor = this;
      while (branchCursor.hasTopLevelBranch()) {
         branchCursor = branchCursor.getParentBranch();
      }
      return branchCursor;
   }

   public Collection<Branch> getChildBranches() throws OseeCoreException {
      return getChildBranches(false);
   }

   public Collection<Branch> getChildBranches(boolean recurse) throws OseeCoreException {
      Set<Branch> children = new HashSet<Branch>();
      getChildBranches(this, children, recurse);
      return children;
   }

   private void getChildBranches(Branch parentBranch, Collection<Branch> children, boolean recurse) throws OseeCoreException {
      for (Branch branch : BranchManager.getNormalBranches()) {
         if (branch.getParentBranchId() == parentBranch.getBranchId()) {
            children.add(branch);
            if (recurse) {
               getChildBranches(branch, children, recurse);
            }
         }
      }
   }

   /**
    * @return Returns all children branches including archived branches
    */
   public Collection<Branch> getDescendants() throws OseeCoreException {
      Set<Branch> children = new HashSet<Branch>();
      getAllChildBranches(this, children, true);

      return children;
   }

   private void getAllChildBranches(Branch parentBranch, Collection<Branch> children, boolean recurse) throws OseeCoreException {
      for (Branch branch : BranchManager.getNormalAllBranches()) {
         if (branch.getParentBranchId() == parentBranch.getBranchId()) {
            children.add(branch);
            if (recurse) {
               getChildBranches(branch, children, recurse);
            }
         }
      }
   }

   public List<Branch> getBranchHierarchy() throws OseeCoreException {
      List<Branch> ancestors = new LinkedList<Branch>();
      Branch branchCursor = this;
      ancestors.add(branchCursor);
      while (branchCursor.hasTopLevelBranch()) {
         branchCursor = branchCursor.getParentBranch();
         ancestors.add(branchCursor);
      }
      return ancestors;
   }

   public int getParentBranchId() {
      return hasParentBranch() ? getParentBranch().getBranchId() : -1;
   }

   public void archive() throws OseeCoreException {
      BranchManager.archive(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Branch) {
         return ((Branch) obj).branchId == branchId;
      }

      return false;
   }

   @Override
   public int hashCode() {
      return branchId * 13;
   }

   /**
    * @return Returns the archived.
    */
   public boolean isArchived() {
      return archived;
   }

   public void setArchived(boolean archived) {
      this.archived = archived;
   }

   public boolean hasChanges() throws OseeCoreException {
      Pair<TransactionId, TransactionId> transactions = TransactionIdManager.getStartEndPoint(this);
      return transactions.getFirst() != transactions.getSecond();
   }

   /**
    * @return the artifact id of the user who created the branch
    */
   public int getAuthorId() {
      return authorId;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public Date getCreationDate() {
      return creationDate;
   }

   public int compareTo(Branch branch) {
      return getName().compareToIgnoreCase(branch.getName());
   }

   /**
    * @return Returns the associatedArtifact.
    * @throws MultipleArtifactsExist
    * @throws ArtifactDoesNotExist
    * @throws OseeDataStoreException
    */
   public Artifact getAssociatedArtifact() throws OseeCoreException {
      if (associatedArtifact == null && associatedArtifactId > 0) {
         associatedArtifact = ArtifactQuery.getArtifactFromId(associatedArtifactId, BranchManager.getCommonBranch());
      }
      return associatedArtifact;
   }

   /**
    * Efficient way of determining if branch is associated cause it does not load the associated artifact
    * 
    * @param artifact
    */
   public boolean isAssociatedToArtifact(Artifact artifact) {
      return artifact.getArtId() == getAssociatedArtifactId();
   }

   public boolean isChangeManaged() {
      try {
         return associatedArtifactId != UserManager.getUser(SystemUser.OseeSystem).getArtId();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return true;
      }
   }

   /*
    * True if baseline or top level branch
    */
   public boolean isBaselineBranch() {
      return branchType.equals(BranchType.BASELINE);
   }

   public boolean isSystemRootBranch() {
      return branchType.equals(BranchType.SYSTEM_ROOT);
   }

   public boolean isTopLevelBranch() {
      return getParentBranch() != null && getParentBranch().isSystemRootBranch();
   }

   public BranchType getBranchType() {
      return branchType;
   }

   protected void setBranchType(int type) {
      branchType = BranchType.getBranchType(type);
   }

   public boolean isMergeBranch() {
      return branchType.equals(BranchType.MERGE);
   }

   public boolean isWorkingBranch() {
      return branchType.equals(BranchType.WORKING);
   }

   /**
    * @return the associatedArtifactId
    */
   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   public String asFolderName() {
      String branchName = this.getShortName();

      // Remove illegal filename characters
      // NOTE: The current program.launch has a tokenizing bug that causes an error if consecutive spaces are in the name
      branchName = branchName.replaceAll("[^A-Za-z0-9]", "_");
      branchName = Strings.truncate(branchName, 20).trim();

      return String.format("%s.%s", branchName.toLowerCase(), this.getBranchId());
   }

   public static int getBranchIdFromBranchFolderName(String folderName) throws Exception {
      return Integer.parseInt(Lib.getExtension(folderName));
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }

   public void setMergeBranchInfo(Branch sourceBranch, Branch destBranch) {
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
   }

   public boolean isMergeBranchFor(Branch sourceBranch, Branch destBranch) {
      return sourceBranch.equals(this.sourceBranch) && destBranch.equals(this.destBranch);
   }

   /**
    * @param branchTypes
    * @return whether this branch is of one of the specified branch types
    */
   public boolean isOfType(BranchType... branchTypes) {
      for (BranchType branchType : branchTypes) {
         if (this.branchType == branchType) {
            return true;
         }
      }
      return false;
   }

   public boolean matchesState(BranchArchivedState branchState) {
      return branchState == BranchArchivedState.ALL || isArchived() && branchState == BranchArchivedState.ARCHIVED || !isArchived() && branchState == BranchArchivedState.UNARCHIVED;
   }

   public boolean matchesControlled(BranchControlled branchControlled) {
      return branchControlled == BranchControlled.ALL || isChangeManaged() && branchControlled == BranchControlled.CHANGE_MANAGED || !isChangeManaged() && branchControlled == BranchControlled.NOT_CHANGE_MANAGED;
   }

   public void setDeleted() {
      setBranchState(BranchState.DELETED);
      try {
         OseeEventManager.kickBranchEvent(this, BranchEventType.Deleted, getBranchId());
      } catch (Exception ex) {
         // Do Nothing
      }
   }

   /**
    * @return the deleted
    */
   public boolean isDeleted() {
      return getBranchState() == BranchState.DELETED;
   }

   /**
    * @return Returns whether the branch is editable.
    */
   public boolean isEditable() {
      return !isCommitted() && !isRebaselined() && !isArchived() && !isDeleted();
   }

   public boolean isCommitted() {
      return getBranchState() == BranchState.COMMITTED;
   }

   public boolean isRebaselined() {
      return getBranchState() == BranchState.REBASELINED;
   }

   public boolean isRebaselineInProgress() {
      return getBranchState() == BranchState.REBASELINE_IN_PROGRESS;
   }

   /**
    * @return the parentTransactionId
    * @throws OseeCoreException
    */
   public TransactionId getParentTransactionId() throws OseeCoreException {
      if (parentTransactionId == null) {
         parentTransactionId = TransactionIdManager.getTransactionId(parentTransactionIdNumber);
      }
      return parentTransactionId;
   }

   public Collection<Branch> getWorkingBranches() throws OseeCoreException {
      return BranchManager.getWorkingBranches(this);
   }

   public int getParentTransactionNumber() {
      return parentTransactionIdNumber;
   }

   @Override
   public Branch getAccessControlBranch() {
      return this;
   }

   @Override
   public PermissionEnum getUserPermission(Artifact subject, PermissionEnum permission) {
      return null;
   }
}