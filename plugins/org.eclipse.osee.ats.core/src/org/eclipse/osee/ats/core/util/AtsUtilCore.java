/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.AttributeId;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilCore {

   public final static double DEFAULT_HOURS_PER_WORK_DAY = 8;
   private static IOseeBranch branch = null;

   public static IOseeBranch getAtsBranch() {
      if (branch == null) {
         String branchGuid = System.getProperty("AtsBranch");
         if (Strings.isValid(branchGuid)) {
            branch = TokenFactory.createBranch(branchGuid, "ATSBranch - fromConfig");
         }
         if (branch == null) {
            branch = CoreBranches.COMMON;
         }
      }
      System.err.println("ATS Branch " + branch);
      return branch;
   }

   public static void setBranch(String guid) {
      setBranch(TokenFactory.createBranch(guid, "Set Branch"));
   }

   public static void setBranch(IOseeBranch branch) {
      AtsUtilCore.branch = branch;
   }

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static String doubleToI18nString(double d) {
      return doubleToI18nString(d, false);
   }

   public static String doubleToI18nString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0) {
         return "";
      }
      // This enables java to use same string for all 0 cases instead of creating new one
      else if (d == 0) {
         return "0.00";
      } else {
         return String.format("%4.2f", d);
      }
   }

   public static ArtifactId toArtifactId(IAtsWorkItem workItem) {
      return new ArtifactIdWrapper(workItem);
   }

   public static AttributeId toAttributeId(IAttribute<?> attr) {
      return new AttributeIdWrapper(attr);
   }

   /**
    * @return true if ATS Config is stored as separeate artifacts. false if it comes from ATS xtext.
    */
   public static boolean isArtifactConfig() {
      boolean artifactConfig = true;
      String atsBranchGuid = System.getProperty("AtsBranch");
      if (!CoreBranches.COMMON.getGuid().equals(atsBranchGuid)) {
         artifactConfig = false;
      }
      return artifactConfig;
   }

}
