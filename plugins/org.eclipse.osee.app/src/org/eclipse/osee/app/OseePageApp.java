package org.eclipse.osee.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.template.engine.AppendableRule;

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

/**
 * @author Ryan D. Brooks
 */
public final class OseePageApp extends AppendableRule<CharSequence> {
   private final List<PageAppParameter> parameters = new ArrayList<PageAppParameter>();

   public OseePageApp() {
   }

   public void addParameter(PageAppParameter parameter) {
      parameters.add(parameter);
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      for (PageAppParameter parameter : parameters) {
         applyParameter(appendable, parameter);
      }

   }

   private void applyParameter(Appendable appendable, PageAppParameter parameter) {

   }
}