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
package org.eclipse.osee.jaxrs.client;

import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public abstract class JaxRsConfirmAccessHandler {

   public static interface Permission {
      String getName();

      String getDescription();

      boolean isDefault();
   }

   public static interface ConfirmAccessRequest {

      String getApplicationName();

      String getApplicationDescription();

      String getApplicationLogoUri();

      String getApplicationWebUri();

      String getEndUserName();

      List<? extends Permission> getPermissionsRequested();

   }

   public static interface ConfirmAccessResponse {

      boolean isGranted();

      List<? extends Permission> getPermissionsGranted();
   }

   public abstract ConfirmAccessResponse onConfirmAccess(ConfirmAccessRequest request);

   protected ConfirmAccessResponse acceptAll(ConfirmAccessRequest request) {
      return new AcceptAllResponse(request);
   }

   private static final class AcceptAllResponse implements ConfirmAccessResponse {
      private final ConfirmAccessRequest request;

      public AcceptAllResponse(ConfirmAccessRequest request) {
         super();
         this.request = request;
      }

      @Override
      public boolean isGranted() {
         return true;
      }

      @Override
      public List<? extends Permission> getPermissionsGranted() {
         return request.getPermissionsRequested();
      }

      @Override
      public String toString() {
         return "AcceptAllResponse [isGranted()=" + isGranted() + ", getPermissionsGranted()=" + getPermissionsGranted() + "]";
      }
   }
}