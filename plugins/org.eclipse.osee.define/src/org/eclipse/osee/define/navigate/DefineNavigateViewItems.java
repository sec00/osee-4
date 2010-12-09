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
package org.eclipse.osee.define.navigate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemFolder;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.action.CompareTwoStringsAction;
import org.eclipse.osee.framework.ui.skynet.artifact.MassEditDirtyArtifactOperation;
import org.eclipse.osee.framework.ui.skynet.blam.BlamContributionManager;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class DefineNavigateViewItems implements XNavigateViewItems {

   public DefineNavigateViewItems() {
      super();
   }

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      try {
         BlamContributionManager.addBlamOperationsToNavigator(items);

         if (AccessControlManager.isOseeAdmin()) {
            XNavigateItem adminItems = new XNavigateItemFolder(null, "Admin");
            items.add(adminItems);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      addExtensionPointItems(items);
      items.add(new XNavigateItemAction(null, new CompareTwoStringsAction(), FrameworkImage.EDIT));
      items.add(new XNavigateItemOperation(null, FrameworkImage.GEAR, MassEditDirtyArtifactOperation.NAME,
         new MassEditDirtyArtifactOperation()));

      return items;
   }

   private void addExtensionPointItems(List<XNavigateItem> items) {
      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.define.DefineNavigateItem");
      if (point == null) {
         OseeLog.log(DefinePlugin.class, OseeLevel.SEVERE_POPUP, "Can't access DefineNavigateItem extension point");
         return;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("IDefineNavigateItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
            }
         }
         if (classname != null && bundleName != null) {
            Bundle bundle = Platform.getBundle(bundleName);
            try {
               Class<?> taskClass = bundle.loadClass(classname);
               Object obj = taskClass.newInstance();
               IDefineNavigateItem task = (IDefineNavigateItem) obj;
               items.addAll(task.getNavigateItems());
            } catch (Exception ex) {
               OseeLog.log(DefinePlugin.class, OseeLevel.SEVERE_POPUP, "Error loading DefineNavigateItem extension", ex);
            }
         }
      }
   }
}