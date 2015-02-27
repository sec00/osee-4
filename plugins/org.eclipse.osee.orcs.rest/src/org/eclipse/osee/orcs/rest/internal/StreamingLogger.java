/*
 * Created on Nov 22, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.rest.internal;

import org.eclipse.osee.logger.Log;

public interface StreamingLogger {

   void stream(Log logger) throws Exception;
}
