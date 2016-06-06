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
package org.eclipse.osee.ote.ui.test.manager.jobs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.ui.markers.MarkerPlugin;
import org.eclipse.osee.ote.ui.test.manager.connection.ScriptManager;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.pages.contributions.TestManagerStorageKeys;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;

/**
 * @author Roberto E. Escobar
 */
public class StoreOutfileJob extends Job {

   private static final Matcher matcher = Pattern.compile("(.*?)(\\.\\d+\\.tmo|\\.tmo)").matcher("");
   
   
   private final ScriptManager userEnvironment;
   private final ScriptTask scriptTask;
   private final boolean isValidRun;
   private final TestManagerEditor testManagerEditor;
   private final ITestEnvironment env;

   private final String clientOutfilePath;

   private final String serverOutfilePath;
   private String serverDataFilePath;

   public StoreOutfileJob(ITestEnvironment env, TestManagerEditor testManagerEditor, ScriptManager userEnvironment, ScriptTask scriptTask, String clientOutfilePath, String serverOutfilePath, String serverDataFilePath, boolean isValidRun) {
      super("Store: " + scriptTask.getName());
      this.env = env;
      this.scriptTask = scriptTask;
      this.testManagerEditor = testManagerEditor;
      this.userEnvironment = userEnvironment;
      this.isValidRun = isValidRun;
      this.clientOutfilePath = clientOutfilePath;
      this.serverOutfilePath = serverOutfilePath;
      this.serverDataFilePath = serverDataFilePath;
   }

   public static void scheduleJob(Job job) {
      job.setUser(false);
      job.setPriority(Job.SHORT);
      job.schedule();
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         if (isValidRun == true) {
            try {
               storeOutfile(scriptTask);
            } catch (Exception e) {
               return new Status(IStatus.ERROR, TestManagerPlugin.PLUGIN_ID, "Failed to write out file to workspace", e);
            }
         }
         //         scriptTask.computeExists();
         userEnvironment.updateScriptTableViewer(scriptTask);
         try {
            Thread.sleep(2000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         processOutFile(scriptTask);
      } catch (Exception ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
      }
      return Status.OK_STATUS;
   }

   public void processOutFile(ScriptTask task) {
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "Processing Outfile: " + task.getName());
      //      task.computeExists();
      File xmlSourceFile = task.getScriptModel().getOutputModel().getFile();
      IFile javaSourceIFile = task.getScriptModel().getIFile();

      if (!xmlSourceFile.exists()) {
         TestManagerPlugin.getInstance().getOteConsoleService().writeError("Output File Not Created");
      } else {
         // Refresh the parent so the workspace knows the new tmo file exists
         AWorkspace.refreshResource(javaSourceIFile);
         task.getScriptModel().getOutputModel().updateTestPointsFromOutfile();
         int failedPoints = task.getScriptModel().getOutputModel().getFailedTestPoints();
         userEnvironment.updateScriptTableViewer(scriptTask);
         if (failedPoints > 0) {
            // Print fails in red, but don't force the console to popup
            TestManagerPlugin.getInstance().getOteConsoleService().write(
               String.format("Test Point Failures => %s[%d]", task.getName(), failedPoints), OseeConsole.CONSOLE_ERROR,
               false);
         }
      }
   }

   private boolean isKeepSavedOutfileEnabled() {
      return testManagerEditor.getPropertyStore().getBoolean(TestManagerStorageKeys.KEEP_OLD_OUTFILE_COPIES_ENABLED_KEY);
   }
   
   private boolean isSaveScriptDataFileEnabled() {
      return testManagerEditor.getPropertyStore().getBoolean(TestManagerStorageKeys.SAVE_SCRIPT_DATA_FILE_ENABLED_KEY);
   }

   private void storeOutfile(ScriptTask scriptTask) throws Exception {
      File output = new File(clientOutfilePath);
      if (clientOutfilePath.equals(serverOutfilePath) != true) {
         // the paths are different so we need to copy the file
         byte[] outBytes = env.getScriptOutfile(serverOutfilePath);
         if (outBytes != null && outBytes.length > 0) {
            if (isKeepSavedOutfileEnabled()) {
               output = findNextDestination(output);
            }
            IFile file = AIFile.constructIFile(output.getAbsolutePath());
            if (file != null) {
               AIFile.writeToFile(file, new ByteArrayInputStream(outBytes));
               MarkerPlugin.addMarkers(file);
            } else {
               Lib.writeBytesToFile(outBytes, output);
            }
            scriptTask.getScriptModel().setOutfile(output.getAbsolutePath());
         }
      }
      if(isSaveScriptDataFileEnabled()){
         byte[] dataBytes = env.getScriptOutfile(serverDataFilePath);
         String outputPath = output.getAbsolutePath();
         if (dataBytes != null && dataBytes.length > 0) {
            int index = outputPath.lastIndexOf(".");
            String clientDataPath = outputPath;
            if(index != -1){
               clientDataPath = outputPath.substring(0, index) + ".zip";
            }
            IFile file = AIFile.constructIFile(clientDataPath);
            if (file != null) {
               AIFile.writeToFile(file, new ByteArrayInputStream(dataBytes));
            } else {
               Lib.writeBytesToFile(dataBytes, new File(clientDataPath));
            }
         }
      }
   }

   private File findNextDestination(File destinationFile) {
      File returnVal = destinationFile;
      if (destinationFile != null && destinationFile.exists() && destinationFile.isFile() && destinationFile.canRead()) {
         String fileName = destinationFile.getName();
         String path = destinationFile.getParent();
         matcher.reset(destinationFile.getName());
         if(matcher.matches()){
            fileName = matcher.group(1);
         }
         int fileNum = 1;
         returnVal = new File(String.format("%s%s%s.%d.tmo", path, File.separator, fileName, fileNum));
         if (returnVal.exists()) {
            while (returnVal.exists()) {
               fileNum++;
               returnVal = new File(String.format("%s%s%s.%d.tmo", path, File.separator, fileName, fileNum));
            }
         }

      }
      return returnVal;
   }
}