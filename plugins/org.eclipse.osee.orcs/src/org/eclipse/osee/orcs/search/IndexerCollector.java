/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.search;

/**
 * @author Roberto E. Escobar
 */
public interface IndexerCollector {

   void onIndexTaskError(int indexerId, Throwable throwable);

   void onIndexTaskTotalToProcess(int totalIndexTasks);

   void onIndexTaskSubmit(int indexerId);

   void onIndexTaskComplete(int indexerId, long waitTime, long processingTime);

   void onIndexItemAdded(int indexerId, long itemId, long codedTag);

   void onIndexItemComplete(int indexerId, long itemId, int totalTags, long processingTime);

   void onIndexTotalTaskItems(long totalItems);
}
