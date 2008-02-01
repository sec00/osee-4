/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.admin.autoRun;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.admin.OseeClientsTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class AutoRunTab {

   private User whoAmI;
   private Composite mainComposite;

   public AutoRunTab(TabFolder tabFolder) {
      super();
      this.whoAmI = SkynetAuthentication.getInstance().getAuthenticatedUser();
      this.mainComposite = null;
      createControl(tabFolder);
      mainComposite.setEnabled(isUserAllowedToOperate(whoAmI));
   }

   private void createControl(TabFolder tabFolder) {
      mainComposite = new Composite(tabFolder, SWT.NONE);
      mainComposite.setLayout(new GridLayout());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      TabItem tab = new TabItem(tabFolder, SWT.NONE);
      tab.setControl(mainComposite);
      tab.setText("Auto Run");

      if (true != isUserAllowedToOperate(whoAmI)) {
         OseeClientsTab.createDefaultWarning(mainComposite);
      } else {
         XAutoRunViewer xTaskViewer = new XAutoRunViewer();
         xTaskViewer.createWidgets(mainComposite, 1);
      }
   }

   private boolean isUserAllowedToOperate(User user) {
      return OseeProperties.getInstance().isDeveloper();
   }

}
