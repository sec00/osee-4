/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IResultsEditorProvider {

   public String getEditorName() throws OseeCoreException;

   public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException;

}
