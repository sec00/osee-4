/*
 * Created on Aug 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;


public class ViewUtility {
 
   public static void createWebBrowserHelpButton(final String urlToHelp, IViewPart view){
      if (view.getViewSite() != null) {
         Action infoAction = new Action("Info") {
            @Override
            public void run() {
               Program.launch(urlToHelp);
            }
         };
         infoAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_LCL_LINKTO_HELP));
         infoAction.setToolTipText("Opens Help in External Browser");
         IToolBarManager toolbarManager = view.getViewSite().getActionBars().getToolBarManager();
         toolbarManager.add(infoAction);
      }
   }
}
