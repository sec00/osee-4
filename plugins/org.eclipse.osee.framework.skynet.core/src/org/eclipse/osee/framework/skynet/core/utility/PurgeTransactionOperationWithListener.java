/*
 * Created on Jun 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.database.operation.PurgeTransactionOperation;
import org.eclipse.osee.framework.database.operation.PurgeTransactionOperation.PurgeTransactionListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.PurgeTransactionEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

public class PurgeTransactionOperationWithListener {
   public static IOperation getPurgeTransactionOperation(boolean force, final int... transactionIdNumbers) {
      final PurgeTransactionOperation op =
         new PurgeTransactionOperation(Activator.getInstance().getOseeDatabaseService(), force, transactionIdNumbers);

      PurgeTransactionListener listener = new PurgeTransactionListener() {

         @Override
         public void onPurgeTransactionSuccess(Collection<TransactionRecord> transactions) {
            TransactionEvent transactionEvent = PurgeTransactionEventUtil.createPurgeTransactionEvent(transactions);
            try {
               OseeEventManager.kickTransactionEvent(op, transactionEvent);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Error sending purge transaction events", ex);
            }
         }
      };
      op.addListener(listener);
      return op;
   }
}
