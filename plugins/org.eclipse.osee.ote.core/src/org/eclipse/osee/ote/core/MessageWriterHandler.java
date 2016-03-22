package org.eclipse.osee.ote.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

public class MessageWriterHandler extends AbstractAnnotationHandler<MessageWriter> {

   private IMessageRequestor messageRequestor;

   public MessageWriterHandler(IMessageRequestor req) {
      this.messageRequestor = req;
   }

   @Override
   public void process(MessageWriter annotation, Object object, Field field) throws Exception {
      Message message = messageRequestor.getMessageWriter(field.getType().getName());
      injectToFields(object, field, message);
   }

   @Override
   public void process(Annotation annotation, Object object) {
      
   }

   @Override
   public void process(Annotation annotation, Object object, Method method) {
      
   }
   
}
