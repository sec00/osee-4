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
package org.eclipse.osee.account.rest.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class SubscriptionGroupData implements Identifiable<String> {

   private String uuid;
   private String name;
   private long id;

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getGuid() {
      return uuid;
   }

   public void setGuid(String uuid) {
      this.uuid = uuid;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      boolean equal = false;
      if (obj instanceof Identity) {
         @SuppressWarnings("unchecked")
         Identity<String> identity = (Identity<String>) obj;
         if (getGuid() == identity.getGuid()) {
            equal = true;
         } else if (getGuid() != null) {
            equal = getGuid().equals(identity.getGuid());
         }
      }
      return equal;
   }

   @Override
   public String toString() {
      return String.valueOf(getGuid());
   }

}
