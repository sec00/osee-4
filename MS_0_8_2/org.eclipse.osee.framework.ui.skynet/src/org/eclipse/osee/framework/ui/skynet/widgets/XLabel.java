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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Simply shows the label name and nothing else. No storage or value associated with this widget.
 * 
 * @author Donald G. Dunne
 */
public class XLabel extends XWidget {

   private final String showString;

   public XLabel(String displayLabel) {
      this(displayLabel, displayLabel);
   }

   public XLabel(String displayLabel, String showString) {
      super(displayLabel, "");
      this.showString = showString;
   }

   /**
    * Create Data Widgets. Widgets Created: Data: "--select--" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2 the string "--select--" will be added to the sent in dataStrings array
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) horizontalSpan = 2;
      // Create Data Widgets
      if (!getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         labelWidget.setText(showString);
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }
   }

   @Override
   public void setFocus() {
   }

   @Override
   public void dispose() {
   }

   @Override
   public Control getControl() {
      return labelWidget;
   }

   @Override
   public Object getData() {
      return null;
   }

   @Override
   public String getReportData() {
      return null;
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public IStatus isValid() {
      return Status.OK_STATUS;
   }

   @Override
   public void refresh() {
   }

   @Override
   public void setXmlData(String str) {
   }

   @Override
   public String toHTML(String labelFont) {
      return "";
   }

}