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

package org.eclipse.osee.ats.actions.wizard;

import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

/**
 * @author Donald G. Dunne
 */
public class NewActionJob extends Job {
   private boolean saveIt = false;
   private String identifyStateDescription = null;
   private String title;
   private final String desc;
   private final ChangeType changeType;
   private final String priority;
   private final Date needByDate;
   private final boolean validationRequired;
   private ActionArtifact actionArt;
   private final Set<ActionableItemArtifact> actionableItems;
   private final NewActionWizard wizard;
   private final INewActionListener newActionListener;

   public NewActionJob(String title, String desc, ChangeType changeType, String priority, Date needByDate, boolean validationRequired, Set<ActionableItemArtifact> actionableItems, NewActionWizard wizard, INewActionListener newActionListener) {
      super("Creating New Action");
      this.title = title;
      this.desc = desc;
      this.changeType = changeType;
      this.priority = priority;
      this.needByDate = needByDate;
      this.validationRequired = validationRequired;
      this.actionableItems = actionableItems;
      this.wizard = wizard;
      this.newActionListener = newActionListener;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Create New Action");
         if ("tt".equals(title)) {
            title += " " + AtsUtil.getAtsDeveloperIncrementingNum();
         }
         actionArt =
            ActionManager.createAction(monitor, title, desc, changeType, priority, validationRequired, needByDate,
               actionableItems, new Date(), UserManager.getUser(), newActionListener, transaction);

         if (wizard != null) {
            wizard.notifyAtsWizardItemExtensions(actionArt, transaction);
         }

         monitor.subTask("Persisting");
         transaction.execute();

         // Because this is a job, it will automatically kill any popups that are created during.
         // Thus, if multiple teams were selected to create, don't popup on openAction or dialog
         // will exception out when it is killed at the end of this job.
         AtsUtil.openATSAction(actionArt, AtsOpenOption.OpenAll);
         OseeNotificationManager.getInstance().sendNotifications();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }

   public String getIdentifyStateDescription() {
      return identifyStateDescription;
   }

   public void setIdentifyStateDescription(String identifyStateDescription) {
      this.identifyStateDescription = identifyStateDescription;
   }

   public boolean isSaveIt() {
      return saveIt;
   }

   public void setSaveIt(boolean saveIt) {
      this.saveIt = saveIt;
   }

   public ActionArtifact getActionArt() {
      return actionArt;
   }

}
