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
package org.eclipse.osee.framework.core.model;

import java.util.Date;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Jeff C. Phillips
 */
public class TransactionRecord extends BaseId implements TransactionToken, Adaptable {
   public static TransactionRecord SENTINEL = new TransactionRecord(Id.SENTINEL, BranchId.SENTINEL, null, null,
      UserId.SENTINEL, 0L, TransactionDetailsType.INVALID, 0L);
   private final TransactionDetailsType txType;
   private final BranchId branch;
   private String comment;
   private Date time;
   private UserId authorArtId;
   private Long commitArtId;
   private Long buildId;

   public TransactionRecord(Long id, BranchId branch, String comment, Date time, UserId authorArtId, Long commitArtId, TransactionDetailsType txType, Long buildId) {
   public TransactionRecord(Long id, BranchId branch, String comment, Date time, UserId author, Long commitArtId, TransactionDetailsType txType, , Long buildId) {
      super(id);
      this.branch = branch;
      this.buildId = buildId;
      this.comment = Strings.intern(comment);
      this.time = time;
      this.author = author;
      this.commitArtId = commitArtId;
      this.txType = txType;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public String getComment() {
      return comment;
   }

   public Date getTimeStamp() {
      return time;
   }

   public UserId getAuthor() {
      return author;
   }

   public Long getCommitArtId() {
      return commitArtId;
   }

   public TransactionDetailsType getTxType() {
      return txType;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setTimeStamp(Date time) {
      this.time = time;
   }

   public void setAuthor(UserId author) {
      this.author = author;
   }

   public void setCommit(Long commitArtId) {
      this.commitArtId = commitArtId;
   }

   public Long getBuildId() {
      return buildId;
   }

   public void setBuildId(Long buildId) {
      this.buildId = buildId;
   }
}