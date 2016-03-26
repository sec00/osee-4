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
package org.eclipse.osee.ote.message.condition;

import org.eclipse.osee.ote.message.Message;

public class MessageTransmissionCountCondition extends AbstractCondition {

   private int max = 0;
   private Message message;

   public MessageTransmissionCountCondition(Message message, int max) {
      this.message = message;
      this.max = max;
   }
   
   public MessageTransmissionCountCondition(Message message) {
      this.message = message;
   }
   
   public void setTrasmitCount(int count){
      this.max = count;
   }

   @Override
   public boolean check() {
      return message.getActivityCount() >= max;
   }

   public int getMaxTransmitCount() {
      return max;
   }

}
