package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.enums.DataType;

public class StringMessageId implements MessageId {

   private final DataType type;

   protected String id;
   protected int hashcode;

   public StringMessageId(DataType type, String id){
      this.type = type;
      this.id = id;
      
      hashcode = localHashCode();
   }

   protected int localHashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
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
      if (!(obj instanceof StringMessageId))
         return false;
      StringMessageId other = (StringMessageId) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
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
      return "StringMessageId [type=" + type + ", id=" + id + "]";
   }
   
   
}
