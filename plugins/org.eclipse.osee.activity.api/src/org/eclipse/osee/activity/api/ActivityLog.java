/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.api;

import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ActivityTypeToken;

/**
 * @author Ryan D. Brooks
 */
public interface ActivityLog {
   Integer INITIAL_STATUS = 100;
   Integer COMPLETE_STATUS = 100;
   Integer ABNORMALLY_ENDED_STATUS = 500;

   ActivityEntry getEntry(ActivityEntryId entryId);

   Long createEntry(ActivityTypeToken type, Integer status, Object... messageArgs);

   Long createUpdateableEntry(ActivityTypeToken type, Object... messageArgs);

   Long createEntry(ActivityTypeToken type, Object... messageArgs);

   Long createEntry(ActivityTypeToken type, Long parentId, Integer status, Object... messageArgs);

   Long createEntry(Long accountId, Long clientId, ActivityTypeToken typeId, Long parentId, Integer status, String... messageArgs);

   Long createThrowableEntry(ActivityTypeToken type, Throwable throwable);

   boolean updateEntry(Long entryId, Integer status);

   /**
    * Set the status of log entry corresponding to entryId to 100% complete
    */
   void completeEntry(Long entryId);

   /**
    * Set the status of log entry corresponding to entryId to abnormally ended
    */
   void endEntryAbnormally(Long entryId);

   void endEntryAbnormally(Long entryId, Integer status);

   Long createActivityThread(ActivityTypeToken type, Long accountId, Long serverId, Long clientId, Object... messageArgs);

   Long createActivityThread(Long parentId, ActivityTypeToken type, Long accountId, Long serverId, Long clientId, Object... messageArgs);

   ActivityTypeToken getActivityType(ActivityTypeId typeId);

   boolean isEnabled();

   void setEnabled(boolean enabled);

   ActivityTypeToken createIfAbsent(ActivityTypeToken type);
}