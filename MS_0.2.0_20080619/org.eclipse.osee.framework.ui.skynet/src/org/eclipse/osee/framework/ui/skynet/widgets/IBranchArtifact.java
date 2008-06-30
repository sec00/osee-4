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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;

/**
 * @author Donald G. Dunne
 */
public interface IBranchArtifact {
   public Branch getWorkingBranch() throws IllegalStateException, SQLException, ArtifactDoesNotExist, MultipleArtifactsExist, MultipleBranchesExist;

   public Artifact getArtifact();
}
