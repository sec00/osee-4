package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

public interface MessageSignalMapping {

   void map(IMessageRequestor requestor, Message message);
}
