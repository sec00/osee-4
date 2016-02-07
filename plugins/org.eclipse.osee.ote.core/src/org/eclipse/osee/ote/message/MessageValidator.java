package org.eclipse.osee.ote.message;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.message.elements.Element;

public class MessageValidator {

   public boolean validate(Message message, StringBuilder sb){
      boolean result = true;
      result = result & checkMessageName(message, sb);
      result = result & checkMessageSize(message, sb);
      result = result & checkElementFieldMatchesNAme(message, sb);
      return result;
   }

   @SuppressWarnings("rawtypes")
   private boolean checkElementFieldMatchesNAme(Message message, StringBuilder sb) {
      boolean result = true;
      List<Field> masterMessage = new ArrayList<Field>();
      Field[] fields = message.getClass().getFields();
      for(Field field:fields){
         Class clazz = field.getType();
         if(Element.class.isAssignableFrom(clazz)){
            masterMessage.add(field);
         }
      }
      for(Field f:masterMessage){
         try {
            
            Element el = (Element)f.get(message);
            if(!el.getName().equals(f.getName())){
               result = false;
               sb.append(String.format("%s failure - Element Field name [%s] does not match given name [%s]\n", message.getClass().getName(), f.getName(), el.getName()));
            }
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         }
      }
      return result;
   }

   private boolean checkMessageSize(Message message, StringBuilder sb) {
      int maxBytes = 0;
      for(Element el:message.getElements()){
         int lsb = el.getLsb();
         int bytes = (lsb+1)/8 + ((lsb+1)%8!=0?1:0);
         bytes += el.getByteOffset();
         if(bytes > maxBytes){
            maxBytes = bytes;
         }
      }
      if(maxBytes > message.getDefaultByteSize()){
         sb.append(String.format("%s failure - Element Size[%d] exceeds default byte size[%d]\n", message.getClass().getName(), maxBytes, message.getDefaultByteSize()));
         return false;
      }
      return true;
   }

   private boolean checkMessageName(Message message, StringBuilder sb) {
      boolean returnValue = true;
      String className = message.getClass().getSimpleName();
      if(!className.equals(message.getName())){
         sb.append(String.format("%s failure - Classname[%s] does not match given name [%s]\n", message.getClass().getName(), message.getClass().getSimpleName(), message.getName()));
         returnValue = false;
      } 
      if(!className.equals(message.getDefaultMessageData().getName())){
         sb.append(String.format("%s failure - Classname[%s] does not match messageData name [%s]\n", message.getClass().getName(), message.getClass().getSimpleName(), message.getName()));
         returnValue = false;
      }
      return returnValue;
   }
   
}
