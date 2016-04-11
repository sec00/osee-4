package org.eclipse.osee.ote.message;

public interface MessageRemoveHandler {

   /**
    * Do any extra work that needs to be done on the removal of a message.  Gives you the ability to return false which will cause the buffer to not get thrown out.
    * This is usefull if you want to keep the contents of a reader/writer buffer the same even though it is no longer referenced in the environment.
    * 
    * @param msg
    * @return true == continue with the removal, false == do not remove the message from the message cache
    */
   boolean process(Message msg);

}
