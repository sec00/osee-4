/*
 * Created on Jul 26, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.rest.external;

import java.util.Collection;
import org.eclipse.osee.disposition.model.DispoItem;

public interface DispoListenerApi {

   public void onUpdateItemStats(Collection<String> ids, Collection<DispoItem> items);
}
