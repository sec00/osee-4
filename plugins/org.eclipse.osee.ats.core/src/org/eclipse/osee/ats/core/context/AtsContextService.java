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
package org.eclipse.osee.ats.core.context;

import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.context.IAtsContextService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.logging.OseeLog;

public class AtsContextService implements IAtsContextService {

   AtsContext atsContext = null;
   private final IAttributeResolver attrResolver;

   public AtsContextService(IAttributeResolver attrResolver) {
      this.attrResolver = attrResolver;
   }

   private AtsContext getAtsContext() {
      if (atsContext == null) {
         AtsContextStore store = new AtsContextStore(attrResolver);
         try {
            atsContext = store.read();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return atsContext;
   }

   @Override
   public void addContext(String uuid, String name) {
      getAtsContext().addContext(uuid, name);
   }

   @Override
   public Set<String> getContextUuids() {
      return getAtsContext().getContextUuids();
   }

   @Override
   public String getContextName(String uuid) {
      return getAtsContext().getContextName(uuid);
   }

   @Override
   public String getUserContextUuid(String userId) {
      return getAtsContext().getUserContextUuid(userId);
   }

   @Override
   public void setUserContextUuid(String userId, String uuid) {
      getAtsContext().setUserContextUuid(userId, uuid);
      if (userId.equals(AtsCore.getUserService().getCurrentUser().getUserId())) {
         Long uuidL = Long.valueOf(uuid);
         IOseeBranch newBranch = AtsCore.getBranchService().getBranch(uuidL);
         if (newBranch != null) {
            //            AtsUtilCore.setBranch(uuidL);
         }
      }
   }

   @Override
   public Set<String> getUserIds() {
      return getAtsContext().getUserIds();
   }

   @Override
   public void writeToStore() throws Exception {
      AtsContextStore store = new AtsContextStore(attrResolver);
      try {
         store.write(atsContext);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void read() throws Exception {
      atsContext = null;
      getAtsContext();
   }

}
