package org.eclipse.osee.ats.rest.internal.workitem;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.AtsTeamWfEndpointApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.change.CompareResults;

/**
 * Donald G. Dunne
 */
@Path("teamwf")
public class AtsTeamWfEndpointImpl implements AtsTeamWfEndpointApi {

   private final AtsApi atsApi;

   public AtsTeamWfEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   @GET
   @Path("{id}/changedata")
   @Produces({MediaType.APPLICATION_JSON})
   public CompareResults getChangeData(@PathParam("id") String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
      if (!workItem.isTeamWorkflow()) {
         throw new UnsupportedOperationException();
      }
      IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
      TransactionToken trans = atsApi.getBranchService().getEarliestTransactionId(teamWf);
      if (trans.isValid()) {
         return atsApi.getBranchService().getChangeData(trans);
      }
      BranchId branch = atsApi.getBranchService().getWorkingBranch(teamWf);
      if (branch.isValid()) {
         return atsApi.getBranchService().getChangeData(branch);
      }
      return new CompareResults();
   }

}
