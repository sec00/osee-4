/*
 * Created on Jun 3, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.rest.internal.config;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.rest.model.CompareResults;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Angel Avila
 */
@Path("uicount")
public class ReportResource {

   private final OrcsApi orcsApi;
   private final TransactionEndpoint transactionService;
   private final IAtsServer atsServer;

   public ReportResource(OrcsApi orcsApi, TransactionEndpoint transactionService, IAtsServer atsServer) {
      this.orcsApi = orcsApi;
      this.transactionService = transactionService;
      this.atsServer = atsServer;
   }

   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response getUiCount() throws OseeCoreException {
      long branchUuid = 14866;
      List<ChangeItem> changes = getChanges(branchUuid);
      Set<Long> newArts = new HashSet<Long>();
      Set<Long> modArts = new HashSet<Long>();
      Set<Long> deletedArts = new HashSet<Long>();

      Set<Long> types = new HashSet<Long>();
      for (ChangeItem c : changes) {
         types.add(c.getItemTypeId());
      }

      Map<Integer, Pair<ChangeItem, Set<ChangeItem>>> artToChanges =
         new HashMap<Integer, Pair<ChangeItem, Set<ChangeItem>>>();

      buildArtIdToChangeMap(changes, artToChanges);
      buildLists(artToChanges, newArts, modArts, deletedArts);

      final UICountWriter writer = new UICountWriter(orcsApi);
      final String fileName = String.format("STRS_Report_%s", System.currentTimeMillis());

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws WebApplicationException, IOException {
            writer.write(branchUuid, newArts, modArts, deletedArts, outputStream);
            outputStream.flush();
         }
      };
      String contentDisposition =
         String.format("attachment; filename=\"%s.xml\"; creation-date=\"%s\"", fileName, new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type("application/xml").build();
   }

   private void buildArtIdToChangeMap(List<ChangeItem> changes, Map<Integer, Pair<ChangeItem, Set<ChangeItem>>> artToChanges) {
      for (ChangeItem change : changes) {
         int artId = change.getArtId();
         ChangeType changeType = change.getChangeType();
         if (changeType.isArtifactChange()) {
            if (!artToChanges.containsKey(artId)) {
               artToChanges.put(artId, new Pair<>(change, new HashSet<ChangeItem>()));
            } else {
               // This entry was added by an attribute change for this art so the changeType hasn't been set
               Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artId);
               pair.setFirst(change);
               //               artToChanges.put(artId, pair);// maybe don't need this line
            }
         } else if (changeType.isAttributeChange()) {
            if (!artToChanges.containsKey(artId)) {
               Set<ChangeItem> changeSet = new HashSet<ChangeItem>();
               changeSet.add(change);
               artToChanges.put(artId, new Pair<>(null, changeSet));
            } else {
               Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artId);
               Set<ChangeItem> changeSet = pair.getSecond();
               changeSet.add(change);
               //             artToChanges.put(artId, pair);// maybe don't need this line
            }
         }

      }
   }

   private void buildLists(Map<Integer, Pair<ChangeItem, Set<ChangeItem>>> artToChanges, Set<Long> newArts, Set<Long> modArts, Set<Long> deletedArts) {
      AttributeTypes attributeTypes = orcsApi.getOrcsTypes().getAttributeTypes();
      ArtifactTypes artifactTypes = orcsApi.getOrcsTypes().getArtifactTypes();

      for (Integer artId : artToChanges.keySet()) {
         Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artId);
         ChangeItem artChange = pair.getFirst();
         ModificationType modType = artChange.getNetChange().getModType();

         if (modType.equals(ModificationType.NEW)) {
            newArts.add((long) artId);
         } else if (modType.equals(ModificationType.DELETED)) {
            deletedArts.add((long) artId);
         } else if (modType.equals(ModificationType.MODIFIED) && weCare(artChange, pair.getSecond(), attributeTypes,
            artifactTypes)) {
            modArts.add((long) artId);
         }
      }

   }

   private boolean weCare(ChangeItem artChange, Set<ChangeItem> attrChanges, AttributeTypes attributeTypes, ArtifactTypes artTypes) {
      boolean toReturn = false;

      // Was a synthetic artifact change added by AddArtifactChangeDataCallable
      IArtifactType artType = artTypes.getByUuid(artChange.getItemTypeId());
      //      if ((artType != null && artType.matches(AbstractSoftwareRequirement)) || artChange.getItemTypeId() == -1) {
      for (ChangeItem change : attrChanges) {
         IAttributeType attrType = attributeTypes.getByUuid(change.getItemTypeId());
         if (attrType.matches(WordTemplateContent)) {
            toReturn = true;
            break;
         }
      }
      //      } else {
      //         System.out.println(artType);
      //      }

      return toReturn;
   }

   private List<ChangeItem> getChanges(long branchUuid) {
      TransactionQuery transactionQuery2 = orcsApi.getQueryFactory().transactionQuery();
      TransactionQuery transactionQuery3 = orcsApi.getQueryFactory().transactionQuery();
      IOseeBranch branchReadable =
         orcsApi.getQueryFactory().branchQuery().andUuids(branchUuid).getResults().getExactlyOne();

      IOseeBranch parentBranch = atsServer.getBranchService().getParentBranch(branchReadable);

      TransactionReadable startTx = transactionQuery2.andIsHead(branchReadable).getResults().getExactlyOne();
      Integer start = startTx.getLocalId();

      TransactionReadable endTx = transactionQuery3.andIsHead(parentBranch).getResults().getExactlyOne();
      Integer end = endTx.getLocalId();

      CompareResults results = transactionService.compareTxs(start, end);

      return results.getChanges();
   }

}
