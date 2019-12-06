/*******************************************************************************
* Copyright (c) 2019 Boeing.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Boeing - initial API and implementation
*******************************************************************************/
package org.eclipse.osee.ats.api.data;

import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProviderBase;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Ryan D. Brooks
 */
public final class AtsTypeTokenProvider extends OrcsTypeTokenProviderBase {
   private static final NamespaceToken ATS =
      NamespaceToken.valueOf(2, "ats", "Namespace for ats system and content management types");
   public static final OrcsTypeTokens ats = new OrcsTypeTokens(ATS);

   private static final NamespaceToken ATSDEMO =
      NamespaceToken.valueOf(10, "ats demo", "Namespace for ats demo system and content management types");
   public static final OrcsTypeTokens atsDemo = new OrcsTypeTokens(ATSDEMO);

   public AtsTypeTokenProvider() {
      super(ats, atsDemo);
      loadClasses(AtsArtifactTypes.AtsArtifact, AtsAttributeTypes.Actionable, AtsRelationTypes.Derive_To,
         AtsDemoOseeTypes.Action);
   }
}