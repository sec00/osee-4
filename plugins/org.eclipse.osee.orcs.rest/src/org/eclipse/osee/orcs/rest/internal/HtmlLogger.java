/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.eclipse.osee.logger.Log;

/**
 * @author Ryan D. Brooks
 */
public class HtmlLogger implements Log {
   private final Writer writer;

   public HtmlLogger(OutputStream output) throws UnsupportedEncodingException {
      writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"), 10);
   }

   @Override
   public boolean isTraceEnabled() {
      return false;
   }

   @Override
   public void trace(String format, Object... args) {
   }

   @Override
   public void trace(Throwable th, String format, Object... args) {
   }

   @Override
   public boolean isDebugEnabled() {
      return false;
   }

   @Override
   public void debug(String format, Object... args) {
      writeLine(String.format(format, args));
   }

   private void writeLine(String line) {
      try {
         writer.write(line + "<br />\n");
         writer.flush();
      } catch (IOException ex) {
         // this really went badly
      }
   }

   @Override
   public void debug(Throwable th, String format, Object... args) {
   }

   @Override
   public boolean isInfoEnabled() {
      return false;
   }

   @Override
   public void info(String format, Object... args) {
   }

   @Override
   public void info(Throwable th, String format, Object... args) {
   }

   @Override
   public boolean isWarnEnabled() {
      return false;
   }

   @Override
   public void warn(String format, Object... args) {
   }

   @Override
   public void warn(Throwable th, String format, Object... args) {
   }

   @Override
   public boolean isErrorEnabled() {
      return false;
   }

   @Override
   public void error(String format, Object... args) {
   }

   @Override
   public void error(Throwable th, String format, Object... args) {
   }

}
