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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.WholeDocumentRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PreviewWholeWordHandler extends AbstractEditorHandler {

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         try {
            WholeDocumentRenderer renderer = new WholeDocumentRenderer();
            renderer.preview(artifacts);
            dispose();

         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWholeWordHandler.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }

   /**
    * A subclass may override this method if they would like options to be set on the renderer
    * 
    * @return
    */
   protected VariableMap getOptions() throws OseeArgumentException {
      return null;
   }
}
