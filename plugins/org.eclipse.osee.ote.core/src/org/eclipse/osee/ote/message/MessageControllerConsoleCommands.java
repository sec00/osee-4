/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;
import org.osgi.framework.ServiceRegistration;

public class MessageControllerConsoleCommands {

   private ServiceRegistration<?> reg;

   private MessageController messageController;

   static final String[] functions = { "io", "ml", "msysl" };
   
   public MessageControllerConsoleCommands(MessageController messageController){
      this.messageController = messageController;
      Dictionary<String, Object> dict = new Hashtable<String, Object>();
      dict.put(CommandProcessor.COMMAND_SCOPE, "ote");
      dict.put(CommandProcessor.COMMAND_FUNCTION, functions);
      reg = ServiceUtility.getContext().registerService(MessageControllerConsoleCommands.class.getName(), this, dict);
   }
   
   @Descriptor ("print ote message io info")
   public void io(CommandSession session, String[] args) throws Exception
   {
      System.out.println("Available DataTypes");
      for(DataType type:messageController.getAvailableDataTypes()){
         System.out.printf("\t%s\n", type.toString());
      }
      System.out.println("Available MessagePublishingHandlers");
      for(Entry<IOType, MessagePublishingHandler> entry: messageController.getMessagePublishers().entrySet()){
         System.out.printf("\t%-20s  %-50s %s\n", entry.getKey().name(), entry.getValue().getClass(), entry.getValue().toString());
      }
      System.out.println("Available MessageDataWriters");
      for(Entry<IOType, CopyOnWriteNoIteratorList<MessageDataWriter>> entry: messageController.getDataWriters().entrySet()){
         MessageDataWriter[] writers = entry.getValue().get();
         for(int i = 0; i < writers.length; i++){
            System.out.printf("\t%-20s  %-50s %s\n", entry.getKey().name(), writers[i].getClass(), writers[i].toString());
         }
      }
      System.out.println("Available MessageDataReceivers");
      for(MessageDataReceiver entry: messageController.getMessageDataReceivers()){
         System.out.printf("\t%-20s  %-50s %s\n", entry.getDataType().name(), entry.getClass(),entry.toString());
      }
   }	
   
   @Descriptor("Print Message System Listener Info")
   public void msysl(){
      System.out.println("InstanceRequestListener:");
      for(IMessageCreationListener listener :messageController.getInstanceRequestListeners()){
         printListenerSummary(listener);
      }
      System.out.println("PreCreationListeners:");
      for(IMessageCreationListener listener :messageController.getPreCreationListeners()){
         printListenerSummary(listener);
      }
      System.out.println("PostCreationListeners:");
      for(IMessageCreationListener listener :messageController.getPostCreationListeners()){
         printListenerSummary(listener);
      }
   }
   
   private void printListenerSummary(IMessageCreationListener listener) {
      System.out.printf("\t%-80s %s\n", listener.getClass().getName(), listener.toString());
   }

   @Descriptor("list messages that are currently instantiated in the environment")
   public void ml(
         @Descriptor("show verbose info (listeners)") @Parameter(names = { "-v", "--verbose" }, presentValue = "true", absentValue = "false") boolean verbose,
         @Descriptor("show readers") @Parameter(names = { "-r", "--readers" }, presentValue = "true", absentValue = "false") boolean showReaders,
         @Descriptor("show writers") @Parameter(names = { "-w", "--writers" }, presentValue = "true", absentValue = "false") boolean showWriters,
         @Descriptor("regex name match (.* is the java regex wildcard)") @Parameter(names = { "-m" }, absentValue = "") String pattern
         ) {
      
         Collection<Message> messages = null;
         if(showReaders == showWriters){
            messages =  messageController.getAllMessages();
         } else if(showReaders){
            messages = messageController.getAllReaders();
         } else {
            messages = messageController.getAllWriters();
         }
         Collections.sort((List<Message>)messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
               if(o1 == null && o2 == null){
                  return 0;
               } else if (o1 == null){
                  return -1;
               } else if (o2 == null){
                  return 1;
               } else {
                  return o1.getName().compareTo(o2.getName());
               }
            }
         });
         
         
         Matcher matcher = Pattern.compile(pattern).matcher("");
         int count = 0;
         for(Message msg:messages){
            count++;
            boolean printMessage = true;
            if(pattern.length() > 0){
               printMessage = matcher.reset(msg.getMessageName()).matches();
            } 
            if(printMessage){
               printMessageInfo(msg, count);
               if(verbose){
                  printListenerInfo(msg);
               }
            }
         }
   }

   private void printListenerInfo(Message msg) {
      System.out.println("\tIOSEEMessageListener:");
//      for(IOSEEMessageListener listener: msg.getListener().getRegisteredFastListeners()){
//         print(listener);
//      }
//      for(IOSEEMessageListener listener: msg.getListener().getRegisteredSlowListeners()){
//         print(listener);
//      }
      
//      if(!msg.getSchedulingChangeListeners().isEmpty()){
//         System.out.println("\tIMessageScheduleChangeListener:");   
//         for(IMessageScheduleChangeListener listener:msg.getSchedulingChangeListeners()){
//            print(listener);
//         }
//      }
//      if(!msg.getPreMemSourceChangeListeners().isEmpty()){
//         System.out.println("\tPre IMemSourceChangeListener:");
//         for(IMemSourceChangeListener listener:msg.getPreMemSourceChangeListeners()){
//            print(listener);
//         }
//      }
//      if(!msg.getPostMemSourceChangeListeners().isEmpty()){
//         System.out.println("\tPost IMemSourceChangeListener:");
//         for(IMemSourceChangeListener listener:msg.getPostMemSourceChangeListeners()){
//            print(listener);
//         }
//      }
//      if(!msg.getPreMessageDisposeListeners().isEmpty()){
//         System.out.println("\tPre IMemSourceChangeListener:");
//         for(IMessageDisposeListener listener:msg.getPreMessageDisposeListeners()){
//            print(listener);
//         }
//      }
//      if(!msg.getPostMemSourceChangeListeners().isEmpty()){
//         System.out.println("\tPost IMessageDisposeListener:");
//         for(IMessageDisposeListener listener:msg.getPostMessageDisposeListeners()){
//            print(listener);
//         }
//      }
   }
   
   private void print(Object listener){
      System.out.printf("\t\t%-80s [%s]\n", listener.getClass().getName(), listener.toString());
   }

   @SuppressWarnings({ "deprecation" })
   private void printMessageInfo(Message msg, int count) {
      System.out.printf("%03d  %s\n\t%s %s\n\tMem=%-8s Recv Cnt=%07d Snt Cnt=%07d Scheduled=%-5b rate=%07.3f off=%-5b, refcnt=%d\n",
            count, msg.getName(), msg.isWriter() ? "Writer" : "Reader", msg.getClass().getName(), msg.getMemType().toString(), (msg).getActivityCount(),
            (msg).getSentCount(), msg.isScheduled(), msg.getRate(), msg.isTurnedOff(),
            messageController.getReferenceCount(msg));
   }

   public void dispose() {
      reg.unregister();
   }
   
   
}
