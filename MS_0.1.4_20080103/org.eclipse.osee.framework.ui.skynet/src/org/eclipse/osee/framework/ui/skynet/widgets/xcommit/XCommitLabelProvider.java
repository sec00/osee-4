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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XCommitLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final CommitXViewer treeViewer;

   public XCommitLabelProvider(CommitXViewer treeViewer) {
      super();
      this.treeViewer = treeViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      Branch branch = ((Branch) element);
      if (branch == null) return "";
      XViewerColumn xCol = treeViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         CommitColumn aCol = CommitColumn.getAtsXColumn(xCol);
         return getColumnText(element, columnIndex, branch, xCol, aCol);
      }
      return "";
   }

   /**
    * Provided as optimization of subclassed classes so provider doesn't have to retrieve the same information that has
    * already been retrieved
    * 
    * @param element
    * @param columnIndex
    * @param branch
    * @param xCol
    * @param aCol
    * @return column string
    */
   public String getColumnText(Object element, int columnIndex, Branch branch, XViewerColumn xCol, CommitColumn aCol) {
      if (!xCol.isShow()) return ""; // Since not shown, don't display
      if (aCol == CommitColumn.Status_Col)
         return "Hours";
      else if (aCol == CommitColumn.Status_Col)
         return "Status";
      else if (aCol == CommitColumn.Name_Col) return branch.getBranchName();
      return "Unhandled Column";
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public CommitXViewer getTreeViewer() {
      return treeViewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      Branch branch = (Branch) element;
      XViewerColumn xCol = treeViewer.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      CommitColumn dCol = CommitColumn.getAtsXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display
      if (dCol == CommitColumn.Name_Col) return SkynetGuiPlugin.getInstance().getImage("branch.gif");
      return null;
   }
}
