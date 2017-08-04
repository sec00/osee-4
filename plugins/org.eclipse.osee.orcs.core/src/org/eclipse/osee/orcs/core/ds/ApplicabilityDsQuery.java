/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Ryan D. Brooks
 */
public interface ApplicabilityDsQuery {

   ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch);

   List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(Collection<? extends ArtifactId> artIds, BranchId branch);

   Set<ArtifactId> getExcludedArtifacts(BranchId branch, ArtifactId view);

}