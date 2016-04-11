package org.eclipse.osee.ote.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

class MessageReaderHandler extends AbstractAnnotationHandler<MessageReader> {
   private IMessageRequestor messageRequestor;

   public MessageReaderHandler(IMessageRequestor req) {
      this.messageRequestor = req;
   }
   @Override
   public void process(MessageReader annotation, Object object, Field field) throws Exception {
      Message message = messageRequestor.getMessageReader(field.getType().getName());
      injectToFields(object, field, message);
   }

   @Override
   public void process(MessageReader annotation, Object object) {
      
   }

   @Override
   public void process(MessageReader annotation, Object object, Method method) {
      
   }

}
