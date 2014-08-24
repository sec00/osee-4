package org.eclipse.osee.ote.internal;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.RequestHostEnvironmentProperties;
import org.eclipse.osee.ote.remote.messages.SerializedConfigurationAndResponse;
import org.eclipse.osee.ote.remote.messages.SerializedDisconnectRemoteTestEnvironment;
import org.eclipse.osee.ote.remote.messages.SerializedRequestRemoteTestEnvironment;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class RemoteOteApiHandler {

   private EventAdmin eventAdmin;
   private OTEApi oteApi;
   private OteUdpEndpoint oteEndpoint;
   private ServiceRegistration<EventHandler> configureAndResponse;
   private ServiceRegistration<EventHandler> getProperties;
   private ServiceRegistration<EventHandler> getConnection;
   private ServiceRegistration<EventHandler> disconnect;

   /**
    * osgi
    */
   public void bindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = eventAdmin;
   }
   
   /**
    * osgi
    */
   public void unbindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = null;
   }
   
   /**
    * osgi
    */
   public void bindOTEApi(OTEApi oteApi){
      this.oteApi = oteApi;
   }
   
   /**
    * osgi
    */
   public void unbindOTEApi(OTEApi oteApi){
      this.oteApi = null;
   }
   
   /**
    * osgi
    */
   public void bindOteUdpEndpoint(OteUdpEndpoint oteEndpoint){
      this.oteEndpoint = oteEndpoint;
   }
   
   /**
    * osgi
    */
   public void unbindOteUdpEndpoint(OteUdpEndpoint oteEndpoint){
      this.oteEndpoint = null;
   }
   
   /**
    * osgi
    */
   public void start(){
      configureAndResponse = OteEventMessageUtil.subscribe(SerializedConfigurationAndResponse.EVENT, new ConfigureAndResponseListener(eventAdmin, oteEndpoint, oteApi));
      getProperties =  OteEventMessageUtil.subscribe(RequestHostEnvironmentProperties.TOPIC, new GetPropertiesListener(eventAdmin, oteEndpoint, oteApi));
      getConnection =  OteEventMessageUtil.subscribe(SerializedRequestRemoteTestEnvironment.TOPIC, new ConnectionListener(eventAdmin, oteEndpoint, oteApi));
      disconnect =  OteEventMessageUtil.subscribe(SerializedDisconnectRemoteTestEnvironment.TOPIC, new DisconnectListener(eventAdmin, oteEndpoint, oteApi));
   }
   
   /**
    * osgi 
    */
   public void stop(){
      configureAndResponse.unregister();
      getProperties.unregister();
      getConnection.unregister();
      disconnect.unregister();
   }
  
}
