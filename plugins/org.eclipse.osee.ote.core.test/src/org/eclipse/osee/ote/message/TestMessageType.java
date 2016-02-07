package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.enums.DataType;

public enum TestMessageType implements DataType {
   eth1(2, 64*1024),
   eth2(2, 64*1024), eth3(2, 64*1024);

   
   private int depth;
   private int bufferSize;

   TestMessageType(int depth, int bufferSize){
      this.depth = depth;
      this.bufferSize = bufferSize;
   }
   
   @Override
   public int getToolingDepth() {
      return depth;
   }

   @Override
   public int getToolingBufferSize() {
      return bufferSize;
   }
   
   
}
