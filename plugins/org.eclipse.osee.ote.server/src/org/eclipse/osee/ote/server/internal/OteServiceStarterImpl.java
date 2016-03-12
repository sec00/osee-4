/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.server.internal;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.activemq.broker.BrokerService;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OTESessionManager;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServer;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;
import org.eclipse.osee.ote.properties.OtePropertiesCore;
import org.eclipse.osee.ote.server.OteServiceStarter;
import org.eclipse.osee.ote.server.PropertyParamter;
import org.eclipse.osee.ote.server.TestEnvironmentFactory;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteServiceStarterImpl implements OteServiceStarter {
   
	private IRuntimeLibraryManager runtimeLibraryManager;
	private IConnectionService connectionService;

	private BrokerService brokerService;
	private OteService service;

	private IServiceConnector serviceSideConnector;
   private OTESessionManager oteSessions;
   private OTEMasterServer masterServer;

   private ScheduledExecutorService executor;
   private OTEServer oteServerEntry;
   private ScheduledFuture<?> taskToCancel;
   private LookupRegistration lookupRegistration;
   private URI masterURI;
   private OteUdpEndpoint receiver;
   
   public OteServiceStarterImpl() {
      executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(){
         
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTE Lookup Registration");
            th.setDaemon(true);
            return th;
         }
         
      });
   }

   public void bindOTESessionManager(OTESessionManager oteSessions){
      this.oteSessions = oteSessions;
   }
   
   public void unbindOTESessionManager(OTESessionManager oteSessions){
      this.oteSessions = null;
   } 
   
	public void bindIRuntimeLibraryManager(IRuntimeLibraryManager runtimeLibraryManager){
	   this.runtimeLibraryManager = runtimeLibraryManager;
	}
	
	public void unbindIRuntimeLibraryManager(IRuntimeLibraryManager runtimeLibraryManager){
      this.runtimeLibraryManager = null;
   } 
	
	public void bindIConnectionService(IConnectionService connectionService){
	   this.connectionService = connectionService;
	}
	
	public void unbindIConnectionService(IConnectionService connectionService){
	   this.connectionService = null;
	}
	
	public void bindOTEMasterServer(OTEMasterServer masterServer){
	   this.masterServer = masterServer;
	}
	
	public void unbindOTEMasterServer(OTEMasterServer masterServer){
      this.masterServer = null;
   }
	
	public void bindOteUdpEndpoint(OteUdpEndpoint receiver){
	   this.receiver = receiver;
	}
	
	public void unbindOteUdpEndpoint(OteUdpEndpoint receiver){
	   this.receiver = receiver;
	}
	
	@Override
	public IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, String environmentFactoryClass) throws Exception {
		return start(serviceSideConnector, config, propertyParameter, null, environmentFactoryClass);
	}

	@Override
	public IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, TestEnvironmentFactory factory) throws Exception {
		return start(serviceSideConnector, config, propertyParameter, factory, null);
	}

	private IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, TestEnvironmentFactory factory, String environmentFactoryClass) throws Exception {
		if (service != null) {
			throw new OseeStateException("An ote Server has already been started.");
		}
		this.serviceSideConnector = serviceSideConnector;
		OteUdpEndpoint oteEndpoint = ServiceUtility.getService(OteUdpEndpoint.class);
		System.out.printf("SERVER CONNECTION URI[\n\ttcp://%s:%d\n]\n", oteEndpoint.getLocalEndpoint().getAddress().getHostAddress(), oteEndpoint.getLocalEndpoint().getPort());

		EnvironmentCreationParameter environmentCreationParameter =
				new EnvironmentCreationParameter(runtimeLibraryManager, serviceSideConnector, config, factory,
						environmentFactoryClass);

		service =
				new OteService(environmentCreationParameter, oteSessions, propertyParameter,
						serviceSideConnector.getProperties(), receiver);

		serviceSideConnector.init(service);

		
		
		if (propertyParameter.isLocalConnector()) {
			connectionService.addConnector(serviceSideConnector);
		}
		if (!propertyParameter.isLocalConnector()) {
			String masterURIStr = OtePropertiesCore.masterURI.getValue();
			if(masterURIStr != null){
			   try{
			      masterURI = new URI(masterURIStr);
			      oteServerEntry = createOTEServer(environmentCreationParameter, propertyParameter, service.getServiceID().toString());
			      lookupRegistration = new LookupRegistration(masterURI, masterServer, oteServerEntry, service);
			      taskToCancel = executor.scheduleWithFixedDelay(lookupRegistration, 0, 30, TimeUnit.SECONDS);
			   } catch(Throwable th){
			      OseeLog.log(getClass(), Level.SEVERE, th);
			   }
			} else {
				OseeLog.log(getClass(), Level.WARNING, "'ote.master.uri' was not set.  You must use direct connect from the client.");
			}
			
		} else {
		}
		
		FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(IHostTestEnvironment.class, service, null);
		System.out.printf("TEST SERVER INITIALIZATION COMPLETE\n");

		return service;
	}
	
	private OTEServer createOTEServer(EnvironmentCreationParameter environmentCreationParameter, PropertyParamter propertyParameter, String uuid) throws NumberFormatException, UnknownHostException{
	   OTEServer server = new OTEServer();
	   server.setName(environmentCreationParameter.getServerTitle().toString());
	   server.setStation(propertyParameter.getStation());
	   server.setVersion(propertyParameter.getVersion());
	   server.setType(propertyParameter.getType());
	   server.setComment(propertyParameter.getComment());
	   server.setStartTime(new Date().toString());
	   server.setOwner(OtePropertiesCore.userName.getValue());
	   server.setUUID(uuid);
	   server.setOteRestServer(String.format("tcp://%s:%d", receiver.getLocalEndpoint().getAddress().getHostAddress(), receiver.getLocalEndpoint().getPort()));
	   server.setOteActivemqServer(String.format("tcp://%s:%d", receiver.getLocalEndpoint().getAddress().getHostAddress(), receiver.getLocalEndpoint().getPort()));
	   return server;
	}

	@Override
	public void stop() {
		if (service != null) {
			try {
				service.updateDynamicInfo();
				service.kill();
				service = null;
			} catch (Exception ex) {
				OseeLog.log(getClass(), Level.SEVERE, ex);
			}
		}
		if (brokerService != null) {
			try {
				brokerService.stopGracefully(".*", ".*", 10000, 500);
				brokerService.stop();
			} catch (Exception ex) {
				OseeLog.log(getClass(), Level.SEVERE, ex);
			}
		}
		if (serviceSideConnector != null) {
			try {
				connectionService.removeConnector(serviceSideConnector);
			} catch (Exception ex) {
				OseeLog.log(getClass(), Level.SEVERE, ex);
			}
		}
		if(oteServerEntry != null) {
		   try{
		      lookupRegistration.stop();
		      taskToCancel.cancel(true);
		   } finally {
		      Future<OTEMasterServerResult> removeServer = masterServer.removeServer(masterURI, oteServerEntry);
		      try {
               removeServer.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
               OseeLog.log(getClass(), Level.INFO, e);
            } catch (ExecutionException e) {
               OseeLog.log(getClass(), Level.INFO, e);
            } catch (TimeoutException e) {
               OseeLog.log(getClass(), Level.INFO, e);
            }
		   }
		}
		brokerService = null;
	}

	private static class LookupRegistration implements Runnable {

      private final OTEMasterServer masterServer;
      private final OTEServer server;
      private final URI uri;
      private volatile boolean run = true;
      private final OteService service;

      public LookupRegistration(URI uri, OTEMasterServer masterServer, OTEServer server, OteService service) {
         this.masterServer = masterServer;
         this.server = server;
         this.uri = uri;
         this.service = service;
      }

      @Override
      public void run() {
         try{
            if(run){
               server.setConnectedUsers(service.getProperties().getProperty("user_list", "N.A.").toString());
               Future<OTEMasterServerResult> result = masterServer.addServer(uri, server);
               OTEMasterServerResult addServerResult = result.get(30, TimeUnit.SECONDS);
               if(!(addServerResult != null && addServerResult.isSuccess())){
                  try{
                     Thread.sleep(1000*60*3);//wait 3 minutes before trying again
                  } catch(Throwable th){
                     //don't care if we're woken up
                  }
               }
            }
         } catch (Throwable th){
            th.printStackTrace();
         }
      }
      
      public void stop(){
         run = false;
      }
      
	}

}
