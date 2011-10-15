/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.HashMap;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.orcs.OrcsConstants;
import org.eclipse.osee.orcs.db.internal.util.DatabaseTxCallable;

/**
 * @author Ryan D. Brooks
 */
public class MoveBranchCallable extends DatabaseTxCallable {

   private static final String INSERT_ADDRESSING =
      "insert into %s (transaction_id, gamma_id, tx_current, mod_type, branch_id) select transaction_id, gamma_id, tx_current, mod_type, branch_id from %s where branch_id = ?";

   public static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";
   private final boolean archive;
   private final Branch branch;

   private final EventService eventService;

   public MoveBranchCallable(IOseeDatabaseService databaseService, EventService eventService, boolean archive, Branch branch) {
      super(databaseService, "Branch Move");
      this.eventService = eventService;
      this.archive = archive;
      this.branch = branch;
   }

   private EventService getEventService() {
      return eventService;
   }

   @Override
   protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

      String sql = String.format(INSERT_ADDRESSING, destinationTableName, sourceTableName);
      getDatabaseService().runPreparedUpdate(connection, sql, branch.getId());

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      getDatabaseService().runPreparedUpdate(connection, sql, branch.getId());

      // TODO Populated Event Data
      getEventService().postEvent(OrcsConstants.BRANCH_MOVE_EVENT, new HashMap<String, Object>());
   }
}