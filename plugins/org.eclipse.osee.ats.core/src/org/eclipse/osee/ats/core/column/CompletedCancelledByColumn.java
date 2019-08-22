/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CompletedCancelledByColumn extends AbstractServicesColumn {

   public CompletedCancelledByColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         IAtsUser user = getCompletedCancelledBy(atsObject, atsApi);
         if (user != null) {
            return user.getName();
         }
      }
      return null;
   }

   public static IAtsUser getCompletedCancelledBy(Object obj, AtsApi atsApi) {
      String userId = null;
      if (obj instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) obj;
         AttributeTypeToken<String> type = null;
         if (workItem.isCompleted()) {
            type = AtsAttributeTypes.CompletedBy;
         } else if (workItem.isCancelled()) {
            type = AtsAttributeTypes.CancelledBy;
         }
         if (type != null) {
            userId = atsApi.getAttributeResolver().getSoleAttributeValue((IAtsWorkItem) obj, type, null);
            if (Strings.isValid(userId)) {
               return atsApi.getUserService().getUserById(userId);
            }
         }
      }
      return null;
   }
}
