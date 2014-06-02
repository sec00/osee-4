/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.app;

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.template.engine.AppendableRule;

/**
 * @author Ryan D. Brooks
 */
public class ParameterRule extends AppendableRule<Pair<CharSequence, CharSequence>> {

   public ParameterRule() {
      super("ParameterRule");
   }

   public void addVisibleParameter(String type, String name, String label, String value, String description, String dependency) {

   }

   public void addVisibleParameter(String type, String name, String label, String value, String description) {
      addVisibleParameter(type, name, label, value, description, null);
   }

   public void addVisibleParameter(String type, String name, String label, String value) {

   }

   public void addVisibleParameter(String type, String name, String label) {

   }

   public void addHiddenParameter(String type, String name, String value) {

   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {

   }
}