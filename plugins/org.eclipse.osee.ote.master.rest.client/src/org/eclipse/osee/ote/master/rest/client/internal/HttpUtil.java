package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

public class HttpUtil {

   private static Set<URI> failedConnections = new HashSet<URI>();
   
   public static boolean canConnect(URI targetUri) {
      try{
         HttpURLConnection connection = (HttpURLConnection)targetUri.toURL().openConnection();
         connection.setRequestMethod("HEAD");
         int responseCode = connection.getResponseCode();
         if(responseCode == 200){
            return true;
         }
      } catch (Throwable th){
         if(!failedConnections.contains(failedConnections)){
            failedConnections.add(targetUri);
            OseeLog.log(HttpUtil.class, Level.INFO, String.format("Unable to connect to [%s]", targetUri));
         }
         OseeLog.log(HttpUtil.class, Level.FINE, th);
      }
      return false;
   }
}
