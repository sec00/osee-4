package org.eclipse.osee.ote.message;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class BinaryMessageRecorderDataCache {
   
   private final ArrayBlockingQueue<ByteBuffer> dataToProcess;
   private final ArrayBlockingQueue<ByteBuffer> availableByteBuffers;

   public BinaryMessageRecorderDataCache(int byteBufferCount, int byteBufferSize){
      dataToProcess = new ArrayBlockingQueue<ByteBuffer>(byteBufferCount*100);
      availableByteBuffers = new ArrayBlockingQueue<ByteBuffer>(byteBufferCount * 4);
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
      if(!dataToProcess.offer(buffer)){
         throw new IllegalStateException("Data to process queue is full we are dropping recording packets.");
      }
   }
   
   public void drainDataToProcess(List<ByteBuffer> data){
      dataToProcess.drainTo(data);
   }

}
