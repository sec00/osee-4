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
package org.eclipse.osee.ote.ui.internal;

import java.io.IOException;

import org.eclipse.osee.framework.jdk.core.util.IConsoleInputListener;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.eclipse.osee.ote.ui.OteConsoleWrapper;
import org.eclipse.osee.ote.ui.internal.prefs.OteConsolePreferences;
import org.eclipse.osee.ote.ui.internal.prefs.OteConsolePrefsUtil;

/**
 * @author Roberto E. Escobar
 */
public class OteConsoleServiceImpl implements IOteConsoleService {

   private static final int HIGH_TO_LOW_DIFF = 100;
   private final OteConsoleWrapper console = new OteConsoleWrapper("OTE Console2");
   private int limit;
   private boolean noLimit;
   private static OteConsoleServiceImpl instance;

   public OteConsoleServiceImpl() {
      limit = OteConsolePrefsUtil.getInt(OteConsolePreferences.BUFFER_LIMIT);
      noLimit = OteConsolePrefsUtil.getBoolean(OteConsolePreferences.NO_BUFFER_LIMIT);
      setWaterMarks();
      instance = this;
   }

   /**
    * 
    */
   private void setWaterMarks() {
      int lowMark, highMark;
      
      if(noLimit) {
         lowMark = -1;
         highMark = -1;
      } else if( limit > HIGH_TO_LOW_DIFF ){
         lowMark = limit -1;
         highMark = limit;
      } else {
         lowMark = limit - HIGH_TO_LOW_DIFF;
         highMark = limit;
      }
      
      console.setWaterMarks(lowMark, highMark);
   }
   
   public static OteConsoleServiceImpl getInstance() {
      return instance;
   }
   
   /**
    * @param limit the buffer size limit in bytes
    */
   public void setLimit(int limit) {
      this.limit = limit;
   }
   
   public void setNoLimit(boolean noLimit) {
      this.noLimit = noLimit;
   }

   private OteConsoleWrapper getConsole() {
      return console;
   }

   @Override
   public void addInputListener(IConsoleInputListener listener) {
      if (listener != null) {
         getConsole().addInputListener(listener);
      }
   }

   @Override
   public void removeInputListener(IConsoleInputListener listener) {
      if (listener != null) {
         getConsole().removeInputListener(listener);
      }
   }

   @Override
   public void write(String value) {
      getConsole().write(value);
   }

   @Override
   public void writeError(String value) {
      getConsole().writeError(value);
   }

   @Override
   public void prompt(String value) throws IOException {
      getConsole().prompt(value);
   }

   @Override
   public void popup() {
      getConsole().popup();
   }

   @Override
   public void write(String value, int type, boolean popup) {
      getConsole().write(value, type, popup);
   }

   public void close() {
      getConsole().shutdown();
   }
 }
