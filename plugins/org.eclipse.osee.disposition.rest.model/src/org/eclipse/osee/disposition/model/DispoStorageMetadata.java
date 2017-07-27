/*
 * Created on Jul 26, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.model;

import java.util.HashSet;
import java.util.Set;

public class DispoStorageMetadata {

   private final Set<String> idsOfUpdatedItems = new HashSet<>();

   public DispoStorageMetadata() {

   }

   public Set<String> getIdsOfUpdatedItems() {
      return idsOfUpdatedItems;
   }

   public void addIdOfUpdatedItem(String id) {
      idsOfUpdatedItems.add(id);
   }
}
