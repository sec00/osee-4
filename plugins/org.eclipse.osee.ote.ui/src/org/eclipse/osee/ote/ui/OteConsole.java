/*
 * Created on Feb 10, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.internal.console.IOConsolePage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;

/**
 * @author Michael P. Masterson
 */
public class OteConsole extends IOConsole {

   private IActionBars bars;
   private Action optionsButton;

   /**
    * @param name
    * @param consoleType
    * @param imageDescriptor
    */
   public OteConsole(String name, ImageDescriptor imageDescriptor) {
      super(name, imageDescriptor);
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.ui.console.IOConsole#createPage(org.eclipse.ui.console.IConsoleView)
    */
   @Override
   public IPageBookViewPage createPage(IConsoleView view) {
      
      @SuppressWarnings("restriction")
      IPageBookViewPage page = new IOConsolePage(this, view) {
         @Override
         public void init(IPageSite pageSite) throws PartInitException {
            super.init(pageSite);
            bars = getSite().getActionBars();
            createOptionsButton();

            bars.getMenuManager().add(optionsButton);
            bars.getToolBarManager().add(optionsButton);

            bars.updateActionBars();
         }
      };
      
      return page;
   }

   /**
    * 
    */
   private void createOptionsButton() {
      this.optionsButton = new Action("OTE_MOREs") {
         /* (non-Javadoc)
          * @see org.eclipse.jface.action.Action#run()
          */
         @Override
         public void run() {
            System.out.println("YEAAAHHHH BUDDY");
            super.run();
         }
      };
      
      optionsButton.setEnabled(true);
   }


}
