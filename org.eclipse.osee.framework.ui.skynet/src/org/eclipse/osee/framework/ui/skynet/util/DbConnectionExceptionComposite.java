/*
 * Created on Jan 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.osee.framework.ui.plugin.util.ALayout;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class DbConnectionExceptionComposite extends Composite {

   /**
    * @param parent
    * @param style
    */
   public DbConnectionExceptionComposite(Composite parent, Exception ex) {
      super(parent, SWT.NONE);
      setLayout(ALayout.getZeroMarginLayout(1, true));
      setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true));
      Text text = new Text(this, SWT.WRAP);
      text.setText(ex.getLocalizedMessage());
      text.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true));
   }

   /**
    * Tests the DB Connection and returns true if ok. If exceptions and parent != null, the
    * DbConnectionExceptionComposite will be displayed in parent giving exception information.
    * 
    * @param parent
    * @return
    */
   public static boolean dbConnectionIsOk(Composite parent) {
      try {
         ConnectionHandler.getConnection();
      } catch (Exception ex) {
         if (parent != null) new DbConnectionExceptionComposite(parent, ex);
         return false;
      }
      return true;
   }
}
