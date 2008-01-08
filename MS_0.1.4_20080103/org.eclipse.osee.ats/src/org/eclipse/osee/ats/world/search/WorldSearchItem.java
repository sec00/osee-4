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
package org.eclipse.osee.ats.world.search;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldSearchItem {

   private final String name;
   protected static Set<Artifact> EMPTY_SET = new HashSet<Artifact>();
   protected boolean cancelled = false;
   protected ArtifactPersistenceManager apm = ArtifactPersistenceManager.getInstance();

   public WorldSearchItem(String name) {
      super();
      this.name = name;
      this.cancelled = false;
   }

   public String getName() {
      return name;
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    * 
    * @return selected name
    */
   public String getSelectedName() {
      return getName();
   }

   public abstract Collection<Artifact> performSearch() throws SQLException, IllegalArgumentException;

   /**
    * Perform search and return result set without loading in WorldView. This method can be used repeatedly.
    * 
    * @return artifacts resulting from search
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Collection<Artifact> performSearchGetResults() throws SQLException, IllegalArgumentException {
      return performSearchGetResults(false);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi) throws SQLException, IllegalArgumentException {
      if (performUi) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            public void run() {
               performUI();
            }
         }, true);

      }
      if (cancelled) return EMPTY_SET;
      return performSearch();
   }

   public void performUI() {
      cancelled = false;
   }

   public boolean isCancelled() {
      return cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

}
