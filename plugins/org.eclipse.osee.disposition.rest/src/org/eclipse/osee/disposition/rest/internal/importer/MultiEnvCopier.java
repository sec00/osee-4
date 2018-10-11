/*
 * Created on Oct 1, 2018
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;

public class MultiEnvCopier {

   public void copy(Map<DispoItemData, Set<DispoItemData>> itemToMultiEnvTwins, OperationReport rerpot) {
      for (DispoItemData origItem : itemToMultiEnvTwins.keySet()) {
         if (origItem.getName().contains("UPDATE_HEALTH_STATUS_FROM_PARTITION_HEALTH_REPORT")) {
            System.out.println();
         }
         Set<DispoItemData> twinItems = itemToMultiEnvTwins.get(origItem);

         copyCoveredLinesToTwins(origItem, twinItems);
      }
   }

   private void copyCoveredLinesToTwins(DispoItemData origItem, Set<DispoItemData> twinItems) {
      DispoDataFactory factory = new DispoDataFactory();
      DispoConnector connector = new DispoConnector();

      for (DispoItemData twinItem : twinItems) {
         Collection<Discrepancy> discrepancies = twinItem.getDiscrepanciesList().values();
         for (Discrepancy discrepancy : discrepancies) {
            DispoAnnotationData annotation = new DispoAnnotationData();
            factory.initAnnotation(annotation);
            annotation.setResolutionType("Deactivated_Compile_Time");
            annotation.setResolution("MULTI ENV");
            annotation.setLocationRefs(discrepancy.getLocation());
            connector.connectAnnotation(annotation, twinItem.getDiscrepanciesList());

            List<DispoAnnotationData> annotationsList = twinItem.getAnnotationsList();
            int newIndex = annotationsList.size();
            annotation.setIndex(newIndex);
            annotationsList.add(newIndex, annotation);
         }
      }

   }

   private Map<String, Discrepancy> buildMap(DispoItemData item) {
      Map<String, Discrepancy> toReturn = new HashMap<>();
      for (Discrepancy discrepancy : item.getDiscrepanciesList().values()) {
         toReturn.put(discrepancy.getText(), discrepancy);
      }
      return toReturn;
   }

}
