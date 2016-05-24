package org.eclipse.osee.ote.message.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A POJO that is used to save an array of {@link ElementSave} and generic config data via a Map to a JSON file.  This 
 * is used for saving data that can be opened in various data analysis GUI's in OTE.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class ElementSaveFile {

   private List<ElementSave> elements;
   private Map<String, String> config;
   
   public ElementSaveFile(){
      elements = new ArrayList<>();
      config = new HashMap<String, String>();
   }
   
   public List<ElementSave> getElements(){
      return elements;
   }
   
   public Map<String, String> getConfig(){
      return config;
   }
   
}
