/*
 * Created on Feb 10, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ote.ui.internal.wizard.OteConsolePrefsWizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;

public class OteConsolePageParticipant implements IConsolePageParticipant {

   private IConsole console;
   private IPageBookViewPage page;
   private IActionBars bars;
   private Action optionsButton;

   @Override
   public <T> T getAdapter(Class<T> adapter) {
      return null;
   }

   @Override
   public void init(IPageBookViewPage page, IConsole console) {
      this.console = console;
      this.page = page;
      IPageSite site = page.getSite();
      this.bars = site.getActionBars();
      
      createOptionsButton();
      
      bars.getMenuManager().add(optionsButton);
      
      bars.getToolBarManager().appendToGroup(IConsoleConstants.LAUNCH_GROUP, optionsButton);

      bars.updateActionBars();
   }

   /**
    * 
    */
   private void createOptionsButton() {
      this.optionsButton = new Action("OTE_AGAIN") {
         /* (non-Javadoc)
          * @see org.eclipse.jface.action.Action#run()
          */
         @Override
         public void run() {
            WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), new OteConsolePrefsWizard());
            System.out.println("YEAAAHHHH BUDDY");
            super.run();
         }
      };
   }

   @Override
   public void dispose() {
      this.page = null;
      this.bars = null;
      this.optionsButton = null;
   }

   @Override
   public void activated() {
      update();
   }

   private void update() {
      if(page == null) {
         return;
      }
      
      optionsButton.setEnabled(true);
      
      bars.updateActionBars();
   }

   @Override
   public void deactivated() {
      update();
   }

}
