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
package org.eclipse.osee.orcs.core.ds;

/**
 * @author Roberto E. Escobar
 */
public class Criteria {

   /**
    * subclasses must return true if the criteria is valid, return false if the criteria is invalid but should be
    * gracefully ignored; and throw an exception if invalid and should not be ignored
    */
   public boolean checkValid(Options options) {
      return true;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName();
   }
}