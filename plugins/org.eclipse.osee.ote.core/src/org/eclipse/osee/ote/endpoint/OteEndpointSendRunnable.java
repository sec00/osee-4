package org.eclipse.osee.ote.endpoint;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.collections.ObjectPool;

public class OteEndpointSendRunnable implements Runnable {

   private static final int SEND_BUFFER_SIZE = 1024 * 512;
   
   private ArrayBlockingQueue<AddressBuffer> toSend;
   private ObjectPool<AddressBuffer> buffers;

   public OteEndpointSendRunnable(ArrayBlockingQueue<AddressBuffer> toSend, ObjectPool<AddressBuffer> buffers) {
      this.toSend = toSend;
      this.buffers = buffers;
   }

   @Override
   public void run() {
      DatagramChannel threadChannel = null; 
      try {
         threadChannel = openAndInitializeDatagramChannel();
         boolean keepRunning = true;
         final List<AddressBuffer> dataToSend = new ArrayList<AddressBuffer>(32);
         System.setSecurityManager(null);
         while(keepRunning){
            try{
               dataToSend.clear();
               if (toSend.drainTo(dataToSend) < 1) {
                  try {
                     // block until something is available
                     dataToSend.add(toSend.take());                    
                  } catch (InterruptedException e) {
                     keepRunning = false;
                     continue;
                  }
               }
               int size = dataToSend.size();
               for (int i = 0; i < size; i++) {
                  AddressBuffer data = dataToSend.get(i);
                  if (data == OteUdpEndpointSender.POISON_PILL) {
                     keepRunning = false;
                     break;
                  }
                  threadChannel.send(data.getBuffer(), data.getAddress());
               }
            } catch (ClosedByInterruptException ex){
               OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
               threadChannel = openAndInitializeDatagramChannel();
            } catch (AsynchronousCloseException ex){
               OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
               closeChannel(threadChannel);
               threadChannel = openAndInitializeDatagramChannel();
            } catch (ClosedChannelException ex){
               OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
               closeChannel(threadChannel);
               threadChannel = openAndInitializeDatagramChannel();
            } catch (IOException ex){
               OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
            } finally {
               int size = dataToSend.size();
               for (int i = 0; i < size; i++) {
                  buffers.returnObj(dataToSend.get(i));
               }
            }
         } 
      } catch (IOException ex){
         OseeLog.log(getClass(), Level.SEVERE, "Error opening DatagramChannel.  Ending OteEndpointSendRunnable unexpectedly.", ex);
      } finally{
         closeChannel(threadChannel);
      }

   }
   
   private void closeChannel(DatagramChannel channel){
      try {
         if (channel != null) {
            channel.close();
         }
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, "Error trying to close channel", e);
      } 
   }

   private DatagramChannel openAndInitializeDatagramChannel() throws IOException {
      DatagramChannel channel = DatagramChannel.open();
      if (channel.socket().getSendBufferSize() < SEND_BUFFER_SIZE) {
         channel.socket().setSendBufferSize(SEND_BUFFER_SIZE);
      }
      channel.socket().setReuseAddress(true);
      channel.configureBlocking(true);
      return channel;
   }
}