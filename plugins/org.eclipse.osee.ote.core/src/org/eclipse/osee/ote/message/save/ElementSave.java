package org.eclipse.osee.ote.message.save;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.osee.ote.message.ElementPath;

/**
 * A POJO that is used by {@link ElementSaveFile} to save an {@link ElementPath} along with 
 * {@link ElementDataSave} and generic configuration data.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class ElementSave {
   private String path;
   private ElementDataSave data;
   private Map<String, String> config;
   
   public ElementSave(){
      data = new ElementDataSave();
      config = new HashMap<String, String>();
   }
   
   public ElementDataSave getData(){
      return data;
   }
   
   public String getPath(){
      return path;
   }
   
   public void setPath(String path){
      this.path = path;
   }
   
   public Map<String, String> getConfig(){
      return config;
   }
}
