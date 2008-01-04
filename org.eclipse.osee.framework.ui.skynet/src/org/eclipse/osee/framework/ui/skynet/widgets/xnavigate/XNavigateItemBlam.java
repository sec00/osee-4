/*
 * Created on Jan 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.sql.SQLException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemBlam extends XNavigateItem {

   private final BlamOperation blamOperation;

   /**
    * @param parent
    * @param name
    */
   public XNavigateItemBlam(XNavigateItem parent, BlamOperation blamOperation) {
      super(parent, blamOperation.getClass().getName().replaceAll("^.*\\.", ""));
      this.blamOperation = blamOperation;
   }

   public void run() throws SQLException {
      if (blamOperation != null) {
         Displays.ensureInDisplayThread(new Runnable() {
            /*
             * (non-Javadoc)
             * 
             * @see java.lang.Runnable#run()
             */
            public void run() {
               AWorkbench.popup("ERROR", "Not Implemented Yet");
            }
         });
      }
   }
}
