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
package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public class TaggingEngine {
   private final Map<String, Tagger> taggers;
   private final TagProcessor tagProcessor;
   private final ConcurrentHashMap<Long, Tagger> typeIdToTaggerMap = new ConcurrentHashMap<Long, Tagger>(1000);

   public TaggingEngine(Map<String, Tagger> taggers, TagProcessor tagProcessor, AttributeTypes attributeTypes) {
      this.taggers = taggers;
      this.tagProcessor = tagProcessor;
      loadCache(attributeTypes);
   }

   private void loadCache(AttributeTypes attributeTypes) {
      for (IAttributeType type : attributeTypes.getAll()) {
         String taggerId = attributeTypes.getTaggerId(type);
         if (!taggerId.equals("")) {
            Tagger tagger = getTagger(taggerId);
            typeIdToTaggerMap.put(type.getGuid(), tagger);
         }
      }
   }

   public TagProcessor getTagProcessor() {
      return tagProcessor;
   }

   public Tagger getDefaultTagger() throws OseeCoreException {
      return getTagger("DefaultAttributeTaggerProvider");
   }

   private String normalize(String alias) {
      String key = alias;
      if (Strings.isValid(key) && key.contains(".")) {
         key = Lib.getExtension(key);
      }
      return key;
   }

   public boolean hasTagger(String taggerId) {
      String key = normalize(taggerId);
      Tagger tagger = taggers.get(key);
      return tagger != null;
   }

   public Tagger getTagger(String taggerId) throws OseeCoreException {
      String key = normalize(taggerId);
      Tagger tagger = taggers.get(key);
      Conditions.checkNotNull(tagger, "tagger", "Unable to find tagger for [%s]", taggerId);
      return tagger;
   }

   public Tagger getTagger(Long attributeTypeId) {
      return typeIdToTaggerMap.get(attributeTypeId);
   }
}