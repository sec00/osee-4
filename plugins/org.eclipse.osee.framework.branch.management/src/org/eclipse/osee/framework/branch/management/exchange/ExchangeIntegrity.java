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
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.branch.management.exchange.transform.ExchangeDataProcessor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ExchangeIntegrity {
   private final OseeServices services;
   private final IOseeExchangeDataProvider exportDataProvider;
   private final ExchangeDataProcessor processor;
   private final List<ReferentialIntegrityConstraint> constraints = new ArrayList<ReferentialIntegrityConstraint>();
   private String verifyFile;

   public ExchangeIntegrity(OseeServices services, IOseeExchangeDataProvider exportDataProvider, ExchangeDataProcessor processor) {
      this.services = services;
      this.exportDataProvider = exportDataProvider;
      this.processor = processor;
   }

   public String getExchangeCheckFileName() {
      return verifyFile;
   }

   private void initializeConstraints() {
      ReferentialIntegrityConstraint constraint;

      constraint = new ReferentialIntegrityConstraint(ExportItem.OSEE_TX_DETAILS_DATA, "transaction_id");
      constraint.addForeignKey(ExportItem.OSEE_BRANCH_DATA, "parent_transaction_id", "baseline_transaction_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_DATA, "transaction_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_ARCHIVED_DATA, "transaction_id");
      constraint.addForeignKey(ExportItem.OSEE_MERGE_DATA, "commit_transaction_id");
      constraints.add(constraint);

      constraint = new ReferentialIntegrityConstraint(ExportItem.OSEE_ARTIFACT_DATA, "art_id");
      constraint.addForeignKey(ExportItem.OSEE_TX_DETAILS_DATA, "author", "commit_art_id");
      constraint.addForeignKey(ExportItem.OSEE_ATTRIBUTE_DATA, "art_id");
      constraint.addForeignKey(ExportItem.OSEE_RELATION_LINK_DATA, "a_art_id", "b_art_id");
      constraint.addForeignKey(ExportItem.OSEE_ARTIFACT_ACL_DATA, "art_id", "privilege_entity_id");
      constraint.addForeignKey(ExportItem.OSEE_BRANCH_ACL_DATA, "privilege_entity_id");
      constraints.add(constraint);

      constraint = new ReferentialIntegrityConstraint(ExportItem.OSEE_ARTIFACT_DATA, "gamma_id");
      constraint.addPrimaryKey(ExportItem.OSEE_ATTRIBUTE_DATA, "gamma_id");
      constraint.addPrimaryKey(ExportItem.OSEE_RELATION_LINK_DATA, "gamma_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_DATA, "gamma_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_ARCHIVED_DATA, "gamma_id");
      constraint.addForeignKey(ExportItem.OSEE_CONFLICT_DATA, "source_gamma_id");
      constraint.addForeignKey(ExportItem.OSEE_CONFLICT_DATA, "dest_gamma_id");
      constraints.add(constraint);

      constraint = new ReferentialIntegrityConstraint(ExportItem.OSEE_BRANCH_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_BRANCH_DATA, "parent_branch_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_ARCHIVED_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_TX_DETAILS_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_ARTIFACT_ACL_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_BRANCH_ACL_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_MERGE_DATA, "source_branch_id");
      constraint.addForeignKey(ExportItem.OSEE_MERGE_DATA, "dest_branch_id");
      constraint.addForeignKey(ExportItem.OSEE_MERGE_DATA, "merge_branch_id");
      constraint.addForeignKey(ExportItem.OSEE_CONFLICT_DATA, "merge_branch_id");
      constraints.add(constraint);
   }

   public void execute() throws OseeCoreException {
      long startTime = System.currentTimeMillis();

      initializeConstraints();

      Writer writer = null;
      try {
         writer = openResults();

         for (ReferentialIntegrityConstraint constraint : constraints) {
            OseeLog.logf(Activator.class, Level.INFO, "Verifing constraint [%s]", constraint.getPrimaryKeyListing());
            constraint.checkConstraint(services.getDatabaseService(), processor);
            writeConstraintResults(writer, constraint);
         }
         ExportImportXml.closeXmlNode(writer, ExportImportXml.DATA);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(writer);
         processor.cleanUp();
         OseeLog.logf(Activator.class, Level.INFO, "Verified [%s] in [%s]", exportDataProvider.getExportedDataRoot(),
            Lib.getElapseString(startTime));
      }
   }

   private void writeConstraintResults(Writer writer, ReferentialIntegrityConstraint constraint) throws IOException {
      HashCollection<String, Long> missingPrimaryKeys = constraint.getMissingPrimaryKeys();

      Set<Long> unreferencedPrimaryKeys = constraint.getUnreferencedPrimaryKeys();
      boolean passedCheck = missingPrimaryKeys.isEmpty() && unreferencedPrimaryKeys.isEmpty();

      writer.append("\t");
      ExportImportXml.openPartialXmlNode(writer, ExportImportXml.PRIMARY_KEY);
      ExportImportXml.addXmlAttribute(writer, ExportImportXml.ID, constraint.getPrimaryKeyListing());
      ExportImportXml.addXmlAttribute(writer, "status", passedCheck ? "OK" : "FAILED");

      if (passedCheck) {
         ExportImportXml.closePartialXmlNode(writer);
      } else {
         ExportImportXml.endOpenedPartialXmlNode(writer);
         writer.append("\t\t");
         ExportImportXml.openXmlNode(writer, ExportImportXml.UNREFERENCED_PRIMARY_KEY);
         Xml.writeAsCdata(writer, "\t\t\t" + unreferencedPrimaryKeys.toString());
         writer.append("\n\t\t");
         ExportImportXml.closeXmlNode(writer, ExportImportXml.UNREFERENCED_PRIMARY_KEY);

         for (String foreignKey : missingPrimaryKeys.keySet()) {
            writer.append("\t\t");
            ExportImportXml.openPartialXmlNode(writer, "ForeignKey");
            ExportImportXml.addXmlAttribute(writer, ExportImportXml.ID, foreignKey);
            ExportImportXml.endOpenedPartialXmlNode(writer);
            Xml.writeAsCdata(writer, "\t\t\t" + missingPrimaryKeys.getValues(foreignKey).toString());
            writer.append("\n\t\t");
            ExportImportXml.closeXmlNode(writer, "ForeignKey");
         }
         writer.append("\t");
         ExportImportXml.closeXmlNode(writer, ExportImportXml.PRIMARY_KEY);
      }
   }

   private Writer openResults() throws IOException {
      verifyFile = exportDataProvider.getExportedDataRoot().getName() + ".verify.xml";
      File writeLocation = exportDataProvider.getExportedDataRoot().getParentFile();

      Writer writer = ExchangeUtil.createXmlWriter(writeLocation, verifyFile, (int) Math.pow(2, 20));
      ExportImportXml.openXmlNode(writer, ExportImportXml.DATA);
      return writer;
   }
}