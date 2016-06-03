package org.eclipse.osee.ote.message;

import java.io.File;
import java.util.List;

import org.eclipse.osee.ote.io.OTEServerFolder;

public class OTEServerFolderForTest implements OTEServerFolder {

   @Override
   public void cleanOldBatchFolders() {
      // TODO Auto-generated method stub

   }

   @Override
   public File getRootFolder() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getServerFolder() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getBatchesFolder() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getCacheFolder() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getBatchLogFile(File batchFolder) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getNewBatchFolder() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getNewBatchFolder(String suffix) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getNewServerFolder() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getCurrentServerFolder() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void markFolderForDelete(File folder) {
      // TODO Auto-generated method stub

   }

   @Override
   public void unmarkFolderForDelete(File folder) {
      // TODO Auto-generated method stub

   }

   @Override
   public File getResultsFile(File outfile) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getBatchStatusFile(File batchFolder) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public File getBatchRunList(File batchFolder) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<File> getRunningServerFolders() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void removeServerRunningFile(File serverFolder) {
      // TODO Auto-generated method stub

   }

   @Override
   public void setTestDataFolder(File testDataFolder) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public File getTestDataFolder() {
      // TODO Auto-generated method stub
      return null;
   }

}
