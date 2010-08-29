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
package org.eclipse.osee.framework.core.exception;

/**
 * @author Roberto E. Escobar
 */
public class OseeInvalidSessionException extends OseeCoreException {

   private static final long serialVersionUID = 5540912279421352009L;

   public OseeInvalidSessionException(String message) {
      super(message);
   }

   public OseeInvalidSessionException(String message, Throwable cause) {
      super(message, cause);
   }

   public OseeInvalidSessionException(Throwable cause) {
      super(cause);
   }
}
