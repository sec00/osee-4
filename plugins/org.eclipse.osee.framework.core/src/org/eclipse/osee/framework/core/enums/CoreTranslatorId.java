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
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.translation.ITranslatorId;

/**
 * @author Megumi Telles
 * @author Roberto E. Escobar
 */
public enum CoreTranslatorId implements ITranslatorId {
   OSEE_DATASTORE_INIT_REQUEST,

   OSEE_IMPORT_MODEL_REQUEST,
   OSEE_IMPORT_MODEL_RESPONSE,

   TABLE_DATA;

   @Override
   public String getKey() {
      return this.name();
   }
}
