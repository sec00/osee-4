package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.io.OTEServerFolder;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.save.ElementSave;
import org.eclipse.osee.ote.message.save.ElementSaveFile;
import org.eclipse.osee.ote.message.save.OTEJsonSaveFile;

/**
 * This class will use all of the {@link Checker} classes that have been added to it to check the {@link MessageCaptureDataIterator} 
 * that was passed in at construction time.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageCaptureChecker {

   private MessageCaptureDataIterator dataIterator;
   private ArrayList<Checker> checkers;

   public MessageCaptureChecker(MessageCaptureDataIterator dataIterator) {
      this.checkers = new ArrayList<>();
      this.dataIterator = dataIterator;
   }

   public void add(Checker... check) {
      for(Checker ch:check){
         checkers.add(ch);
      }
   }

   /**
    * Check the given data with the {@link Checker}'s that have been added to this object.
    * 
    * @throws IOException
    */
   public void check() throws IOException {
      for(Checker check:checkers){
         check.init(dataIterator.getMessageLookup());
      }
      while(dataIterator.hasNext()){
         MessageCaptureDataStripe stripe = dataIterator.next();
         for(Checker check:checkers){
            check.check(stripe);
         }
      }
      for(Checker check:checkers){
         check.complete(dataIterator.getMessageLookup());
      }
   }

   public void close() throws IOException {
      dataIterator.close();
   }
   
   /**
    * Log the results of the check to the outfile.  The {@link Checker} implementation is responsible for what logging happens.
    * 
    * @param accessor
    */
   public void logToOutfile(ITestAccessor accessor){
      for(Checker check:checkers){
         check.logToOutfile(accessor);
      }
   }
   
   /**
    * Log the results of the check to the outfile.  The {@link Checker} implementation is responsible for what logging happens.
    * 
    * @param accessor
    */
   public void logToOutfile(ITestAccessor accessor, CheckGroup checkGroup){
      for(Checker check:checkers){
         check.logToOutfile(accessor, checkGroup);
      }
   }
   
   /**
    * This will save the .xyplot data to the script temporary area so that it will be added to the .zip file that can be retrieved by 
    * the client. 
    * 
    * @param filename The name to use when saving the file
    * @throws FileNotFoundException
    */
   public void saveData(File filename) throws FileNotFoundException{
      ElementSaveFile data = new ElementSaveFile();
      for(Checker check:checkers){
         ElementSave save = check.getElementSave();
         if(save != null){
            data.getElements().add(save);
         }
      }      
      OTEJsonSaveFile.writeSaveFile(filename, data);
   }
   
   /**
    * This will save the .xyplot data to the script temporary area so that it will be added to the .zip file that can be retrieved by 
    * the client.  
    * 
    * @param env
    * @throws IOException
    */
   public void saveData(TestEnvironment env) throws IOException{
      OTEServerFolder serverFolder = ServiceUtility.getService(OTEServerFolder.class);
      File file = File.createTempFile("msgCaptureData", ".xyplot", serverFolder.getTestDataFolder());
      ElementSaveFile data = new ElementSaveFile();
      for(Checker check:checkers){
         ElementSave save = check.getElementSave();
         if(save != null){
            data.getElements().add(save);
         }
      }      
      OTEJsonSaveFile.writeSaveFile(file, data);
   }
   
   /**
    * Get the list of checkers.  This is mostly useful to run through and see if the checkers passed after calling {@link #check()}
    * in case you do not want to {@link #logToOutfile(ITestAccessor)} but are still interested on wither things passed or failed.
    * 
    * @return
    */
   public List<Checker> get(){
      return checkers;
   }

}
