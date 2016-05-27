package org.eclipse.osee.ote.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

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
   
   public List<Checker> get(){
      return checkers;
   }

}
