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
package org.eclipse.osee.ats.ide.workflow.review;

import java.util.List;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsReviewHook;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public final class ReviewProviders {

   private static List<IAtsReviewHook> reviewProvider;

   private ReviewProviders() {
      // private constructor
   }

   /*
    * due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   public synchronized static List<IAtsReviewHook> getAtsReviewProviders() {
      if (reviewProvider == null) {

         ExtensionDefinedObjects<IAtsReviewHook> objects = new ExtensionDefinedObjects<>(
            Activator.PLUGIN_ID + ".AtsReviewProvider", "AtsReviewProvider", "classname", true);
         reviewProvider = objects.getObjects();

      }
      return reviewProvider;
   }

}
