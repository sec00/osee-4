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
package org.eclipse.osee.framework.skynet.core.event;

import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.artifact.IEventBasicGuidArtifact;

/**
 * @author Donald G. Dunne
 */
public abstract class ArtifactTransactionModifiedEvent {

   public abstract Set<? extends IEventBasicGuidArtifact> getArtifactChanges() throws OseeCoreException;

}
