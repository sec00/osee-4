/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.types;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
public interface IArtifact {

   int getArtId();

   String getGuid();

   String getName();

   Branch getBranch();

   ArtifactType getArtifactType();

   Artifact getFullArtifact() throws OseeCoreException;

   List<? extends IArtifact> getRelatedArtifacts(RelationType relationType) throws OseeCoreException;
}
