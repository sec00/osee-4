/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andrew M. Finkbeiner
 */
public class PortUtil {

   private static ConcurrentHashMap<InetAddress, PortUtil> singleton = new ConcurrentHashMap<>();
   @SuppressWarnings("unused")
   private ServerSocket ss;
   
   
   private InetAddress address;

   public static PortUtil getInstance(InetAddress address) {
      PortUtil port = singleton.get(EthernetUtil.getLocalhost());//default to a single block
      if (port == null) {
         port = new PortUtil(address);
         singleton.put(address, port);
      }
      return port;
   }

   int basePort = 18000;
   int nextPort = 18000;

   private PortUtil(InetAddress address) {
      this.address = address;
      String port = System.getProperty("osee.port.scanner.start.port", "18000");
      try{
         basePort = nextPort = Integer.parseInt(port);
      } catch (Throwable th){
         
      }
      for (int j = nextPort; j < 64000; j += 250) {
         if (checkIfPortIsTaken(j, address)) {
            basePort = nextPort = j;
            try {
               ss = new ServerSocket(basePort);
            } catch (IOException e) {
               e.printStackTrace();
            }
            break;
         }
      }
   }

   public void computeNewBasePort() {
      basePort = nextPort = basePort + 1000;
      for (int j = nextPort; j < 64000; j += 250) {
         if (checkIfPortIsTaken(j, address)) {
            basePort = nextPort = j;
            try {
               ss = new ServerSocket(basePort);
            } catch (IOException e) {
               e.printStackTrace();
            }
            break;
         }
      }
   }

   public int getValidPort() throws IOException {
      int port = getConsecutiveValidPorts(1);
      return port;
   }

   public int getConsecutiveValidPorts(int numberOfPorts) throws IOException {
      try {
         int returnVal = getConsecutiveLocalPorts(numberOfPorts, address);
         nextPort = returnVal + numberOfPorts;
         return returnVal;
      } catch (Exception e) {
         e.printStackTrace();
         IOException ioE = new IOException("Unable to get a valid port.");
         ioE.initCause(e);
         throw ioE;
      }
   }

   /**
    * @param numberOfPorts The number of consecutive available ports to find
    * @return The port of first number in the sequence of valid ports
    */
   private int getConsecutiveLocalPorts(int numberOfPorts, InetAddress address) throws Exception {
      if (nextPort >= basePort + 250 - numberOfPorts) {
         nextPort = basePort;
      }
      for (int i = nextPort; i < basePort + 250; i++) {
         boolean passed = true;
         for (int j = i; j < numberOfPorts + i; j++) {
            if (!checkIfPortIsTaken(j, address)) {
               passed = false;
               break;
            }
         }
         if (passed) {
            return i;
         }
      }
      throw new Exception("Unable to find valid port.");
   }

   private boolean checkIfPortIsTaken(int port, InetAddress address) {
      return checkTcpIp(port, address) && checkUdpPort(port, address);
   }

   private boolean checkTcpIp(int port, InetAddress address) {
      try {
         ServerSocket socket;
         socket = new ServerSocket(port);
         socket.close();
      } catch (Exception e) {
         return false;
      }
      return true;
   }

   private boolean checkUdpPort(int port, InetAddress address) {
      try {
         DatagramSocket ds = new DatagramSocket(port, address);
         ds.close();
         ds.disconnect();
      } catch (Exception e) {
         return false;
      }
      return true;
   }
}
