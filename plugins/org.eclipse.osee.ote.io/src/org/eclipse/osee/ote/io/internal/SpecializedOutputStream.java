package org.eclipse.osee.ote.io.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.ote.io.SystemOutputListener;

public class SpecializedOutputStream extends OutputStream {

   private OutputStream outputStream;
   private CopyOnWriteArrayList<SystemOutputListener> listeners;

   public SpecializedOutputStream(OutputStream outputStream) {
      this.outputStream = outputStream;
      this.listeners = new CopyOnWriteArrayList<SystemOutputListener>();
   }

   public void add(SystemOutputListener listener){
      listeners.add(listener);
   }
   
   public void remove(SystemOutputListener listner){
      listeners.remove(listner);
   }
   
   @Override
   public void write(int arg0) throws IOException {
      
   }

   @Override
   public void close() throws IOException {
      outputStream.close();
      for(SystemOutputListener listner:listeners){
         listner.close();
      }
   }

   @Override
   public void flush() throws IOException {
      outputStream.flush();
      for(SystemOutputListener listner:listeners){
         listner.flush();
      }
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
      outputStream.write(b, off, len);
      for(SystemOutputListener listner:listeners){
         listner.write(b, off, len);
      }
   }

   @Override
   public void write(byte[] b) throws IOException {
      outputStream.write(b);
      for(SystemOutputListener listner:listeners){
         listner.write(b);
      }
   }

}
