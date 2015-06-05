/*
 * Created on Jun 5, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.rest.internal.config;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class UICountWriter {

   private final OrcsApi orcsApi;

   public UICountWriter(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void write(long branchUuid, Set<Long> newArts, Set<Long> modifiedArts, Set<Long> deletedArts, OutputStream outputStream) {
      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         String[] headers = getHeaders();
         int columns = headers.length;
         sheetWriter.startSheet("STRS Report", headers.length);
         sheetWriter.writeRow((Object[]) headers);

         List<IArtifactType> types = getTypes();

         if (!newArts.isEmpty()) {
            ResultSet<ArtifactReadable> newArtifacts =
               orcsApi.getQueryFactory().fromBranch(branchUuid).andTypeEquals(types).andUuids(newArts).getResults();
            for (ArtifactReadable art : newArtifacts) {
               String[] row = new String[columns];
               int index = 0;

               row[index++] = art.getName();
               row[index++] = "NEW";
               row[index++] = art.getAttributeValues(CoreAttributeTypes.Partition).toString();
               row[index++] = art.getArtifactType().toString();
               sheetWriter.writeRow((Object[]) row);
            }
         }

         if (!modifiedArts.isEmpty()) {
            ResultSet<ArtifactReadable> modifiedArtifacts =
               orcsApi.getQueryFactory().fromBranch(branchUuid).andTypeEquals(types).andUuids(modifiedArts).getResults();
            for (ArtifactReadable art : modifiedArtifacts) {
               String[] row = new String[columns];
               int index = 0;

               row[index++] = art.getName();
               row[index++] = "MODIFIED";
               row[index++] = art.getAttributeValues(CoreAttributeTypes.Partition).toString();
               row[index++] = art.getArtifactType().toString();
               sheetWriter.writeRow((Object[]) row);
            }
         }

         if (!deletedArts.isEmpty()) {
            ResultSet<ArtifactReadable> deletedArtifacts =
               orcsApi.getQueryFactory().fromBranch(branchUuid).andTypeEquals(types).andUuids(deletedArts).getResults();
            for (ArtifactReadable art : deletedArtifacts) {
               String[] row = new String[columns];
               int index = 0;

               row[index++] = art.getName();
               row[index++] = "DELETED";
               row[index++] = art.getAttributeValues(CoreAttributeTypes.Partition).toString();
               row[index++] = art.getArtifactType().toString();
               sheetWriter.writeRow((Object[]) row);
            }
         }

         sheetWriter.endSheet();
         sheetWriter.endWorkbook();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private List<IArtifactType> getTypes() {
      List<IArtifactType> types = new ArrayList<IArtifactType>();

      types.add(CoreArtifactTypes.SoftwareRequirement);
      types.add(CoreArtifactTypes.SoftwareRequirementDataDefinition);
      types.add(CoreArtifactTypes.SoftwareRequirementDrawing);
      types.add(CoreArtifactTypes.SoftwareRequirementFunction);
      types.add(CoreArtifactTypes.SoftwareRequirementPlainText);
      types.add(CoreArtifactTypes.SoftwareRequirementProcedure);
      types.add(CoreArtifactTypes.Design);
      types.add(CoreArtifactTypes.SoftwareDesign);
      types.add(CoreArtifactTypes.SystemDesign);
      types.add(CoreArtifactTypes.SubsystemDesign);

      return types;
   }

   private static String[] getHeaders() {
      String[] toReturn = {"Name", "Mod Type", "Partition", "Type"};
      return toReturn;
   }

}
