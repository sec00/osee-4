/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * Default sorter for artifacts. Sorts on descriptive name
 */
@SuppressWarnings("deprecation")
public class UserIdSorter extends ViewerSorter {

   private final Collection<? extends UserId> initialSel;
   private final Collection<? extends UserId> teamMembers;

   public UserIdSorter(Collection<? extends UserId> initialSel, Collection<? extends UserId> teamMembers) {
      this.initialSel = initialSel;
      this.teamMembers = teamMembers;
   }

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      User user1 = (User) e1;
      User user2 = (User) e2;
      try {
         if (UserManager.getUser().equals(user1)) {
            return -1;
         }
         if (UserManager.getUser().equals(user2)) {
            return 1;
         }
         if (initialSel != null) {
            if (initialSel.contains(user1) && initialSel.contains(user2)) {
               return getComparator().compare(user1.getName(), user2.getName());
            }
            if (initialSel.contains(user1)) {
               return -1;
            }
            if (initialSel.contains(user2)) {
               return 1;
            }
         }
         if (teamMembers != null) {
            if (teamMembers.contains(user1) && teamMembers.contains(user2)) {
               return getComparator().compare(user1.getName(), user2.getName());
            }
            if (teamMembers.contains(user1)) {
               return -1;
            }
            if (teamMembers.contains(user2)) {
               return 1;
            }
         }
         return getComparator().compare(user1.getName(), user2.getName());
      } catch (OseeCoreException ex) {
         return -1;
      }
   }

}