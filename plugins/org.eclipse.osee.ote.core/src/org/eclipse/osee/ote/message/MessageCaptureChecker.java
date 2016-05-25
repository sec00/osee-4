package org.eclipse.osee.ote.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
      while(dataIterator.hasNext()){
         MessageCaptureDataStripe stripe = dataIterator.next();
         for(Checker check:checkers){
            check.check(stripe);
         }
      }
   }

   public void close() throws IOException {
      dataIterator.close();
   }
   
   public List<Checker> get(){
      return checkers;
   }

}
