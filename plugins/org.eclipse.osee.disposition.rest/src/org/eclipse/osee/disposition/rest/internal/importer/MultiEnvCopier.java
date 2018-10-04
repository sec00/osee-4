/*
 * Created on Oct 1, 2018
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;

public class MultiEnvCopier {

   public List<DispoItem> copy(Map<DispoItemData, Set<DispoItemData>> itemToMultiEnvTwins, OperationReport rerpot) {
      List<DispoItem> modifiedItems = new ArrayList<>();

      Set<Entry<DispoItemData, Set<DispoItemData>>> entrySet = itemToMultiEnvTwins.entrySet();
      for (DispoItemData origItem : itemToMultiEnvTwins.keySet()) {
         Set<DispoItemData> twinItems = itemToMultiEnvTwins.get(origItem);

         copyCoveredLinesToTwins(origItem, twinItems);
      }

      return modifiedItems;
   }

   private void copyCoveredLinesToTwins(DispoItemData origItem, Set<DispoItemData> twinItems) {
      DispoDataFactory factory = new DispoDataFactory();
      DispoConnector connector = new DispoConnector();
      Set<String> texts = new HashSet<>();

      for (DispoAnnotationData annotation : origItem.getAnnotationsList()) {
         if (annotation.getIsDefault()) {
            texts.add(annotation.getResolution());
         }
      }

      for (DispoItemData twinItem : twinItems) {
         Map<String, Discrepancy> textToDiscrepancy = buildMap(twinItem);
         for (String text : texts) {
            Discrepancy discrepancy = textToDiscrepancy.get(text);
            DispoAnnotationData annotation = new DispoAnnotationData();
            factory.initAnnotation(annotation);
            annotation.setResolutionType("NEED TO UPDATE");
            annotation.setResolution("MULTI ENV");
            annotation.setLocationRefs(discrepancy.getLocation());
            connector.connectAnnotation(annotation, twinItem.getDiscrepanciesList());
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
