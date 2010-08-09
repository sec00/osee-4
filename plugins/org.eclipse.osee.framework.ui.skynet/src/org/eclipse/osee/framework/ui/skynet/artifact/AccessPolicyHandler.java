/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Jeff C. Phillips
 */
public class AccessPolicyHandler {
   private final IBasicArtifact<?> user;
   private final IAccessControlService accessControlService;

   public AccessPolicyHandler(IBasicArtifact<?> user, IAccessControlService accessControlService) {
      this.user = user;
      this.accessControlService = accessControlService;
   }

   /**
    * @param artifacts
    * @param attributeType
    * @param permission
    * @param level - A level of OseeLevel.SEVERE_POPUP will cause an error dialog to be displayed to the user. All
    * others will write to the log.
    * @return PermissionStatus
    * @throws OseeCoreException
    */
   public PermissionStatus hasAttributeTypePermission(Collection<? extends IBasicArtifact<?>> artifacts, IAttributeType attributeType, PermissionEnum permission, Level level) throws OseeCoreException {
      AccessDataQuery query = accessControlService.getAccessData(user, artifacts);
      PermissionStatus permissionStatus = new PermissionStatus();

      if (artifacts != null) {
         for (IBasicArtifact<?> artifact : artifacts) {
            query.attributeTypeMatches(PermissionEnum.WRITE, artifact, attributeType, permissionStatus);

            if (!permissionStatus.matched()) {
               OseeLog.log(
                  SkynetGuiPlugin.class,
                  level,
                  "No Permission Error: \nArtifacts: " + Collections.toString(artifacts, ",") + " does not have permissions becuase: " + permissionStatus.getReason());
               break;
            }
         }
      }
      return permissionStatus;
   }

   public PermissionStatus hasAttributeTypePermission(Collection<? extends IBasicArtifact<?>> artifacts, IAttributeType attributeType, PermissionEnum permission, boolean displayMessage) throws OseeCoreException {
      return new PermissionStatus();
   }
}
