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
package org.eclipse.osee.framework.core.server;

import java.util.List;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidSessionException;

/**
 * @author Roberto E. Escobar
 */
public interface ISessionManager {

   public OseeSessionGrant createSession(OseeCredential credential) throws OseeCoreException;

   public void releaseSession(String sessionId);

   public void updateSessionActivity(String sessionId, String interactionName) throws OseeInvalidSessionException;

   public SessionData getSessionById(String sessionId);

   public List<SessionData> getSessionByClientAddress(String clientAddress);

   public List<SessionData> getSessionsByUserId(String userId, boolean includeNonServerManagedSessions) throws OseeCoreException;

   public List<SessionData> getAllSessions(boolean includeNonServerManagedSessions) throws OseeDataStoreException;

   public void releaseSessionImmediate(String... sessionId) throws OseeCoreException;

   public boolean isAlive(OseeSession oseeSession) throws OseeCoreException;

   public void shutdown();
}
