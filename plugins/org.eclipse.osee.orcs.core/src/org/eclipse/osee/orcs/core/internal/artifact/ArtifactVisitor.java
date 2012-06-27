/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.artifact;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactVisitor {

   void visit(ArtifactImpl artifact) throws OseeCoreException;

   void visit(Attribute<?> attribute) throws OseeCoreException;

   //   void visit(RelationData data) throws OseeCoreException;

}