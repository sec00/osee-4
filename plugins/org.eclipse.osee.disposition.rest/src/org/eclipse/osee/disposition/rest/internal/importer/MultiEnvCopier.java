/*
 * Created on Oct 1, 2018
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.Collection;
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

   public void copy(Map<String, Set<DispoItemData>> itemToMultiEnvTwins, Set<String> foundItems, OperationReport rerpot) {
      for (String origItemName : foundItems) {
         Set<DispoItemData> twinItems = itemToMultiEnvTwins.get(origItemName);

         if (twinItems != null) {
            copyCoveredLinesToTwins(twinItems);
         }
      }
   }

   private void copyCoveredLinesToTwins(Set<DispoItemData> twinItems) {
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
}
