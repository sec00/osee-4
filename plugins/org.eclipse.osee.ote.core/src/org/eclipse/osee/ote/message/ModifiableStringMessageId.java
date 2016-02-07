package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.enums.DataType;

public class ModifiableStringMessageId extends StringMessageId {

   public ModifiableStringMessageId(DataType type, String id){
      super(type, id);
   }

   public void set(String id){
      this.id = id;
      this.hashcode = localHashCode();
   }
   
}
