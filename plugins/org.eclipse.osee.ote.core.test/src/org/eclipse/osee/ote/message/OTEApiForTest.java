package org.eclipse.osee.ote.message;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.OTEServerRuntimeCache;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.model.IModelManager;
import org.eclipse.osee.ote.io.OTEServerFolder;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;

public class OTEApiForTest implements OTEApi {

   private TestEnvironmentInterface env;
   private OTEServerFolder serverFolder;

   public OTEApiForTest(OTEServerFolder serverFolder, TestEnvironmentInterface env) {
      this.serverFolder = serverFolder;
      this.env = env;
   }
   
   @Override
   public OTEServerFolder getServerFolder() {
      return serverFolder;
   }

   @Override
   public OTEServerRuntimeCache getRuntimeCache() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IModelManager getModelManager() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public TestEnvironmentInterface getTestEnvironment() {
      return env;
   }

   @Override
   public IRemoteMessageService getRemoteMessageService() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Future<ConfigurationStatus> loadConfiguration(Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Future<ConfigurationStatus> resetConfiguration(OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Future<ConfigurationStatus> getConfiguration() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Future<ConfigurationStatus> downloadConfigurationJars(Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Class<?> loadFromScriptClassLoader(String clazz) throws ClassNotFoundException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Class<?> loadFromRuntimeLibraryLoader(String clazz) throws ClassNotFoundException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IHostTestEnvironment getIHostTestEnvironment() {
      // TODO Auto-generated method stub
      return null;
   }

}
