package org.eclipse.osee.ote.message.save;

import java.util.ArrayList;
import java.util.List;

/**
 * A POJO that is used by {@link ElementSave} to save element data.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class ElementDataSave {

   private List<Double> time;
   private List<Double> value;
   private List<String> info;
   
   public ElementDataSave(){
      time = new ArrayList<>();
      value = new ArrayList<>();
      info = new ArrayList<>();
   }
   
   public List<Double> getTime(){
      return time;
   }
   
   public List<Double> getValue(){
      return value;
   }
   
   public List<String> getInfo(){
      return info;
   }
   
   
}
