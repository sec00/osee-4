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
package org.eclipse.osee.define.api;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;

/**
 * @author Ryan D. Brooks
 */
public interface DataRightsOperations {

   DataRightResult getDataRights(List<ArtifactId> artifacts, BranchId branch);

   DataRightResult getDataRights(List<ArtifactId> artifacts, BranchId branch, DataRightsClassification overrideClassification);

}
