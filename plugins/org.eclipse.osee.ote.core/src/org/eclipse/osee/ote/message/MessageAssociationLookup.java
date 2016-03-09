package org.eclipse.osee.ote.message;

import java.util.List;
import java.util.Map;

import org.eclipse.osee.ote.message.enums.DataType;

public interface MessageAssociationLookup {
   
   public Map<DataType, Class<? extends Message>[]> getAssociatedMessages(Class<Message> messageClass);

   List<String> lookupAssociatedMessages(String className);
}
