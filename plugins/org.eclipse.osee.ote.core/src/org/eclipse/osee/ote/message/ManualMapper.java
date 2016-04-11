package org.eclipse.osee.ote.message;

public interface ManualMapper {

   /**
    * 
    * @param targetMessageClass
    * @param mappedMessageClass
    * @param mappedMessageField
    * @return
    */
   String getName(String targetMessageClass, String mappedMessageClass, String mappedMessageField);

}
