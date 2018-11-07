/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.List;
import org.eclipse.osee.ats.workflow.CollectorArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactMembersCache<T extends CollectorArtifact> {

   List<Artifact> getMembers(T artifact);

   void decache(T artifact);

   void invalidate();

   String getMemberOrder(T memberArt, Artifact member);

}