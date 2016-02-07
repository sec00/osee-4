package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.enums.DataType;

public class TestMessageId implements MessageId {

   private final DataType type;
   private final int id;
   
   private final int hashcode;

   public TestMessageId(DataType type, int id){
      this.type = type;
      this.id = id;
      
      hashcode = localHashCode();
   }

   private int localHashCode() {
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
      if (getClass() != obj.getClass())
         return false;
      TestMessageId other = (TestMessageId) obj;
      if (id != other.id)
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }
   
     
   
}
