package org.eclipse.osee.ote.internal.endpoint;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.endpoint.OteUdpEndpointReceiverImpl;
import org.eclipse.osee.ote.endpoint.OteUdpEndpointSender;

public class EndpointComponent implements OteUdpEndpoint {
   
   private OteUdpEndpointReceiverImpl receiver;
   private HashMap<InetSocketAddress, OteUdpEndpointSender> senders = new HashMap<InetSocketAddress, OteUdpEndpointSender>();
   private CopyOnWriteNoIteratorList<OteUdpEndpointSender> broadcast = new CopyOnWriteNoIteratorList<OteUdpEndpointSender>(OteUdpEndpointSender.class);
   private boolean debug = false;

   public void start(){
      int port;
      try {
         port = PortUtil.getInstance().getValidPort();
         receiver = new OteUdpEndpointReceiverImpl(new InetSocketAddress(InetAddress.getLocalHost(), port));
         receiver.setDebugOutput(debug);
         receiver.start();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public synchronized void stop(){
      receiver.stop();
      for(OteUdpEndpointSender sender: senders.values()){
         try {
            sender.stop();
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }
   }
   
   @Override
   public void setDebugOutput(boolean debug) {
      this.debug = debug;
      receiver.setDebugOutput(debug);
      for(OteUdpEndpointSender sender: senders.values()){
         sender.setDebug(debug);
      }
   }

   @Override
   public InetSocketAddress getLocalEndpoint() {
      return receiver.getEndpoint();
   }

   @Override
   public synchronized OteUdpEndpointSender getOteEndpointSender(InetSocketAddress address) {
      OteUdpEndpointSender sender = senders.get(address);
      if(sender == null || (sender != null && sender.isClosed())){
         sender = new OteUdpEndpointSender(address);
         sender.setDebug(debug);
         sender.start();
         senders.put(address, sender);
      }
      return sender;
   }

   @Override
   public void addBroadcast(OteUdpEndpointSender sender) {
      broadcast.add(sender);
   }

   @Override
   public void removeBroadcast(OteUdpEndpointSender sender) {
      broadcast.remove(sender);
   }

   @Override
   public CopyOnWriteNoIteratorList<OteUdpEndpointSender> getBroadcastSenders() {
      return broadcast;
   }

}
