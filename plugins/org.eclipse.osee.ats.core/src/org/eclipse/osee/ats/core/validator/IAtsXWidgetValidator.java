/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * Provider to validate XWidget/IAttributeWidget entry against Artifact store model without use of XWidget UI
 * 
 * @author Donald G. Dunne
 */
public interface IAtsXWidgetValidator {

   public WidgetResult validateTransition(IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) throws OseeCoreException;

}
