/*
 * Created on Jul 26, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.rest.external;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.logger.Log;

public class DispoUpdateBroadcaster {

   private Log logger;
   private final Set<DispoListenerApi> listeners = new HashSet<>();

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start() {
      logger.trace("Starting DispoUpdateBroadcaster...");
   }

   public void stop() {
      logger.trace("Stopping DispoUpdateBroadcaster...");
   }

   public void addDispoListener(DispoListenerApi listener) {
      listeners.add(listener);
   }

   public void broadcastUpdateItems(Collection<String> ids, Collection<DispoItem> items) {
      for (DispoListenerApi listener : listeners) {
         listener.onUpdateItemStats(ids, items);
      }
   }
}
