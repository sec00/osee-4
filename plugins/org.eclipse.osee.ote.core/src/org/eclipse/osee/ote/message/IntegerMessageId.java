package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.enums.DataType;

public class IntegerMessageId implements MessageId {

   private final DataType type;

   protected int id;
   protected int hashcode;

   public IntegerMessageId(DataType type, int id){
      this.type = type;
      this.id = id;
      
      hashcode = localHashCode();
   }

   protected int localHashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }
   
   @Override
   public int hashCode() {
      return hashcode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof IntegerMessageId))
         return false;
      IntegerMessageId other = (IntegerMessageId) obj;
      if (id != other.id)
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "IntegerMessageId [type=" + type + ", id=" + id + "]";
   }
   
   
   
}
