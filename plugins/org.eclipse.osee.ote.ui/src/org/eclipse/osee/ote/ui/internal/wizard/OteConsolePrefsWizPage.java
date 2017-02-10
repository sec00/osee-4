/*
 * Created on Feb 10, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.internal.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ote.ui.internal.prefs.OteConsolePreferences;
import org.eclipse.osee.ote.ui.internal.prefs.OteConsolePrefsUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael P. Masterson
 */
public class OteConsolePrefsWizPage extends WizardPage {

   private Text bufferText;
   private Label errorLabel;
   private Button noLimitCheckbox;

   /**
    * @param pageName
    */
   public OteConsolePrefsWizPage() {
      super("OTE Console Preferences Wizard");
      setTitle("OTE Console Options");
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      GridLayoutFactory.fillDefaults().numColumns(1).applyTo(parent);
      
      addBufferLimit(parent);
      addErrorLabel(parent);
      determinePageComplete();
   }

   private void addErrorLabel(Composite parent) {
      this.errorLabel = new Label(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(errorLabel);
      errorLabel.setVisible(false);
   }

   /**
    * 
    */
   private void determinePageComplete() {
      boolean allIsWell = false;
      String bufferLimitStr = bufferText.getText();
      if(bufferLimitStr != null && bufferLimitStr.length() > 0 ) {
         try {
            Integer.parseInt(bufferLimitStr);
            allIsWell = true;
         } catch (NumberFormatException ex) {
            errorLabel.setText("Buffer limit must be a number between 0 and " + Integer.MAX_VALUE);
            allIsWell = false;
         }

      }

      errorLabel.setVisible(!allIsWell);
      setPageComplete(allIsWell);
   }

   /**
    * @param parent
    */
   private void addBufferLimit(Composite parent) {
      Group comp = new Group(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(false, false).applyTo(comp);
      GridLayoutFactory.fillDefaults().numColumns(2).applyTo(comp);
      comp.setText("Select Console Buffer Limit (Characters)");

      bufferText = new Text(comp, SWT.BORDER);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(bufferText);
      
      String defaultText = OteConsolePrefsUtil.getString(OteConsolePreferences.BUFFER_LIMIT);
      
      noLimitCheckbox = new Button(comp, SWT.CHECK);
      noLimitCheckbox.setText("No Limit");
      boolean defaultNoLimit = OteConsolePrefsUtil.getBoolean(OteConsolePreferences.NO_BUFFER_LIMIT);
      noLimitCheckbox.setSelection(defaultNoLimit);
      
      if(!defaultNoLimit) {
         bufferText.setText(defaultText);
      }
      
      bufferText.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            determinePageComplete(); 
         }
      });
      
      bufferText.addFocusListener(new FocusListener() {
         
         @Override
         public void focusLost(FocusEvent e) {
            savePreferences();
         }
         
         @Override
         public void focusGained(FocusEvent e) {
         }
      });
   }

   /**
    * 
    */
   protected void savePreferences() {
      if(isPageComplete()) {
         OteConsolePrefsUtil.setInt(OteConsolePreferences.BUFFER_LIMIT, Integer.parseInt(bufferText.getText()));
      }
   }

}
