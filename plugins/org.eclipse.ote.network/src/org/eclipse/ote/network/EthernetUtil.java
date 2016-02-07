package org.eclipse.ote.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class EthernetUtil {
   
   private static Set<InetAddress> ipv4 = new HashSet<InetAddress>();
   private static Set<InetAddress> ipv6 = new HashSet<InetAddress>();
   private static boolean labInterfaceAvailable = false;
   private static InetAddress localhost;
   private static InetAddress loopback;
   private static InetAddress defaultLocalTestInterface;
   private static InetAddress defaultWanInterface;
   
   static {
      updateAvailableInterfaces();
      try {
         localhost = InetAddress.getLocalHost();
         loopback = InetAddress.getLoopbackAddress();
      } catch (UnknownHostException e) {
      }
     
      InetAddress address;
      try {
         address = InetAddress.getByAddress("192.168.0.254", new byte[] {(byte) 192, (byte) 168, 0, (byte) 254});
         if (isAvailable(address)) {
            labInterfaceAvailable = true;
         } 
      } catch (UnknownHostException e) {
      }
      InetAddress address2;
      try {
         address2 = InetAddress.getByAddress("192.168.1.254", new byte[] {(byte) 192, (byte) 168, 1, (byte) 254});
         if (isAvailable(address2)){
            labInterfaceAvailable = true;
         }
      } catch (UnknownHostException e) {
      }
      determineNetworkConfiguration();
      
      Thread th = new Thread(new Runnable(){

         @Override
         public void run() {
            try{
               while(true){
                  boolean localHostSame = true;
                  while(localHostSame){
                     try {
                        Thread.sleep(50);
                     } catch (InterruptedException e) {
                     }
                     InetAddress local = null;
                     local = InetAddress.getLocalHost();
                     if(!localhost.equals(local)){
                        localHostSame = false;
                        localhost = local;
                     }
                  }
                  System.out.println("changed");
                  System.out.println(getLocalhost());
                  System.out.println(getLoopback()); 
                  if(!isLabInterfaceAvailable() && !isLocalRange(getLocalhost())){
                     System.out.println("moving from local to on a wan so I am shutting down so we do not spam the boeing network.  goodbye from EthernetUtil MONITOR");
                     System.exit(1);
                  }
               }
            } catch (Throwable th){
               System.out.println("goodbye from EthernetUtil MONITOR");
               System.exit(1);
            }
         }
      });
      th.setDaemon(true);
      th.setName("Ethernet Interface Monitor");
      th.start();
   }
   
   public static boolean isLocalRange(InetAddress address){
      if(address.isLoopbackAddress()){
         return true;
      }
      if(isIpv4(address)){
         int first = address.getAddress()[0] & 0xFF;
         int second = address.getAddress()[1] & 0xFF;
         if(first == 10){
            return true;
         } else if (first == 172 && (second >= 16 && second <= 31)){
            return true;
         } else if (first == 192 && second == 168){
            return true;
         }
      } else {
         int first = address.getAddress()[0] & 0xFF;
         if(address.isLinkLocalAddress()){
            return true;
         } else if ( first == 0xfd){
            return true;
         }
      }
      return false;
   }
   
   private static boolean isIpv4(InetAddress address) {
      return address instanceof Inet4Address;
   }

   public static InetAddress getLocalhost(){
      return localhost;
   }
   
   public static InetAddress getLoopback(){
      return loopback;
   }
   
   public static boolean isLabInterfaceAvailable(){
      return labInterfaceAvailable;
   }
   
   public static boolean isAvailable(InetAddress address){
      return ipv4.contains(address) || ipv6.contains(address);
   }
   
   public static boolean isAvailable(String regex){
      for(InetAddress eth:ipv4){
         if(eth.toString().contains(regex)){
            return true;
         }
      }
      for(InetAddress eth:ipv6){
         if(eth.toString().contains(regex)){
            return true;
         }
      }
      return false;
   }
   
   public static void updateAvailableInterfaces(){
      try {
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
         ArrayList<NetworkInterface> networks = Collections.list(interfaces);
         for(NetworkInterface eth:networks){
            Enumeration<InetAddress> stuff = eth.getInetAddresses();
            ArrayList<InetAddress> more = Collections.list(stuff);
            for(InetAddress what:more){
               if(what instanceof Inet4Address){
                  ipv4.add(what);
               } else {
                  ipv6.add(what);
               }
            }
         }
      } catch (SocketException e) {
         e.printStackTrace();
      }
   }
   
   public static void determineNetworkConfiguration(){
      defaultLocalTestInterface = getLoopback();
      defaultWanInterface = localhost;
   }
   
//   public static InetAddress getMyAddress(){
//      if(isLabInterfaceAvailable()){
//         return localhost;
//      } 
//   }
//   
   
   
   
   public static void main(String[] args){
      boolean localHostSame = true;
      System.out.println("done");
//      while(localHostSame){
//         try {
//            Thread.sleep(50);
//         } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//         }
//         InetAddress local = null;
//         try {
//            local = InetAddress.getLocalHost();
//         } catch (UnknownHostException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//         }
//         if(!localhost.equals(local)){
//            localHostSame = false;
//            localhost = local;
//         }
//      }
//      System.out.println("changed");
//      System.out.println(getLocalhost());
      
//      System.out.println(getLookback());
//      System.out.println(localhost.isAnyLocalAddress());
//      System.out.println(localhost.isLinkLocalAddress());
//      System.out.println(localhost.isLoopbackAddress());
//      System.out.println(localhost.getHostAddress());
   }

   public static InetAddress getValidAddress(String address) throws UnknownHostException {
      return loopback;
   }
   
   public static InetAddress getServerTestAddress(){
      if(isLocalRange(defaultLocalTestInterface) && !isLabInterfaceAvailable()){
         return loopback;
      } else {
         return defaultLocalTestInterface;
      }
   }
   
   public static InetAddress getServerClientAddress(){
      if(isLocalRange(defaultWanInterface) && !isLabInterfaceAvailable()){
         return loopback;
      } else {
         return defaultWanInterface;
      }
   }

   public static InetAddress getByName(String host) throws UnknownHostException {
      InetAddress address = InetAddress.getByName(host);
      if(isLocalRange(address) && !isLabInterfaceAvailable()){
         return loopback;
      } else {
         return address;
      }
   }
   
}
