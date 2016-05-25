package org.eclipse.osee.ote.message;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class BinaryMessageRecorderDataCache {
   
   private final ArrayBlockingQueue<ByteBuffer> dataToProcess;
   private final ArrayBlockingQueue<ByteBuffer> availableByteBuffers;

   public BinaryMessageRecorderDataCache(int byteBufferCount, int byteBufferSize){
      dataToProcess = new ArrayBlockingQueue<ByteBuffer>(byteBufferCount);
      availableByteBuffers = new ArrayBlockingQueue<ByteBuffer>(byteBufferCount);
      for(int i = 0; i < byteBufferCount;i++){
         availableByteBuffers.offer(ByteBuffer.allocate(byteBufferSize));
      }
   }
   
   public void giveBufferBack(ByteBuffer buffer){
      buffer.clear();
      availableByteBuffers.offer(buffer);
   }
   
   public ByteBuffer takeBufferForCopy(int length){
      ByteBuffer buffer = availableByteBuffers.poll();
      if(buffer != null && buffer.capacity() >= length){
         buffer.clear();
         return buffer;
      } else {
         return getNewBuffer(length);
      }
   }

   private ByteBuffer getNewBuffer(int size) {
      return ByteBuffer.allocate(size);
   }
   
   public void giveBufferForProcessing(ByteBuffer buffer){
      dataToProcess.offer(buffer);
   }
   
   public void drainDataToProcess(List<ByteBuffer> data){
      dataToProcess.drainTo(data);
   }

}
