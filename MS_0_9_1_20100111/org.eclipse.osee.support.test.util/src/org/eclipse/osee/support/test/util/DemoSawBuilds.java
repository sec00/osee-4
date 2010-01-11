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
package org.eclipse.osee.support.test.util;

import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author Donald G. Dunne
 */
public enum DemoSawBuilds implements IOseeBranch {
   SAW_Bld_1("AyH_f2sSKy3l07fIvAAA"),
   SAW_Bld_2("AyH_f2sSKy3l07fIvBBB"),
   SAW_Bld_3("AyH_f2sSKy3l07fIvCCC");

   private final String guid;

   private DemoSawBuilds(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public String getName() {
      return name();
   }
}
