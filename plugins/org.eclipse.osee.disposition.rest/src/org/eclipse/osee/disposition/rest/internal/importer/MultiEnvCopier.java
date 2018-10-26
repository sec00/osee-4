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
      for (DispoItemData twinItem : twinItems) {
         Collection<Discrepancy> discrepancies = twinItem.getDiscrepanciesList().values();
         for (Discrepancy discrepancy : discrepancies) {
            addAnnotation(twinItem, discrepancy);
            discrepancies.remove(discrepancy);
         }
      }

   }

   private void addAnnotation(DispoItemData item, Discrepancy discrepancy) {
      DispoDataFactory factory = new DispoDataFactory();

      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      factory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = factory.getNewId();
      newAnnotation.setId(idOfNewAnnotation);

      newAnnotation.setIsDefault(true);
      newAnnotation.setLocationRefs(discrepancy.getLocation());
      newAnnotation.setResolutionType("Deactivated_Compile_Time");
      newAnnotation.setResolution("MULTI ENV");
      newAnnotation.setIsResolutionValid(true);
      newAnnotation.setCustomerNotes(discrepancy.getText());

      List<DispoAnnotationData> annotationsList = item.getAnnotationsList();
      int newIndex = annotationsList.size();
      newAnnotation.setIndex(newIndex);
      annotationsList.add(newIndex, newAnnotation);
   }
}
