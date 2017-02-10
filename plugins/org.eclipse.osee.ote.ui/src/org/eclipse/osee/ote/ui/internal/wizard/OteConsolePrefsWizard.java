/*
 * Created on Feb 10, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.internal.wizard;

import org.eclipse.jface.wizard.Wizard;

/**
 * @author Michael P. Masterson
 */
public class OteConsolePrefsWizard extends Wizard {
   
   
   OteConsolePrefsWizPage mainPage;

   public OteConsolePrefsWizard() {
      mainPage = new OteConsolePrefsWizPage();
   }

   @Override
   public boolean performFinish() {
      return true;
   }
   
   @Override
   public void addPages() {
      addPage(mainPage);
   }

}
