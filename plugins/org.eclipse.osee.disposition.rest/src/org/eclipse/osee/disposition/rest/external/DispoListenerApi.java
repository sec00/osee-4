/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.external;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.UpdateSummaryData;

/**
 * @author Angel Avila
 */
public interface DispoListenerApi {

   public List<UpdateSummaryData> onUpdateItemStats(Collection<String> ids, Collection<DispoItem> items, DispoSet set);

   public void onDeleteDispoSet(DispoSet set);
}