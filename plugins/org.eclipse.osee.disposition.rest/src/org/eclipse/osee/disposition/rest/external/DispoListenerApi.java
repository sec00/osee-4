/*
 * Created on Jul 26, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.rest.external;

import java.util.Collection;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;

public interface DispoListenerApi {

   public void onUpdateItemStats(Collection<String> ids, Collection<DispoItem> items, DispoSet set);
}
