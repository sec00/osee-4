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
package org.eclipse.osee.framework.core.client;

import java.net.InetAddress;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseCredentialProvider implements ICredentialProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.client.ICredentialProvider#getCredential()
    */
   @Override
   public OseeCredential getCredential() throws OseeCoreException {
      OseeCredential credential = new OseeCredential();
      credential.setUserId("");
      credential.setDomain("");
      credential.setPassword("");
      credential.setAuthenticationProtocol("");
      HttpUrlBuilder.getInstance().getSkynetHttpLocalServerPrefix();
      credential.setClientAddress(HttpServer.getLocalServerAddress(), HttpServer.getDefaultServicePort());
      credential.setClientVersion(OseeCodeVersion.getVersion());
      try {
         credential.setClientMachineName(InetAddress.getLocalHost().getHostName());
      } catch (Exception ex) {
         credential.setClientMachineName(ex.getLocalizedMessage());
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
      }
      return credential;
   }
}
