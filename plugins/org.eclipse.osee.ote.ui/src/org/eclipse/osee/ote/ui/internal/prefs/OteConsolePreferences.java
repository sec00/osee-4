/*
 * Created on Feb 10, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.internal.prefs;

/**
 * @author Michael P. Masterson
 */
public enum OteConsolePreferences {
   BUFFER_LIMIT("org.eclipse.osee.ote.ui.bufferLimit", 1000000), 
   NO_BUFFER_LIMIT("org.eclipse.osee.ote.ui.noLimit", false);

   
   private String propKey;
   private Object defaultValue;

   private OteConsolePreferences(String propKey, Object defaultValue) {
      this.propKey = propKey;
      this.defaultValue = defaultValue;
   }
   
   /**
    * @return the defaultValue
    */
   public Object getDefaultValue() {
      return defaultValue;
   }
   
   /**
    * @return the propKey
    */
   public String getPropKey() {
      return propKey;
   }
   
}
