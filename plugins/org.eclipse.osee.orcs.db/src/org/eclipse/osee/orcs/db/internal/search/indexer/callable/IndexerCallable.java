/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.indexer.callable;

import java.util.concurrent.Callable;
import org.eclipse.osee.activity.api.Activity;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.orcs.db.internal.search.indexer.TagCollectorAndStorage;
import org.eclipse.osee.orcs.db.internal.search.indexer.data.AttributeLoader;
import org.eclipse.osee.orcs.db.internal.search.indexer.data.IndexerDataSourceImpl;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;

/**
 * @author Ryan D. Brooks
 */
public class IndexerCallable implements Callable<Void> {
   private final TaggingEngine taggingEngine;
   private final AttributeLoader loader;
   private final IResourceManager resourceManager;
   private final TagCollectorAndStorage collector;
   private final ActivityLog activityLog;

   public IndexerCallable(TaggingEngine taggingEngine, AttributeLoader loader, TagCollectorAndStorage collector, IResourceManager resourceManager, ActivityLog activityLog) {
      this.taggingEngine = taggingEngine;
      this.loader = loader;
      this.resourceManager = resourceManager;
      this.collector = collector;
      this.activityLog = activityLog;
   }

   @Override
   public Void call() {
      try {
         IndexerDataSourceImpl source = new IndexerDataSourceImpl(resourceManager);
         while (loader.loadInto(source)) {
            long gammaId = source.getGammaId();

            Tagger tagger = taggingEngine.getTagger(source.getTypeUuid());
            if (tagger == null) {
               // message format  "No tagger found for type [%d] from gammaId [%d]"
               activityLog.createEntry(Activity.SRS_TRACE, ActivityLog.ABNORMALLY_ENDED_STATUS, source.getTypeUuid(),
                  gammaId);
            } else {
               try {
                  tagger.tagIt(gammaId, source, collector);
                  collector.gammaComplete(gammaId);
               } catch (Exception ex) {
                  ex.printStackTrace();
                  //                  activityLog.createThrowableEntry(Activity.SRS_TRACE, ex);
               }
            }
         }
         collector.store();
      } catch (Exception ex) {
         activityLog.createThrowableEntry(Activity.SRS_TRACE, ex);
      }
      return null;
   }
}