package org.eclipse.osee.ote.message;

import java.util.HashMap;

import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;

public class MessageCaptureMessageLookup {

   private HashMap<String, Message> messageMap;
   
   public MessageCaptureMessageLookup(){
      messageMap = new HashMap<>();
   }
   
   public Message get(String name) {
      return messageMap.get(name);
   }

   public void put(String messageClass, Message newInstance) {
      messageMap.put(messageClass, newInstance);
   }

    /**
    * 
    * @param message
    * @param element
    * @return
    */
   @SuppressWarnings("unchecked")
   public <T extends Element> T getElement(Message message, T element) {
      Message localMessageObject = get(message.getClass().getName());
      return (T)localMessageObject.getElement(element.getElementPath());
   }

   public int getValue(Message message, IntegerElement element) {
      IntegerElement el = getElement(message, element);
      return el.getInt();
   }

}
