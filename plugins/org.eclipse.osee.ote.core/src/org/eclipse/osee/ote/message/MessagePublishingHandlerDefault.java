package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.interfaces.IMessageManager;

public class MessagePublishingHandlerDefault implements MessagePublishingHandler {

   @Override
   public IOType getType() {
      return null;
   }

   @Override
   public void publish(IMessageManager messageManager, Message message) {
      messageManager.write(message, null);
   }

   @Override
   public void publish(IMessageManager messageManager, Message message, PublishInfo info) {
      messageManager.write(message, null);
   }

}
