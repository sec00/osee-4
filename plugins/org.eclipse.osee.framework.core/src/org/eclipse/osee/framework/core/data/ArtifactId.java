/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Megumi Telles
 */
public interface ArtifactId extends Identifiable<String>, HasBranch {

   public Long getUuid();

   default String toStringWithId() {
      return String.format("[%s][%s]", getName(), getUuid());
   }
}
