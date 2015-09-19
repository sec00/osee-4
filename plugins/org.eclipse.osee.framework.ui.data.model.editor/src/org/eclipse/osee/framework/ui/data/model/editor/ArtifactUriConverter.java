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
package org.eclipse.osee.framework.ui.data.model.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactUriConverter extends ExtensibleURIConverterImpl {

   @Override
   public OutputStream createOutputStream(URI uri) throws IOException {
      return super.createOutputStream(uri);
   }

   @Override
   public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
      return super.createOutputStream(uri, options);
   }

   @Override
   public InputStream createInputStream(URI uri) throws IOException {
      return super.createInputStream(uri);
   }

   @Override
   public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
      return super.createInputStream(uri, options);
   }

   @Override
   public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
      return super.getAttributes(uri, options);
   }

}