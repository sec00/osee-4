/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * StyledText snippet: embed controls For a list of all SWT example snippets see http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.2
 */
public class SnippetHyperlinkExample {

   static StyledText styledText;
   static String text =
         "This snippet shows how to embed widgets in a StyledText.\n" + "Here is one: www.google.com , and here is another: www.yahoo.com .";
   static int[] offsets;
   static Control[] controls;
   static int MARGIN = 5;

   static void addControl(Control control, int offset) {
      StyleRange style = new StyleRange();
      style.start = offset;
      style.length = 1;
      control.pack();
      Rectangle rect = control.getBounds();
      int ascent = 2 * rect.height / 3;
      int descent = rect.height - ascent;
      style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN, rect.width + 2 * MARGIN);
      styledText.setStyleRange(style);
   }

   public static void main(String[] args) {
      final Display display = new Display();
      final Shell shell = new Shell(display);
      Font font = new Font(display, "Tahoma", 32, SWT.NORMAL);
      shell.setLayout(new GridLayout());
      styledText = new StyledText(shell, SWT.WRAP | SWT.BORDER);
      styledText.setFont(font);
      styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      styledText.setText(text);
      controls = new Control[2];
      Matcher m = Pattern.compile(" (www\\..*?) ").matcher(text);
      int x = 0;
      while (m.find()) {
         HyperLinkLabel button = new HyperLinkLabel(styledText, SWT.PUSH);
         button.setText(m.group(1));
         button.setFont(font);
         controls[x++] = button;
      }
      offsets = new int[controls.length];
      int lastOffset = 0;
      for (int i = 0; i < controls.length; i++) {
         int offset = text.indexOf(" www\\.", lastOffset) + 1;
         offsets[i] = offset;
         addControl(controls[i], offsets[i]);
         lastOffset = offset + 1;
      }

      // use a verify listener to keep the offsets up to date
      styledText.addVerifyListener(new VerifyListener() {
         public void verifyText(VerifyEvent e) {
            int start = e.start;
            int replaceCharCount = e.end - e.start;
            int newCharCount = e.text.length();
            for (int i = 0; i < offsets.length; i++) {
               int offset = offsets[i];
               if (start <= offset && offset < start + replaceCharCount) {
                  // this widget is being deleted from the text
                  if (controls[i] != null && !controls[i].isDisposed()) {
                     controls[i].dispose();
                     controls[i] = null;
                  }
                  offset = -1;
               }
               if (offset != -1 && offset >= start) offset += newCharCount - replaceCharCount;
               offsets[i] = offset;
            }
         }
      });

      // reposition widgets on paint event
      styledText.addPaintObjectListener(new PaintObjectListener() {
         public void paintObject(PaintObjectEvent event) {
            StyleRange style = event.style;
            int start = style.start;
            for (int i = 0; i < offsets.length; i++) {
               int offset = offsets[i];
               if (start == offset) {
                  Point pt = controls[i].getSize();
                  int x = event.x + MARGIN;
                  int y = event.y + event.ascent - 2 * pt.y / 3;
                  controls[i].setLocation(x, y);
                  break;
               }
            }
         }
      });

      shell.setSize(400, 400);
      shell.open();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) display.sleep();
      }
      font.dispose();
      display.dispose();
   }
}
