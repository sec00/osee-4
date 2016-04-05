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
package org.eclipse.osee.ote.message.condition;

import org.eclipse.osee.ote.message.elements.CharElement;

public class CharElementStringComparisonCondition extends AbstractCondition implements IDiscreteElementCondition<String> {

   private final CharElement element;
   private final String value;
   private final StringOperation operation;
   private String lastValue = null;

   public CharElementStringComparisonCondition(CharElement element, StringOperation operation, String value) {
      this.element = element;
      this.operation = operation;
      this.value = value;
   }

   @Override
   public String getLastCheckValue() {
      return lastValue;
   }

   @Override
   public boolean check() {
      lastValue = element.getString(null, value.length());
      return operation.evaluate(lastValue, value);
   }

   public String getValue() {
      return value;
   }

   public StringOperation getOperation() {
      return operation;
   }

}
