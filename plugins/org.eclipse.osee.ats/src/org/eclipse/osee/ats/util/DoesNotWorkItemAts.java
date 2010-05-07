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
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ATSNote;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.NoteItem;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.util.ElapsedTime;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - fixAtsNotesAndDefectItemsUserIds", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
         return;
      }
      convertAtsStateNotesAndDefectItemsUserIds();
      //      fixNotesStateNames();
      //      renameTransactionComments();
      //      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Admin Cleanup");
      //      Artifact verArt =
      //            ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, "0.9.0", AtsUtil.getAtsBranch());
      //      for (Attribute<?> attr : verArt.getAttributes()) {
      //         if (attr.getAttributeType().getName().equals(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName())) {
      //            System.out.println("next " + attr.getValue());
      //            attr.delete();
      //            break;
      //         }
      //      }
      //      verArt.persist(transaction);
      //      verArt = ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, "0.8.2", AtsUtil.getAtsBranch());
      //      for (Attribute<?> attr : verArt.getAttributes()) {
      //         if (attr.getAttributeType().getName().equals(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName())) {
      //            System.out.println("released " + attr.getValue());
      //            attr.delete();
      //            break;
      //         }
      //      }
      //      verArt.persist(transaction);
      //      transaction.execute();

      //      deleteDuplicateCommonBranchDuplicateRelations();
      //      convertAtsLogUserIds(transaction);
      //      deleteUnAssignedUserRelations();
      //      relateDonDunne();

      //      testDeleteAttribute();
      //      deleteNullUserAttributes();
      //      XNavigateItem item = AtsNavigateViewItems.getInstance().getSearchNavigateItems().get(1);
      //      System.out.println("Item " + item.getName());
      //      NavigateView.getNavigateView().handleDoubleClick(item);

      //      XResultData.runExample();

      // fixOseePeerReviews();

      AWorkbench.popup("Completed", "Complete");
   }

   @SuppressWarnings("unused")
   private void myWorldTimeTest() throws OseeCoreException {
      AtsBulkLoad.loadConfig(true);
      //      ElapsedTime time = new ElapsedTime("My World via Relations");
      //      Set<Artifact> assigned =
      //            RelationManager.getRelatedArtifacts(Arrays.asList(UserManager.getUser()), 1,
      //                  CoreRelationTypes.Users_Artifact);
      //      System.out.println("Returned " + assigned.size() + " objects");
      //      time.end();

      ElapsedTime time = new ElapsedTime("My World via Attribute Search");
      List<Artifact> assignedList =
            ArtifactQuery.getArtifactListFromAttribute(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
                  "%727536%", AtsUtil.getAtsBranch());
      System.out.println("Returned " + assignedList.size() + " objects");
      time.end();

   }

   @SuppressWarnings("unused")
   private void fixNotesStateNames() throws OseeCoreException {
      Map<String, Integer> valueToGammaId = new HashMap<String, Integer>();
      String SELECT_QUERY = "select * from osee_attribute where value like '%quot; State%'";
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_QUERY);
         while (chStmt.next()) {
            int gammaId = chStmt.getInt("gamma_id");
            String value = chStmt.getString("value");
            valueToGammaId.put(value, gammaId);
         }
      } finally {
         chStmt.close();
      }

      for (Entry<String, Integer> entry : valueToGammaId.entrySet()) {
         String value = entry.getKey();
         int gammaId = entry.getValue();
         System.out.println("Old GammaId " + gammaId + " Value = " + value);
         List<NoteItem> items = ATSNote.getNoteItems(value, "GammaId:" + gammaId);
         for (NoteItem item : items) {
            System.out.println(String.format("Pre State [%s]", item.getState()));
         }

         // Make changes
         value = value.replaceAll("\\&quot; State", "");
         value = value.replaceAll("state=\"\\&quot;", "state=\"");
         System.out.println("New GammaId " + gammaId + " Value = " + value);
         items = ATSNote.getNoteItems(value, "GammaId:" + gammaId);
         for (NoteItem item : items) {
            System.out.println(String.format("Post State [%s]", item.getState()));
         }
         //         ConnectionHandler.runPreparedUpdate(UPDATE_QUERY, value, gammaId);
      }
   }

   private void convertAtsStateNotesAndDefectItemsUserIds() throws OseeCoreException {
      String SELECT_QUERY = "select * from osee_attribute where value like ? and attr_type_id in (84,92,72,73)";
      Map<String, String> oldIdToNewId = new HashMap<String, String>();
      oldIdToNewId.put("<va053c>", "<1588621>");
      oldIdToNewId.put("<fw314c>", "<1631765>");

      for (Entry<String, String> entry : oldIdToNewId.entrySet()) {

         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(SELECT_QUERY, "%" + entry.getKey() + "%");
            while (chStmt.next()) {
               int gammaId = chStmt.getInt("gamma_id");
               String value = chStmt.getString("value");
               System.out.println("Old " + gammaId + " Value = " + value);
               value = value.replaceAll(entry.getKey(), entry.getValue());
               System.out.println("New " + gammaId + " Value = " + value);
               //               ConnectionHandler.runPreparedUpdate(UPDATE_QUERY, value, gammaId);
            }
         } finally {
            chStmt.close();
         }

      }
   }

   @SuppressWarnings("unused")
   private void renameTransactionComments() throws OseeCoreException {
      List<String> notRenamed = new ArrayList<String>();
      Map<String, Integer> commentToTransId = new HashMap<String, Integer>();
      String SEARCH_TRANSACTION_COMMENTS =
            "select transaction_id, osee_comment from osee_tx_details where osee_comment like '%Commit%'";

      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SEARCH_TRANSACTION_COMMENTS);
         while (chStmt.next()) {
            int transId = chStmt.getInt("transaction_id");
            String comment = chStmt.getString("osee_comment");
            commentToTransId.put(comment, transId);
         }
      } finally {
         chStmt.close();
      }

      for (Entry<String, Integer> entry : commentToTransId.entrySet()) {
         String comment = entry.getKey();
         Pattern traxPattern = Pattern.compile("^(.*? - TRAX RPCR .*)( - TRAX RPCR .*)$");
         Pattern shortPattern = Pattern.compile("^(.*? -) +(.*?) - +(.*?)$");
         System.out.println("TransId " + entry.getValue() + " Comment = " + comment);

         boolean renamed = false;
         Matcher m = traxPattern.matcher(comment);
         if (m.find()) {
            String first = m.group(1);
            m.group(2);
            String newComment = first;
            if (!comment.equals(newComment)) {
               renamed = true;
               System.out.println("TransId " + entry.getValue() + " Rename1 = " + newComment);
               //               TransactionManager.setTransactionComment(TransactionManager.getTransactionId(entry.getValue()),
               //                     newComment);
            }
         }
         if (!renamed) {
            m = shortPattern.matcher(comment);
            if (m.find()) {
               String prefix = m.group(1);
               String first = m.group(2);
               String second = m.group(3);
               second = second.replaceFirst("...$", "");
               String newComment = prefix + " " + first;
               if (first.equals(second) || first.startsWith(second)) {
                  renamed = true;
                  System.out.println("TransId " + entry.getValue() + " Rename2 = " + newComment);
                  //                  TransactionManager.setTransactionComment(TransactionManager.getTransactionId(entry.getValue()),
                  //                        newComment);
               }
            }
         }
         if (!renamed) {
            notRenamed.add(comment);
         }

      }
      for (String branchName : notRenamed) {
         System.out.println("Not Renamed " + branchName);
      }

   }

   @SuppressWarnings("unused")
   private void renameBranches() throws OseeCoreException {
      List<String> notRenamed = new ArrayList<String>();
      Pattern traxPattern = Pattern.compile("^(..... - TRAX RPCR .*)( - TRAX RPCR .*)$");
      Pattern shortPattern = Pattern.compile("^(..... -) +(.*?) - +(.*?)$");
      Collection<Branch> branches = BranchManager.getBranches(BranchArchivedState.ALL, BranchType.WORKING);
      int x = 0;
      for (Branch branch : branches) {
         System.out.println(String.format("\n%d/%d - Branch = [%s]", x++, branches.size(), branch));
         String branchName = branch.getName();
         boolean renamed = false;
         Matcher m = traxPattern.matcher(branch.getName());
         if (m.find()) {
            String first = m.group(1);
            m.group(2);
            String newBranchName = first;
            if (!branchName.equals(newBranchName)) {
               renamed = true;
               System.out.println("Rename = " + newBranchName);
               branch.setName(newBranchName);
               BranchManager.persist(branch);
            }
         }
         if (branchName.contains("BGJ6A")) {
            System.out.println("here");
         }
         if (!renamed) {
            m = shortPattern.matcher(branch.getName());
            if (m.find()) {
               String prefix = m.group(1);
               String first = m.group(2);
               String second = m.group(3);
               second = second.replaceFirst("...$", "");
               String newBranchName = prefix + " " + first;
               if (first.equals(second) || first.startsWith(second)) {
                  renamed = true;
                  System.out.println("Rename = " + newBranchName);
                  branch.setName(newBranchName);
                  BranchManager.persist(branch);
               }
            }
         }
         if (!renamed) {
            notRenamed.add(branchName);
         }

      }
      for (String branchName : notRenamed) {
         System.out.println("Not Renamed " + branchName);
      }

   }

   @SuppressWarnings("unused")
   private void deleteDuplicateCommonBranchDuplicateRelations() throws OseeCoreException {
      XResultData rd = new XResultData();
      rd.log(getName());
      int listsCount = 0;
      int artifactsCount = 0;
      boolean fix = true;
      SkynetTransaction transaction =
            new SkynetTransaction(AtsUtil.getAtsBranch(), "Delete duplicate common branch relations");

      // Break artifacts into blocks so don't run out of memory
      List<Collection<Integer>> artIdLists = ValidateAtsDatabase.loadAtsBranchArtifactIds(rd, null);
      for (Collection<Integer> artIdList : artIdLists) {
         System.out.println(String.format("Processing artifacts from list %d/%d", listsCount++, artIdLists.size()));
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFromIds(artIdList, AtsUtil.getAtsBranch());
         artifactsCount += artifacts.size();

         for (Artifact artifact : artifacts) {
            List<RelationLink> relLinks = artifact.getRelationsAll(false);
            for (RelationLink relLink : relLinks) {
               // Only handle relations where this artifact is SIDEA (so don't handle same relation twice)
               if (!relLink.getArtifactA().equals(artifact)) {
                  continue;
               }
               // Find all duplicates
               List<RelationLink> duplicates = new ArrayList<RelationLink>();
               duplicates.add(relLink);
               for (RelationLink otherRelLink : relLinks) {
                  if (relLink == otherRelLink) {
                     continue;
                  }
                  if (relLink.equalsConceptually(otherRelLink)) {
                     duplicates.add(otherRelLink);
                  }
               }
               // If more than one, there are duplicates
               if (duplicates.size() > 1) {
                  handleDuplicates(rd, artifact, duplicates, fix, transaction);
               }
            }
         }
      }
      transaction.execute();
      rd.report(getName());
   }

   /**
    * For set of duplicate relations, keep first one and delete rest
    */
   private void handleDuplicates(XResultData rd, Artifact artifact, List<RelationLink> relLinks, boolean fix, SkynetTransaction transaction) throws OseeCoreException {
      Integer firstId = null;
      String firstName = null;
      String str = relLinks.size() + " duplicate relations found for [" + artifact.getHumanReadableId() + "] ";
      for (RelationLink relLink : relLinks) {
         if (firstId == null) {
            firstId = relLink.getId();
            firstName = relLink.getArtifactB().getName();
         } else if (relLink.getId() != firstId && relLink.getArtifactB().getName().equals(firstName)) {
            str += " Deleteable";
            if (fix) {
               relLink.delete(false);
               artifact.persist(transaction);
            }
         }
         str += "[" + relLink.getArtifactB().getName() + "(" + relLink.getId() + ")]";
      }
      System.out.println(str);
      if (fix) {
         rd.log("fixed");
      }
      rd.log(str);
   }

   @SuppressWarnings("unused")
   private void purgeHrids() throws OseeCoreException {
      String[] hrids = AFile.readFile("O:\\hrids_to_delete.txt").split("\r\n");
      int x = 0;
      for (String hrid : hrids) {
         hrid = hrid.replaceAll(" ", "");
         System.out.println("Processing " + x++ + " of " + hrids.length);
         try {
            Artifact art = null;
            // Handle case where duplicate hrids
            try {
               art = ArtifactQuery.getArtifactFromId(hrid, AtsUtil.getAtsBranch());
            } catch (MultipleArtifactsExist ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               continue;
            }
            Set<Artifact> artsToDelete = new HashSet<Artifact>();
            if (art instanceof TeamWorkFlowArtifact) {
               art = ((TeamWorkFlowArtifact) art).getParentActionArtifact();
            }
            if (art instanceof ActionArtifact) {
               artsToDelete.add(art);
               artsToDelete.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
            }
            if (artsToDelete.size() > 0) {
               System.out.println("Sleeping 5 sec...");
               Thread.sleep(5000);
               System.out.println("Purging " + artsToDelete.size() + " artifacts...");
               new PurgeArtifacts(artsToDelete).execute();
            }
         } catch (ArtifactDoesNotExist ex) {
            System.out.println("Artifact with hrid does not exist: " + hrid);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

      }

   }

   @SuppressWarnings("unused")
   private void convertAtsLogUserIds(SkynetTransaction transaction) throws OseeCoreException {
      List<String> hrids =
            Arrays.asList("NKYBF", "J1Z48", "ZY4W5", "U9H58", "9713S", "83XVW", "59B9X", "TQD1J", "UVM7U", "HZT73",
                  "C49Q5", "RHCPY", "MBCXV", "YJFKC", "2F461", "AGW15", "K6ZGD", "9W45V", "GG43L", "G2VTQ", "CVWFC",
                  "EXMT0", "W1TS8", "JM3RD", "7Q0W3", "P9DKR", "BR2RN", "Z6B0Z", "6KT6U", "HPQJX", "QN2K3", "W0VTD",
                  "LDJMH", "6PWYH", "T8B4K", "YTNLC", "9557A", "SQQ6T", "D82X9", "2P5GC", "YK58N", "LWVT1", "KCGSQ",
                  "5X2WL", "C8HWW");
      for (Artifact art : ArtifactQuery.getArtifactListFromIds(hrids, AtsUtil.getAtsBranch())) {
         String str = art.getSoleAttributeValue(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), null);
         str = str.replaceAll("rj236c", "1779483");
         art.setSoleAttributeFromString(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), str);
         art.persist(transaction);
      }
   }

   @SuppressWarnings("unused")
   private void fixTestTaskResolutions() throws OseeCoreException {
      System.out.println("Started fixTestTaskResolutions...");
      for (Artifact artifact : ArtifactQuery.getArtifactListFromAttributeType(AtsAttributeTypes.Resolution,
            AtsUtil.getAtsBranch())) {
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArt = (TaskArtifact) artifact;
            String resolution =
                  ((TaskArtifact) artifact).getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(),
                        null);
            if (resolution == null) {
               System.err.println("Unexpected null resolution." + taskArt.getHumanReadableId());
               //               taskArt.deleteSoleAttribute(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
               //               taskArt.persistAttributes();
            } else {
               String newResolution = null;
               if (resolution.equals("Need_DTE_Test")) {
                  System.out.println("Rename Need_DTE_Test to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               } else if (resolution.equals("Awaiting_Code_Fix")) {
                  System.out.println("Rename Awaiting_Code_Fix to Awaiting_Code " + taskArt.getHumanReadableId());
                  newResolution = "Awaiting_Code";
               } else if (resolution.equals("Awaiting_Review")) {
                  System.out.println("Rename Awaiting_Review to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               } else if (resolution.equals("Unit_Tested")) {
                  System.out.println("Rename Unit_Tested to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               }
               if (newResolution != null) {
                  taskArt.setSoleAttributeFromString(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), newResolution);
                  taskArt.persist();
               }
            }
         }
      }
      System.out.println("Completed fixTestTaskResolutions...");
   }

   //   private void deleteUnAssignedUserRelations() throws OseeCoreException {
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsUtil.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
   //            User unassignedUser = SkynetAuthentication.getUser(UserEnum.UnAssigned);
   //            for (Artifact art : unassignedUser.getRelatedArtifacts(CoreRelationEnumeration.Users_Artifact)) {
   //               if (art instanceof StateMachineArtifact) {
   //                  unassignedUser.deleteRelation(CoreRelationEnumeration.Users_Artifact, art);
   //               }
   //            }
   //            unassignedUser.persistRelations();
   //         }
   //      };
   //      newActionTx.execute();
   //   }

   //   private static final boolean fixIt = false;

   //   public void cleanXViewerCustomizations() throws OseeCoreException {
   //      for (User user : SkynetAuthentication.getUsers()) {
   //         System.out.println("User: " + user);
   //
   //         SkynetUserArtifactCustomizeDefaults custDefaults = new SkynetUserArtifactCustomizeDefaults(user);
   //
   //         // Get all customizations
   //         List<String> customizations = user.getAttributesToStringList("XViewer Customization");
   //         if (customizations.size() == 0 && custDefaults.size() == 0) continue;
   //         Set<String> validGuids = new HashSet<String>();
   //         int currNumDefaults = custDefaults.getGuids().size();
   //         for (String custStr : new CopyOnWriteArrayList<String>(customizations)) {
   //            CustomizeData custData = new CustomizeData(custStr);
   //            validGuids.add(custData.getGuid());
   //
   //            // check for old customizations to remove
   //            boolean orderFound = custStr.contains("<order>");
   //            boolean namespaceNullFound = custStr.contains("namespace=\"null\"");
   //            if (orderFound || namespaceNullFound) {
   //               System.err.println("Removing " + (orderFound ? "<order>" : "namespace==null") + " customizations " + custData.getGuid());
   //               validGuids.remove(custData.getGuid());
   //               custDefaults.removeDefaultCustomization(custData);
   //               customizations.remove(custStr);
   //            } else {
   //               // Check for sort columns that are hidden
   //               for (String columnName : custData.getSortingData().getSortingNames()) {
   //                  XViewerColumn xCol = custData.getColumnData().getXColumn(columnName);
   //                  if (xCol == null) {
   //                     System.err.println("sort column not found \"" + columnName + "\" - " + custData.getGuid());
   //                  } else if (xCol.isShow() == false) {
   //                     System.err.println("sort col is hidden \"" + columnName + "\" - " + custData.getGuid());
   //                  }
   //               }
   //            }
   //         }
   //         if (validGuids.size() != custDefaults.getGuids().size()) {
   //            System.err.println("Update default customizations : " + user + " - " + currNumDefaults + " valid: " + validGuids.size());
   //            custDefaults.setGuids(validGuids);
   //         }
   //         if (fixIt) {
   //            custDefaults.save();
   //            user.setAttributeValues("XViewer Customization", customizations);
   //            user.persistAttributes();
   //         }
   //      }
   //   }

   public Result isCustomizationSortErrored(String custDataStr, CustomizeData custData) {

      return Result.TrueResult;
   }

   //   String xViewerDefaults = user.getSoleAttributeValueAsString("XViewer Defaults", null);
   //   // Get all current default guids
   //   Set<String> currentDefaultGuids = new HashSet<String>();
   //   if (xViewerDefaults != null) {
   //      for (String guid : AXml.getTagDataArray(xViewerDefaults, XVIEWER_DEFAULTS_TAG)) {
   //         if (guid != null && !guid.equals("")) {
   //            currentDefaultGuids.add(guid);
   //         }
   //      }
   //   }
   //   private void relateDonDunne()throws OseeCoreException{
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsUtil.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork()throws OseeCoreException{
   //            for (Artifact art : ArtifactQuery.getArtifactsFromAttribute(
   //                  ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
   //                  "%<" + SkynetAuthentication.getUser().getUserId() + ">%", AtsUtil.getAtsBranch())) {
   //               if ((art instanceof StateMachineArtifact) && ((StateMachineArtifact) art).getStateMgr().getAssignees().contains(
   //                     SkynetAuthentication.getUser())) {
   //                  art.addRelation(CoreRelationEnumeration.Users_User, SkynetAuthentication.getUser());
   //               }
   //            }
   //            SkynetAuthentication.getUser().persistRelations();
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }

   //   private void testDeleteAttribute() throws OseeCoreException {
   //      Artifact art =
   //            ArtifactQuery.getArtifactsFromIds(Arrays.asList("76589"), AtsUtil.getAtsBranch()).iterator().next();
   //      for (Attribute<?> attr : art.getAttributes()) {
   //         if (attr.getValue() == null) {
   //            System.out.println(art.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //            attr.delete();
   //         }
   //      }
   //      art.persistAttributes();
   //   }

   //   private void deleteNullAttributes() throws OseeCoreException {
   //
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsUtil.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
   //            int x = 0;
   //            for (String artTypeName : Arrays.asList(TeamWorkFlowArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
   //                  DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
   //                  "Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow", "Lba V13 Req Team Workflow",
   //                  "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
   //                  "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
   //                  "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
   //                  "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {
   //               for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsUtil.getAtsBranch())) {
   //                  for (Attribute<?> attr : team.getAttributes(false)) {
   //                     if (attr.getValue() == null) {
   //                        System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //                        attr.delete();
   //                        x++;
   //                     }
   //                  }
   //                  if (team.isDirty()) team.persistAttributes();
   //               }
   //            }
   //            System.out.println("Deleted " + x);
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }
   //
   //   private void deleteNullUserAttributes() throws OseeCoreException {
   //
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsUtil.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
   //            int x = 0;
   //            for (String artTypeName : Arrays.asList(User.ARTIFACT_NAME)) {
   //               for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsUtil.getAtsBranch())) {
   //                  for (Attribute<?> attr : team.getAttributes(false)) {
   //                     if (attr.getValue() == null) {
   //                        System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //                        attr.delete();
   //                        x++;
   //                     }
   //                  }
   //                  if (team.isDirty()) team.persistAttributes();
   //               }
   //            }
   //            System.out.println("Deleted " + x);
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }

   //   for (String artTypeName : Arrays.asList(TeamWorkFlowArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
   //         DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
   //         "Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow", "Lba V13 Req Team Workflow",
   //         "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
   //         "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
   //         "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
   //         "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {

}
