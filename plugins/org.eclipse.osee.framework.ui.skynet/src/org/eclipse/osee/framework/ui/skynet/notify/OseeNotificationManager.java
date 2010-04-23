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
package org.eclipse.osee.framework.ui.skynet.notify;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * Stores notification events generated by the framework or applications. Currently, send happens upon call to
 * sendNotifications(). Eventually, a timer will kick the send event at certain intervals. This mechanism allows for
 * notifications to be collected for a certain period of time and rolled into a single notification. This will
 * eventually also support other types of notifications such as popups and allow the user to configure which events are
 * sent and how.
 * 
 * @author Donald G. Dunne
 */
public class OseeNotificationManager implements INotificationManager {

   private boolean emailEnabled = true;
   private static OseeNotificationManager instance = new OseeNotificationManager();
   private List<OseeNotificationEvent> notificationEvents = new ArrayList<OseeNotificationEvent>();

   private OseeNotificationManager() {
      instance = this;
   }

   public void addNotificationEvent(OseeNotificationEvent notificationEvent) {
      notificationEvents.add(notificationEvent);
   }

   public void clear() {
      notificationEvents.clear();
   }

   public void sendNotifications() throws OseeCoreException {
      if (!emailEnabled) {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Osee Notification Disabled");
         return;
      }
      List<OseeNotificationEvent> sendEvents = new ArrayList<OseeNotificationEvent>();
      sendEvents.addAll(notificationEvents);
      notificationEvents.clear();
      OseeNotifyUsersJob job = new OseeNotifyUsersJob(sendEvents);
      job.setPriority(Job.SHORT);
      job.schedule();
   }

   public boolean isEmailEnabled() {
      return emailEnabled;
   }

   public void setEmailEnabled(boolean emailEnabled) {
      this.emailEnabled = emailEnabled;
   }

   public List<OseeNotificationEvent> getNotificationEvents() {
      return notificationEvents;
   }

   public static OseeNotificationManager getInstance() {
      return instance;
   }
}
