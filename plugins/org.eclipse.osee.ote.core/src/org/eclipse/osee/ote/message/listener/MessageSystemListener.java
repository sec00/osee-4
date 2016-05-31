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
package org.eclipse.osee.ote.message.listener;

import java.util.List;

import org.eclipse.osee.ote.message.Message;

/**
 * Everything done in this class should be done directly from the Message.  If you are using waitForData it should be switched to the ICondition API.
 * 
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
@Deprecated
public class MessageSystemListener {

   private Message message;

   public MessageSystemListener(Message message){
      this.message = message;
   }
   
   /**
    * Use {@link Message#addListener(IOSEEMessageListener)}
    * 
    * @param listener
    */
   @Deprecated
   public void addListener(IOSEEMessageListener listener){
      message.addListener(listener);
   }
   
   /**
    * Use {@link Message#removeListener(IOSEEMessageListener)}
    * 
    * @param listener
    */
   @Deprecated
   public void removeListener(IOSEEMessageListener listener){
      message.removeListener(listener);
   }
   
   /**
    * Direct access to the list of listenrs is going away.
    * 
    * @return
    */
   @Deprecated
   public List<IOSEEMessageListener> getRegisteredFastListeners(){
      return message.getListeners();
   }
   
   public boolean containsListener(IOSEEMessageListener listener){
      return message.containsListener(listener);
   }
   
}
