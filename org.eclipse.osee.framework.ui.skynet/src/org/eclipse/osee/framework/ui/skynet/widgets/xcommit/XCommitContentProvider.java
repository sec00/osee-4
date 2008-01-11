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
package org.eclipse.osee.framework.ui.skynet.widgets.xcommit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

public class XCommitContentProvider implements ITreeContentProvider {

   protected Collection<Branch> rootSet = new HashSet<Branch>();
   private final CommitXViewer xViewer;
   private static Object[] EMPTY_ARRAY = new Object[0];

   public XCommitContentProvider(CommitXViewer WorldXViewer) {
      super();
      this.xViewer = WorldXViewer;
   }

   public void add(final Branch item) {
      add(Arrays.asList(new Branch[] {item}));
   }

   public void add(final Collection<? extends Branch> items) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.addAll(items);
            xViewer.refresh();
         };
      });
   }

   public void set(final Collection<? extends Branch> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            clear();
            add(arts);
         };
      });
   }

   public void remove(final Artifact art) {
      remove(Arrays.asList(new Artifact[] {art}));
   }

   public void remove(final Collection<? extends Artifact> arts) {
      if (xViewer.getInput() == null) xViewer.setInput(rootSet);
      ArrayList<Branch> delItems = new ArrayList<Branch>();
      delItems.addAll(rootSet);
      for (Artifact art : arts) {
         for (Branch wai : rootSet)
            if (wai.equals(art)) delItems.add(wai);
      }
      removeItems(delItems);
   }

   public void removeItems(final Collection<? extends Branch> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.remove(arts);
            xViewer.refresh();
         };
      });
   }

   public void clear() {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.clear();
            xViewer.refresh();
         };
      });
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      if (parentElement instanceof Branch) {
         try {
            return ((Branch) parentElement).getChildBranches().toArray();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   @SuppressWarnings("unchecked")
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   /**
    * @return the rootSet
    */
   public Collection<Branch> getRootSet() {
      return rootSet;
   }

}
