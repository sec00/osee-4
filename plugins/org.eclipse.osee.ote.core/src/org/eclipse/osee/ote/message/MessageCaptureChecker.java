package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.io.OTEServerFolder;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.save.ElementSave;
import org.eclipse.osee.ote.message.save.ElementSaveFile;
import org.eclipse.osee.ote.message.save.OTEJsonSaveFile;

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
   
   public void logToOutfile(ITestAccessor accessor){
      for(Checker check:checkers){
         check.logToOutfile(accessor);
      }
   }
   
   public void logToOutfile(ITestAccessor accessor, CheckGroup checkGroup){
      for(Checker check:checkers){
         check.logToOutfile(accessor, checkGroup);
      }
   }
   
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
   
   public List<Checker> get(){
      return checkers;
   }

}
