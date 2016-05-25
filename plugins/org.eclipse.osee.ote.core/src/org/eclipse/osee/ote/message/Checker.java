package org.eclipse.osee.ote.message;

public interface Checker {

   void check(MessageCaptureDataStripe stripe);

   
   
   boolean passed();

}
