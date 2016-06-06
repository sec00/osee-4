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
package org.eclipse.osee.ote.ui.test.manager.models;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.ws.AJavaProject;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.config.ScriptVersionConfig;
import org.eclipse.osee.ote.version.FileVersion;

public class ScriptModel extends FileModel {

   public enum ScriptInteractionEnum {
      BATCH,
      MANUAL,
      UNKNOWN
   }

   public class TestFileData {
      public String absoluteFilePath = null;
      public String classPath = null;
      public String error = "";
      public String name = null;
      public String outFile = null;
      public String projectPath = null;
      public String rawFileName;

      public ScriptVersionConfig getVersionInfo(FileVersion version) {
    	  ScriptVersionConfig scriptVersion = new ScriptVersionConfig();
    	  if (version != null) {
    		  scriptVersion.setLastChangedRevision(version.getLastChangedRevision());
    		  scriptVersion.setLocation(version.getURL());
    		  scriptVersion.setRepositoryType(version.getVersionControlSystem());
    		  scriptVersion.setLastAuthor(version.getLastAuthor());
    		  scriptVersion.setLastModificationDate(version.getLastModificationDate());
    		  scriptVersion.setModifiedFlag(version.getModifiedFlag());
    	  }
    	  return scriptVersion;
      }
   }

   private TestFileData javaFileData;
   private OutputModel outputModel;
   private TestScript testScript;

   /**
    * @param outputDir alternate output directory for tmo output files null will default to script directory
    */
   public ScriptModel(String rawFilename, String outputDir) {
      super(rawFilename);
      javaFileData = new TestFileData();
      javaFileData = getSunData(outputDir);
      javaFileData.rawFileName = rawFilename;
      outputModel = new OutputModel(javaFileData.outFile);
   }

   public ScriptInteractionEnum getInteraction() {
      if (testScript == null) {
         return ScriptInteractionEnum.UNKNOWN;
      }
      if (testScript.isBatchable()) {
         return ScriptInteractionEnum.BATCH;
      } else {
         return ScriptInteractionEnum.MANUAL;
      }
   }

   /**
    * @return Returns the outputModel.
    */
   public OutputModel getOutputModel() {
      outputModel.setRawFilename(javaFileData.outFile);
      return outputModel;

   }

   /**
    * @param alternateOutputDir place output files here instead of at location of the script
    * @return Returns sun data.
    */
   private TestFileData getSunData(String alternateOutputDir) {
      javaFileData.absoluteFilePath = getRawFilename();
      String temp = null;
      if (javaFileData.absoluteFilePath.endsWith(".java")) {
         temp = AJavaProject.getClassName(this.getRawFilename());
      }
      javaFileData.name = temp == null ? new File(getRawFilename()).getName() : temp;
      javaFileData.classPath = "";
      alternateOutputDir = alternateOutputDir.trim();
     
      
      File javaFile = new File(javaFileData.absoluteFilePath);
      File outfileFolder = javaFile.getParentFile();
      if (Strings.isValid(alternateOutputDir)) {
         outfileFolder = new File(alternateOutputDir);
         if(!outfileFolder.getAbsolutePath().equals(alternateOutputDir)){//then it's a project relative path
            if (getIFile() != null) {
               IProject project = getIFile().getProject();
               IFolder folder = project.getFolder(alternateOutputDir);
               if (!folder.exists()) {
                  ContainerCreator containerCreator =
                        new ContainerCreator(folder.getWorkspace(), folder.getFullPath());
                  try {
                     containerCreator.createContainer(new NullProgressMonitor());
                  } catch (CoreException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               } 
               outfileFolder = folder.getLocation().toFile();
            }
         }
      }
      if(!outfileFolder.exists()) {
         outfileFolder.mkdirs();
      } else if (outfileFolder.exists() && !outfileFolder.isDirectory()){
         outfileFolder = javaFile.getParentFile();
      }
      String name = javaFile.getName();
      int index = name.lastIndexOf(".java");
      if(index > 0){
         name = name.substring(0, index+1);
      }
      final String className = name;      
      File[] possibleOutfiles = outfileFolder.listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            if (name.startsWith(className) && name.endsWith(".tmo")){
               return true;
            }
            return false;
         }
      });
      javaFileData.outFile = new File(outfileFolder, name + "tmo").getAbsolutePath(); 
      long lastModified = 0;
      for(File outfile:possibleOutfiles){
         if(outfile.lastModified() > lastModified){
            lastModified = outfile.lastModified();
            javaFileData.outFile = outfile.getAbsolutePath();
         }
      }
      
      return javaFileData;
   }
   
   public void setOutfile(String path){
      javaFileData.outFile = path;
   }

   public TestFileData updateScriptModelInfo(String alternateOutputDir) {
      TestFileData javaFileData = getSunData(alternateOutputDir);
      outputModel = new OutputModel(javaFileData.outFile);
      return javaFileData;
   }

   public String getTestClass() {
      return javaFileData.name;
   }

   /**
    * @param outputModel The outputModel to set.
    */
   public void setOutputModel(OutputModel outputModel) {
      this.outputModel = outputModel;
   }

}