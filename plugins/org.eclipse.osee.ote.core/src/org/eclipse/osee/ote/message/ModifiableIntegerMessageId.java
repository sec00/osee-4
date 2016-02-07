package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.enums.DataType;

/**
 * This should only be used in cases where we want to reuse the object.  Like in a receiver of messages where it has to decode bytes.
 * 
 * @author b1528444
 *
 */
public class ModifiableIntegerMessageId extends IntegerMessageId {

   public ModifiableIntegerMessageId(DataType type, int id) {
      super(type, id);
   }
   
   public void set(int id){
      this.id = id;
      this.hashcode = localHashCode();
   }

}
