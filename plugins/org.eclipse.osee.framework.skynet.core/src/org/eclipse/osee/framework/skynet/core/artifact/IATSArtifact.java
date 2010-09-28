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
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * Class exists to mark ATS Artifacts so the EventManager in core can report the artifact type
 * 
 * @author Donald G. Dunne
 */
public interface IATSArtifact {

   public Artifact getParentAtsArtifact() throws OseeCoreException;

   public String getGuid();

   public String getName();

   public String getArtifactTypeName();

   public boolean isDeleted();

}
