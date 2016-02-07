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
package org.eclipse.osee.ote.message.interfaces;

import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.message.Message;

/**
 * @author Ken J. Aguilar
 */
public interface IMessageRequestor {
   
   /**
    * This will get a shared instance of a message reader.  It will be hooked up to the message receiving infrastructure in the environment.
    * 
    * @param type
    * @return
    * @throws TestException
    */
   <CLASSTYPE extends Message> CLASSTYPE getMessageReader(Class<CLASSTYPE> type) throws TestException;
   
   Message getMessageReader(String type) throws TestException;
   
   /**
    * This will get a shared instance of a message writer.
    * 
    * @param type
    * @return
    * @throws TestException
    */
   <CLASSTYPE extends Message> CLASSTYPE getMessageWriter(Class<CLASSTYPE> type) throws TestException;
   
   /**
    * This will get a new instance of a message writer, it will not be shared by any other writers in the running environment.
    * 
    * @param type
    * @return
    * @throws TestException
    */
   <CLASSTYPE extends Message> CLASSTYPE createMessageWriter(Class<CLASSTYPE> type) throws TestException;

   Message getMessageWriter(String type) throws TestException;
   
   String getName();

   void remove(Message message) throws TestException;

   void dispose();
}
