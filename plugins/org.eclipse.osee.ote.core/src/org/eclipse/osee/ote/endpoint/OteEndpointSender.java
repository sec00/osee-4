package org.eclipse.osee.ote.endpoint;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.eclipse.osee.ote.message.event.OteEventMessage;

public interface OteEndpointSender {

   void send(OteEventMessage sendMessage);
   
   void send(ByteBuffer buffer);

   InetSocketAddress getAddress();

   void stop() throws InterruptedException;

   boolean isClosed();

   void setDebug(boolean debug);

   void start();

}
