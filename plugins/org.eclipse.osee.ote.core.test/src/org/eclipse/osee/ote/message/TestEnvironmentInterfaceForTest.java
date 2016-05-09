package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.List;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IEnvironmentFactory;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentListener;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;
import org.osgi.util.tracker.ServiceTracker;

public class TestEnvironmentInterfaceForTest implements TestEnvironmentInterface {

   private ITimerControl timerControl;

   public TestEnvironmentInterfaceForTest(ITimerControl timerControl){
      this.timerControl = timerControl;
      
   }
   
   @Override
   public ServiceTracker getServiceTracker(String clazz) {
      return null;
   }

   @Override
   public ICommandHandle addCommand(ITestServerCommand cmd) throws ExportException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IRunManager getRunManager() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IRuntimeLibraryManager getRuntimeManager() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isInBatchMode() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void setBatchMode(boolean isInBatchMode) {
      // TODO Auto-generated method stub

   }

   @Override
   public void addEnvironmentListener(ITestEnvironmentListener listener) {
      // TODO Auto-generated method stub

   }

   @Override
   public boolean addTask(EnvironmentTask task) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public long getEnvTime() {
      return timerControl.getEnvTime();
   }

   @Override
   public IExecutionUnitManagement getExecutionUnitManagement() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ITestLogger getLogger() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<String> getQueueLabels() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Object getModel(String modelClassName) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IScriptControl getScriptCtrl() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public byte[] getScriptOutfile(String filepath) throws RemoteException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ITestStation getTestStation() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ITimerControl getTimerCtrl() {
      return timerControl;
   }

   @Override
   public int getUniqueId() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public URL setBatchLibJar(byte[] batchJar) throws IOException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ICancelTimer setTimerFor(ITimeout listener, int time) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void shutdown() {
      // TODO Auto-generated method stub

   }

   @Override
   public File getOutDir() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Remote getControlInterface(String id) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void registerControlInterface(String id, Remote controlInterface) {
      // TODO Auto-generated method stub

   }

   @Override
   public IServiceConnector getConnector() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IEnvironmentFactory getEnvironmentFactory() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setupOutfileDir(String outfileDir) throws IOException {
      // TODO Auto-generated method stub

   }

}
