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
package org.eclipse.osee.ote.client.msg.core.internal;

import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;

/**
 * @author Ken J. Aguilar
 */
public class MessageDatabase extends AbstractMessageDataBase {

   public MessageDatabase(IMsgToolServiceClient service) {
      super(service);
   }

}
