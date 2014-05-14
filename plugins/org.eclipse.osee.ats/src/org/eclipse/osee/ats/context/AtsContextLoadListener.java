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
package org.eclipse.osee.ats.context;

import org.eclipse.osee.ats.api.context.AtsContextUtil;
import org.eclipse.osee.ats.api.context.IAtsContextLoadListener;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;

/**
 * @author Donald G. Dunne
 */
public class AtsContextLoadListener implements IAtsContextLoadListener {

   @Override
   public void switchToContext(String contextUuid, String contextName) {
      // decache ats objects
      System.setProperty(AtsContextUtil.ATS_SYSTEM_CURRENT_CONTEXT_UUID, contextUuid);
   }

   @Override
   public void createNewContextBranch() {
      // create baseline branch
      // copy over user artifacts
      // create default work definitions
      // create default configuration
      // loadContext(branch.getUuid(), contextName);
   }

   @Override
   public void store() throws Exception {
      AtsCore.getContextService().writeToStore();
      OseeSystemArtifacts.getGlobalPreferenceArtifact().persist("Persiste AtsContext config");
   }

   @Override
   public void storeAsDefault(String uuid) throws Exception {
      AtsCore.getContextService().read();
      AtsCore.getContextService().setUserContextUuid(
         AtsClientService.get().getUserAdmin().getCurrentUser().getUserId(), uuid);
      AtsCore.getContextService().writeToStore();
      OseeSystemArtifacts.getGlobalPreferenceArtifact().persist("Persist AtsContext user default");
   }
}
