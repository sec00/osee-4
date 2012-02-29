/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.console.admin.internal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class CommandDispatcher {

   private static final String CONSOLE_EXECUTOR_ID = "org.eclipse.osee.console.executor";

   private final Map<String, ConsoleCommand> registered = new ConcurrentHashMap<String, ConsoleCommand>();
   private final Map<String, Map<String, Future<?>>> futures = new ConcurrentHashMap<String, Map<String, Future<?>>>();
   private final Map<String, String> commandNameToId = new ConcurrentHashMap<String, String>();

   private final Log logger;
   private final ExecutorAdmin executorAdmin;

   public CommandDispatcher(Log logger, ExecutorAdmin executorAdmin) {
      this.logger = logger;
      this.executorAdmin = executorAdmin;
   }

   public Collection<ConsoleCommand> getRegistered() {
      return registered.values();
   }

   public void register(String id, ConsoleCommand command) {
      registered.put(id, command);
      commandNameToId.put(command.getName().toLowerCase(), id);
   }

   public void unregister(String id) {
      cancelAllTasksFor(id);

      ConsoleCommand command = registered.remove(id);
      if (command != null) {
         commandNameToId.remove(command.getName().toLowerCase());
      }
   }

   public void dispatch(Console console, ConsoleParameters params) throws Exception {
      String cmdId = getCommandId(params);
      ConsoleCommand command = getCommandById(cmdId);

      ConsoleAdminUtils.checkNotNull(command, "command", "Unable to find command for [%s]", cmdId);

      Callable<?> callable = command.createCallable(console, params);
      execute(cmdId, callable);
   }

   private <T> void execute(final String cmdId, Callable<T> callable) throws Exception {

      final String guid = GUID.create();
      Future<?> future = getExecutorAdmin().schedule(CONSOLE_EXECUTOR_ID, callable, new ExecutionCallback<T>() {

         @Override
         public void onCancelled() {
            removeFuture();
         }

         @Override
         public void onSuccess(T result) {
            removeFuture();
         }

         @Override
         public void onFailure(Throwable throwable) {
            removeFuture();
         }

         private void removeFuture() {
            Map<String, Future<?>> items = futures.get(cmdId);
            items.remove(guid);
            if (items.isEmpty()) {
               futures.remove(cmdId);
            }
         }
      });

      Map<String, Future<?>> items = futures.get(cmdId);
      if (items == null) {
         items = new ConcurrentHashMap<String, Future<?>>();
         futures.put(cmdId, items);
      }
      items.put(guid, future);
   }

   public void cancelAllTasksFor(String cmdId) {
      if (Strings.isValid(cmdId)) {
         Map<String, Future<?>> items = futures.get(cmdId);
         if (items != null) {
            for (Future<?> item : items.values()) {
               item.cancel(true);
            }
            if (items.isEmpty()) {
               futures.remove(cmdId);
            }
         }
      } else {
         getLogger().warn("Null command id received");
      }
   }

   private ConsoleCommand getCommandById(String commandId) throws Exception {
      ConsoleAdminUtils.checkNotNull(commandId, "commandId");
      return registered.get(commandId);
   }

   public ConsoleCommand getCommandByName(String commandName) throws Exception {
      String commandId = getCommandIdByName(commandName);
      return getCommandById(commandId);
   }

   private String getCommandIdByName(String commandName) {
      ConsoleAdminUtils.checkNotNullOrEmpty(commandName, "command name");
      return commandNameToId.get(commandName);
   }

   private String getCommandId(ConsoleParameters params) throws Exception {
      String commandName = params.getCommandName();
      return getCommandIdByName(commandName);
   }

   private ExecutorAdmin getExecutorAdmin() {
      return executorAdmin;
   }

   private Log getLogger() {
      return logger;
   }
}
