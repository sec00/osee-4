package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.interfaces.IMessageManager;

public interface MessagePublishingHandler {
   IOType getType();
   void publish(IMessageManager messageManager, Message message);
   void publish(IMessageManager messageManager, Message message, PublishInfo info);
}
