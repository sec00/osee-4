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
package org.eclipse.osee.ats.api.demo.enums.token;

import org.eclipse.osee.ats.api.demo.enums.token.CodeReqDocAttributeType.CodeReqDocEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class CodeReqDocAttributeType extends AttributeTypeEnum<CodeReqDocEnum> {

   // @formatter:off
	public final CodeReqDocEnum Unknown = new CodeReqDocEnum(0, "Unknown");
	public final CodeReqDocEnum SubDD = new CodeReqDocEnum(1, "SubDD");
	public final CodeReqDocEnum CSID = new CodeReqDocEnum(2, "CSID");
	public final CodeReqDocEnum SRS = new CodeReqDocEnum(3, "SRS");
	public final CodeReqDocEnum Other = new CodeReqDocEnum(4, "Other");
	// @formatter:on

   public CodeReqDocAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1740569308658341L, namespace, "Code Req Doc", mediaType, "", taggerType);
   }

   public class CodeReqDocEnum extends EnumToken {
      public CodeReqDocEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
