/*
 * Created on Oct 14, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal.util;

import java.util.concurrent.Callable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.DatabaseTransactions;
import org.eclipse.osee.framework.database.core.IDbTransactionWork;
import org.eclipse.osee.framework.database.core.OseeConnection;

public abstract class DatabaseTxCallable implements Callable<IStatus> {

   private final IOseeDatabaseService dbService;
   private final String name;

   public DatabaseTxCallable(IOseeDatabaseService dbService, String name) {
      this.dbService = dbService;
      this.name = name;
   }

   protected IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   @Override
   public final IStatus call() throws Exception {
      OseeConnection connection = getDatabaseService().getConnection();
      try {
         InternalTxWork work = new InternalTxWork();
         DatabaseTransactions.execute(connection, work);
      } finally {
         connection.close();
      }
      return Status.OK_STATUS;
   }

   protected abstract void handleTxWork(OseeConnection connection) throws OseeCoreException;

   protected void handleTxException(Exception ex) {
      // Do nothing
   }

   @SuppressWarnings("unused")
   protected void handleTxFinally() throws OseeCoreException {
      // Do nothing
   }

   private final class InternalTxWork implements IDbTransactionWork {

      @Override
      public String getName() {
         return name;
      }

      @Override
      public void handleTxWork(OseeConnection connection) throws OseeCoreException {
         DatabaseTxCallable.this.handleTxWork(connection);
      }

      @Override
      public void handleTxException(Exception ex) {
         DatabaseTxCallable.this.handleTxException(ex);
      }

      @Override
      public void handleTxFinally() throws OseeCoreException {
         DatabaseTxCallable.this.handleTxFinally();
      }
   };
}
