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
package org.eclipse.osee.app.internal;

import java.io.IOException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.template.engine.AppendableRule;

/**
 * @author Ryan D. Brooks
 */
public final class AppListRule extends AppendableRule<CharSequence> {
   private final Iterable<ArtifactReadable> apps;

   public AppListRule(Iterable<ArtifactReadable> apps) {
      super("AppList");
      this.apps = apps;
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      for (ArtifactReadable app : apps) {
         appendable.append("<a href=\"app/");
         appendable.append(app.getLocalId() + "\">");
         appendable.append(app.getName());
         appendable.append("</a><br />\n");
      }
   }
}