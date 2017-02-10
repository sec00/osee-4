/*
 * Created on Feb 10, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.internal.prefs;

import org.eclipse.osee.ote.ui.internal.TestCoreGuiPlugin;

/**
 * @author Michael P. Masterson
 */
public class OteConsolePrefsUtil {

   public static int getInt(OteConsolePreferences bufferLimit) {
      return TestCoreGuiPlugin.getDefault().getPreferenceStore().getInt(bufferLimit.getPropKey());
   }

   public static String getString(OteConsolePreferences bufferLimit) {
      return TestCoreGuiPlugin.getDefault().getPreferenceStore().getString(bufferLimit.getPropKey());
   }
   
   public static boolean getBoolean(OteConsolePreferences bufferLimit) {
      return TestCoreGuiPlugin.getDefault().getPreferenceStore().getBoolean(bufferLimit.getPropKey());
   }
   
   public static void setInt(OteConsolePreferences preference, int value) {
      TestCoreGuiPlugin.getDefault().getPreferenceStore().setValue(preference.getPropKey(), value);
   }
   
   public static void setString(OteConsolePreferences preference, String value) {
      TestCoreGuiPlugin.getDefault().getPreferenceStore().setValue(preference.getPropKey(), value);
   }
   
   public static void setBoolean(OteConsolePreferences preference, boolean value) {
      TestCoreGuiPlugin.getDefault().getPreferenceStore().setValue(preference.getPropKey(), value);
   }

}
