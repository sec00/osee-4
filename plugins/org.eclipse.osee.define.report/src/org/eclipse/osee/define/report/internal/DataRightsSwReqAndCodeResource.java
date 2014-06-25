/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Ryan D. Brooks
 */
@Path("/traceability/datarights")
public final class DataRightsSwReqAndCodeResource {
   private final OrcsApi orcsApi;
   private final IResourceRegistry resourceRegistry;
   private final Map<String, Object> properties;
   private final Log logger;
   private final QueryFactory queryFactory;

   public DataRightsSwReqAndCodeResource(Log logger, Map<String, Object> properties, IResourceRegistry resourceRegistry, OrcsApi orcsApi) {
      this.properties = properties;
      this.resourceRegistry = resourceRegistry;
      this.orcsApi = orcsApi;
      queryFactory = orcsApi.getQueryFactory(null);
      this.logger = logger;
   }

   /**
    * Produce the SRS Trace Report
    * 
    * @param branch The Branch uuid to run the SRS Trace Report on.
    * @param codeRoot The root directory accessible on the server for the code traces.
    * @param csci The desired CSCI.
    * @param traceType The desired trace type.
    * @return Produces a streaming xml file containing the SRS Trace Report
    */
   @Path("gen")
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getDataRightspReport(@QueryParam("branch") Long branchUuid, @QueryParam("code_root") String codeRoot) {
      TraceMatch match = new TraceMatch("\\^SRS\\s*([^;]+);?", null);
      TraceAccumulator traceAccumulator = new TraceAccumulator(".*\\.(java|ada|ads|adb|c|h)", match);
      StreamingOutput streamingOutput =
         new DataRightsStreamingOutput(orcsApi, branchUuid, codeRoot, traceAccumulator, logger);

      ResponseBuilder builder = Response.ok(streamingOutput);
      String fileName = "Req_Code_Data_Rights_Trace_Report.xml";
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   /**
    * Provides the user interface for the System Safety Report
    * 
    * @return Returns the html page for the System Safety Report
    */
   @Path("ui")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getApplet() {
      OseeAppletPage pageUtil = new OseeAppletPage(queryFactory.branchQuery());
      return pageUtil.realizeApplet(resourceRegistry, "dataRightsReport.html", getClass());
   }

   private static final IArtifactType WCAFE = TokenFactory.createArtifactType(0x0000BA000000001FL, "WCAFE");

   @Path("check")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String check() {
      ResultSet<ArtifactReadable> results =
         queryFactory.fromBranch(482760509616967553L).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement).getResults();

      StringBuilder strb = new StringBuilder(2000);
      int count = 0;
      for (ArtifactReadable art : results) {
         if (art.isOfType(WCAFE)) {
            continue;
         }
         String classification = art.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "");
         String subsystem = art.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
         String sme = art.getSoleAttributeValue(CoreAttributeTypes.SubjectMatterExpert, "");
         if ((subsystem.equals("Controls and Displays") || subsystem.equals("Mission System Management") || subsystem.equals("Data Management") || subsystem.equals("Unmanned Systems Management"))) {
            if (classification.isEmpty()) {
               appendDetails("missing classification", strb, art, subsystem, classification);
            }
            if (sme.equals("")) {
               appendDetails("missing sme", strb, art, subsystem, classification);
            }
            count++;
         }
      }
      strb.append("done" + count);
      return strb.toString();
   }

   @Path("copyV22toV4")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String copySSDDrightsIntoV4SBVT() {
      long sourceBranch = 196577889L; // Block III V2.2 SBVT
      long destinationBranch = 5059327723596941818L; // Block III V4 SBVT SSDD Data Rights Classification

      ResultSet<ArtifactReadable> results =
         queryFactory.fromBranch(destinationBranch).andIsOfType(CoreArtifactTypes.SubsystemDesign).getResults();

      TransactionBuilder txBuilder =
         createTxBuilder("Copy SSDD data rights from Block III V2.2 SBVT", destinationBranch);

      StringBuilder strb = new StringBuilder(2000);
      int count = 0;
      for (ArtifactReadable dest : results) {
         ArtifactReadable source =
            queryFactory.fromBranch(sourceBranch).andUuid(dest.getLocalId()).getResults().getAtMostOneOrNull();
         if (source == null) {
            String classification = dest.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "");
            String subsystem = dest.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
            appendDetails("missing source", strb, dest, subsystem, classification);
         } else {
            copyIfValid(txBuilder, source, dest, CoreAttributeTypes.DataRightsClassification);
            if (source.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "").isEmpty()) {
               txBuilder.setSoleAttributeValue(dest, CoreAttributeTypes.DataRightsClassification, "Unspecified");
            }
            copyIfValid(txBuilder, source, dest, CoreAttributeTypes.SubjectMatterExpert);
            copyIfValid(txBuilder, source, dest, CoreAttributeTypes.DataRightsBasis);
            count++;
         }
      }
      txBuilder.commit();

      strb.append("done" + count);
      return strb.toString();
   }

   @Path("updaterights")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String updateDataRights() throws Exception {
      long sourceBranch = 11190; // Block III V2 SBVT 2.1.1
      long destinationBranch = 2395311045230317474L; // Korea FTB1 SRS Data Rights Classification

      return updateDataRights(sourceBranch, destinationBranch, CoreArtifactTypes.AbstractSoftwareRequirement);
   }

   private String updateDataRights(long sourceBranch, long destinationBranch, IArtifactType artifactType) throws Exception {
      ResultSet<ArtifactReadable> results =
         queryFactory.fromBranch(destinationBranch).andIsOfType(artifactType).getResults();

      String branchName =
         orcsApi.getQueryFactory(null).branchQuery().andUuids(sourceBranch).getResults().getExactlyOne().getName();

      String txMsg = "Copy data rights for " + artifactType + " from " + branchName;
      TransactionBuilder txBuilder = createTxBuilder(txMsg, destinationBranch);

      StringBuilder strb = new StringBuilder(2000);
      int count = 0;
      for (ArtifactReadable dest : results) {
         if (dest.isOfType(WCAFE)) {
            continue;
         }

         ArtifactReadable source =
            queryFactory.fromBranch(sourceBranch).andUuid(dest.getLocalId()).getResults().getAtMostOneOrNull();
         if (source == null) {
            String classification = dest.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "");
            String subsystem = dest.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
            appendDetails("missing source", strb, dest, subsystem, classification);
         } else {
            setBestValue(txBuilder, source, dest, CoreAttributeTypes.DataRightsClassification);
            setBestValue(txBuilder, source, dest, CoreAttributeTypes.SubjectMatterExpert);
            setBestValue(txBuilder, source, dest, CoreAttributeTypes.DataRightsBasis);
            count++;
         }
      }
      txBuilder.commit();

      strb.append("done: " + count);
      return strb.toString();
   }

   private void copyIfValid(TransactionBuilder txBuilder, ArtifactReadable source, ArtifactReadable dest, IAttributeType attributeType) {
      String value = source.getSoleAttributeValue(attributeType, "");
      if (!value.isEmpty()) {
         txBuilder.setSoleAttributeValue(dest, attributeType, value);
      }
   }

   private void appendDetails(String msg, StringBuilder strb, ArtifactReadable art, String subsystem, String classification) {
      strb.append(msg + "|" + subsystem + "|" + classification + " |" + art.getArtifactType() + "| " + art.getName() + "| " + art.getLocalId() + "| " + art.getLastModifiedTransaction() + "<br />");
   }

   private TransactionBuilder createTxBuilder(String comment, long branchId) {
      TransactionFactory txFactory = orcsApi.getTransactionFactory(null);
      ArtifactReadable userArtifact =
         orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andUuid(50).getResults().getExactlyOne();

      return txFactory.createTransaction(branchId, userArtifact, comment);
   }

   @Path("1")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String missingSME() {
      ResultSet<ArtifactReadable> results =
      //         queryFactory.fromBranch(1573337143).and(CoreAttributeTypes.DataRightsClassification, "Restricted Rights").getResults();
      //   queryFactory.fromBranch(2089598753442167976L).andExists(CoreAttributeTypes.DataRightsClassification).getResults();
         queryFactory.fromBranch(2089598753442167976L).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement).getResults();

      StringBuilder strb = new StringBuilder(2000);
      int count = 0;
      //      TransactionBuilder txBuilder = createTxBuilder("Set Subject Matter Expert");

      for (ArtifactReadable req : results) {
         if (req.isOfType(WCAFE)) {
            continue;
         }
         String classification = req.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "");
         String subsystem = req.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
         String sme = req.getSoleAttributeValue(CoreAttributeTypes.SubjectMatterExpert, "");
         if ((classification.isEmpty() || classification.equals("Unspecified")) && (subsystem.equals("Controls and Displays") || subsystem.equals("Mission System Management") || subsystem.equals("Data Management") || subsystem.equals("Unmanned Systems Management"))) {
            strb.append(subsystem + "|" + classification + " |" + req.getArtifactType() + "| " + req.getName() + "| " + req.getGuid() + "<br />");
            if (false) {
               int recentTx = req.getLastModifiedTransaction();
               TransactionReadable tx;
               if (recentTx == 1288474) {
                  ArtifactReadable source =
                     queryFactory.fromBranch(15993).andUuid(req.getLocalId()).getResults().getExactlyOne();
                  tx =
                     queryFactory.transactionQuery().andTxId(source.getLastModifiedTransaction()).getResults().getExactlyOne();
               } else {
                  tx = queryFactory.transactionQuery().andTxId(recentTx).getResults().getExactlyOne();
               }
               String author = getAuthor(tx);

               if (req.isAttributeTypeValid(CoreAttributeTypes.SubjectMatterExpert)) {
                  //               txBuilder.setAttributesFromValues(req, CoreAttributeTypes.SubjectMatterExpert, author);
               }
            }

            count++;
         }
      }

      //      txBuilder.commit();

      strb.append("<br />done: " + count);
      return strb.toString();
   }

   @Path("ssd")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String copySddDataRightsToV2_2() throws Exception {
      //branch is Block III V2.2 SDD Data Rights Classification
      long destinationBranch = 482760509616967553L;
      ResultSet<ArtifactReadable> results =
         queryFactory.fromBranch(destinationBranch).andIsOfType(CoreArtifactTypes.SubsystemDesign).getResults();

      StringBuilder strb = new StringBuilder(2000);
      int count = 0;
      TransactionBuilder txBuilder = createTxBuilder("Set Subject Matter Expert", destinationBranch);

      long rpcr19378 = 15993;
      long ssddJeffPublish = 1887579420;
      for (ArtifactReadable art : results) {
         String classification = art.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "");
         String subsystem = art.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
         String sme = art.getSoleAttributeValue(CoreAttributeTypes.SubjectMatterExpert, "");

         if (sme.isEmpty() || sme.equals("Unspecified")) {
            ArtifactReadable source1 =
               queryFactory.fromBranch(rpcr19378).andUuid(art.getLocalId()).getResults().getAtMostOneOrNull();
            ArtifactReadable source2 =
               queryFactory.fromBranch(ssddJeffPublish).andUuid(art.getLocalId()).getResults().getAtMostOneOrNull();
            if (source1 == null && source2 == null) {
               appendDetails("missing artifact", strb, art, subsystem, classification);
               continue;
            }

            String srcRights = getBestValue(source1, source2, CoreAttributeTypes.DataRightsClassification);
            if (srcRights.equals("Unspecified")) {
               appendDetails("rights unspecified", strb, art, subsystem, classification);
            } else {
               txBuilder.setAttributesFromValues(art, CoreAttributeTypes.DataRightsClassification, srcRights);

               String srcSme = getBestValue(source1, source2, CoreAttributeTypes.SubjectMatterExpert);
               if (srcSme.equals("Unspecified")) {
                  String author = getAuthorFromSource(getBestSource(source1, source2));
                  txBuilder.setAttributesFromValues(art, CoreAttributeTypes.SubjectMatterExpert, author);
               } else {
                  strb.append("found sme|" + subsystem + "|" + classification + " |" + art.getArtifactType() + "| " + art.getName() + "| " + art.getLocalId() + "| " + art.getLastModifiedTransaction() + "<br />");
               }
            }
         } else {
            strb.append("sme already set|" + subsystem + "|" + classification + " |" + art.getArtifactType() + "| " + art.getName() + "| " + art.getLocalId() + "| " + art.getLastModifiedTransaction() + "<br />");
         }
      }
      txBuilder.commit();
      strb.append("<br />done: " + count);
      return strb.toString();
   }

   private String getBestValue(ArtifactReadable source1, ArtifactReadable source2, IAttributeType attributeType) throws Exception {
      if (source1 == null && source2 == null) {
         throw new Exception("how did this happen?");
      }
      String value = "Unspecified";
      if (source1 == null) {
         value = source2.getSoleAttributeValue(attributeType, "Unspecified");
      } else {
         value = source1.getSoleAttributeValue(attributeType, "Unspecified");
         if (source2 != null) {
            String value2 = source2.getSoleAttributeValue(attributeType, "Unspecified");
            if (!value2.equals("Unspecified")) {
               if (!value.equals("Unspecified")) {
                  System.out.println(attributeType + " conflict");
               } else {
                  value = value2;
               }
            }
         }
      }
      return value;
   }

   private void setBestValue(TransactionBuilder txBuilder, ArtifactReadable source, ArtifactReadable dest, IAttributeType attributeType) throws Exception {
      String value = dest.getSoleAttributeValue(attributeType, "");
      String sourceValue = source.getSoleAttributeValue(attributeType, "Unspecified");
      if (sourceValue.equals("Unspecified")) {
         if (value.isEmpty() && attributeType.equals(CoreAttributeTypes.DataRightsClassification)) {
            txBuilder.setSoleAttributeValue(dest, attributeType, "Unspecified");
         }
      } else {
         if (value.isEmpty()) {
            txBuilder.setSoleAttributeValue(dest, attributeType, sourceValue);
         } else {
            if (!value.equals(sourceValue)) {
               System.out.println(dest.getName() + " conflict with " + attributeType);
            }
         }
      }
   }

   private String getAuthorFromSource(ArtifactReadable source) {
      return getAuthor(queryFactory.transactionQuery().andTxId(source.getLastModifiedTransaction()).getResults().getExactlyOne());
   }

   private String getAuthor(TransactionReadable tx) {
      ArtifactReadable author =
         queryFactory.fromBranch(CoreBranches.COMMON).andUuid(tx.getAuthorId()).getResults().getExactlyOne();
      return author.getName();
   }

   private ArtifactReadable getBestSource(ArtifactReadable source1, ArtifactReadable source2) throws Exception {
      if (source1 == null && source2 == null) {
         throw new Exception("how did this happen?");
      }
      if (source1 == null) {
         return source2;
      }
      if (source2 == null) {
         return source1;
      }
      if (source1.getLastModifiedTransaction() == 1153421) {
         if (source2.getLastModifiedTransaction() == 1257436) {
            System.out.println("no valid source");
         } else {
            return source2;
         }
      }
      return source1;
   }
}