/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.operation;

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class CommandInterpreterLogger extends OperationLogger {
   private final CommandInterpreter ci;

   public CommandInterpreterLogger(CommandInterpreter ci) {
      this.ci = ci;
   }

   @Override
   public void log(String... row) {
      for (String cell : row) {
         ci.print(cell);
         ci.print("   ");
      }
      ci.println();
   }

   @Override
   public void log(Throwable th) {
      ci.printStackTrace(th);
   }
}