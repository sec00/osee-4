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

package org.eclipse.osee.framework.jdk.core.util.xml;

/**
 * @author Paul K. Waldfogel
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class SimpleNamespaceContext implements NamespaceContext {

   private final Map<String, String> urisByPrefix = new HashMap<String, String>();

   private final Map<String, Set<String>> prefixesByURI = new HashMap<String, Set<String>>();

   public SimpleNamespaceContext() {
      // prepopulate with xml and xmlns prefixes
      // per JavaDoc of NamespaceContext interface
      addNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
      addNamespace(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
   }

   public synchronized void addNamespace(String prefix, String namespaceURI) {
      urisByPrefix.put(prefix, namespaceURI);
      if (prefixesByURI.containsKey(namespaceURI)) {
         prefixesByURI.get(namespaceURI).add(prefix);
      } else {
         Set<String> set = new HashSet<String>();
         set.add(prefix);
         prefixesByURI.put(namespaceURI, set);
      }
   }

   @Override
   public String getNamespaceURI(String prefix) {
      if (prefix == null) {
         throw new IllegalArgumentException("prefix cannot be null");
      }
      if (urisByPrefix.containsKey(prefix)) {
         return urisByPrefix.get(prefix);
      } else {
         return XMLConstants.NULL_NS_URI;
      }
   }

   @Override
   public String getPrefix(String namespaceURI) {
      return getPrefixes(namespaceURI).next();
   }

   @Override
   public Iterator<String> getPrefixes(String namespaceURI) {
      if (namespaceURI == null) {
         throw new IllegalArgumentException("namespaceURI cannot be null");
      }
      if (prefixesByURI.containsKey(namespaceURI)) {
         return prefixesByURI.get(namespaceURI).iterator();
      } else {
         return Collections.EMPTY_SET.iterator();
      }
   }
}
