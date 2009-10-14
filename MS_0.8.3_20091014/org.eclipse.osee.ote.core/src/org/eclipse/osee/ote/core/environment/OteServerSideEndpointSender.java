/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.environment;

import java.util.Properties;
import org.eclipse.osee.framework.messaging.EndpointSend;
import org.eclipse.osee.framework.messaging.ExceptionHandler;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.id.ProtocolId;
import org.eclipse.osee.framework.messaging.id.StringName;
import org.eclipse.osee.framework.messaging.id.StringNamespace;
import org.eclipse.osee.framework.messaging.id.StringProtocolId;
import org.eclipse.osee.ote.core.IUserSession;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteServerSideEndpointSender implements EndpointSend {

   public static final ProtocolId OTE_SERVER_SIDE_SEND_PROTOCOL = new StringProtocolId(new StringNamespace("org.eclipse.osee.ote.core.environment"), new StringName("OteServerSideEndpointSender"));
   
   private TestEnvironment testEnvironment;

   public OteServerSideEndpointSender(TestEnvironment testEnvironment) {
      this.testEnvironment = testEnvironment;
   }

   public void send(Message message, ExceptionHandler exceptionHandler) {
      try{
         for(IUserSession session : testEnvironment.getUserSessions()){
            session.sendMessageToClient(message);
         }
      } catch(Throwable th){
         exceptionHandler.handleException(th);
      }
   }

   public void start(Properties properties) {
   }

}
