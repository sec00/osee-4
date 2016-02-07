package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.enums.DataType;

public enum TestMessageDataType implements DataType {
   eth1,
   eth2,
   eth3;

   @Override
   public int getToolingDepth() {
      return 0;
   }

   @Override
   public int getToolingBufferSize() {
      return 0;
   }

   
   
}
