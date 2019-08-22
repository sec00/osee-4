/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.indexer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public class IndexerCollectorNotifier implements IndexerCollector {

   private final Set<IndexerCollector> listeners = new CopyOnWriteArraySet<>();

   private final Log logger;

   public IndexerCollectorNotifier(Log logger) {
      this.logger = logger;
   }

   private void handleError(IndexerCollector collector, Exception ex) {
      logger.error(ex, "Error notifying collector: [%s] ", collector.getClass().getName());
   }

   public void addCollector(IndexerCollector collector) {
      if (collector != null) {
         this.listeners.add(collector);
      }
   }

   public void removeCollector(IndexerCollector collector) {
      if (collector != null) {
         this.listeners.remove(collector);
      }
   }

   @Override
   public void onIndexTaskError(int indexerId, Throwable throwable) {
      for (IndexerCollector collector : listeners) {
         try {
            collector.onIndexTaskError(indexerId, throwable);
         } catch (Exception ex) {
            handleError(collector, ex);
         }
      }
   }

   @Override
   public void onIndexTaskTotalToProcess(int totalIndexTasks) {
      for (IndexerCollector collector : listeners) {
         try {
            collector.onIndexTaskTotalToProcess(totalIndexTasks);
         } catch (Exception ex) {
            handleError(collector, ex);
         }
      }
   }

   @Override
   public void onIndexTaskSubmit(int indexerId) {
      for (IndexerCollector collector : listeners) {
         try {
            collector.onIndexTaskSubmit(indexerId);
         } catch (Exception ex) {
            handleError(collector, ex);
         }
      }
   }

   @Override
   public void onIndexTaskComplete(int indexerId, long waitTime, long processingTime) {
      for (IndexerCollector collector : listeners) {
         try {
            collector.onIndexTaskComplete(indexerId, waitTime, processingTime);
         } catch (Exception ex) {
            handleError(collector, ex);
         }
      }
   }

   @Override
   public void onIndexItemAdded(int indexerId, long itemId, long codedTag) {
      for (IndexerCollector collector : listeners) {
         try {
            collector.onIndexItemAdded(indexerId, itemId, codedTag);
         } catch (Exception ex) {
            handleError(collector, ex);
         }
      }
   }

   @Override
   public void onIndexItemComplete(int indexerId, long itemId, int totalTags, long processingTime) {
      for (IndexerCollector collector : listeners) {
         try {
            collector.onIndexItemComplete(indexerId, itemId, totalTags, processingTime);
         } catch (Exception ex) {
            handleError(collector, ex);
         }
      }
   }

   @Override
   public void onIndexTotalTaskItems(long totalItems) {
      for (IndexerCollector collector : listeners) {
         try {
            collector.onIndexTotalTaskItems(totalItems);
         } catch (Exception ex) {
            handleError(collector, ex);
         }
      }
   }
}
