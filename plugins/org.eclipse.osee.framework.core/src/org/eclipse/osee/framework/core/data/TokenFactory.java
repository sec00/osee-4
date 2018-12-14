/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public final class TokenFactory {

   public static final Pattern nameIdPattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");

   private TokenFactory() {
      // Utility Class
   }

   public static IArtifactType createArtifactType(long guid, String name) {
      return new ArtifactTypeToken(guid, name);
   }

   public static IArtifactType createArtifactType() {
      return new ArtifactTypeToken();
   }

   /**
    * @param token as [name]-[uuid]
    */
   public static IArtifactType createArtifactTypeFromToken(String token) {
      Matcher matcher = nameIdPattern.matcher(token);
      if (matcher.find()) {
         Long uuid = Long.valueOf(matcher.group(2));
         String name = matcher.group(1);
         return new ArtifactTypeToken(uuid, name);
      }
      return null;
   }

   public static RelationTypeToken createRelationType(long id, String name) {
      return RelationTypeToken.create(id, name);
   }

   public static ArtifactToken createArtifactToken(long id, String guid, String name, BranchId branch, IArtifactType artifactType) {
      return ArtifactToken.valueOf(id, guid, name, branch, artifactType);
   }

   private final static class ArtifactTypeToken extends NamedIdBase implements IArtifactType {

      public ArtifactTypeToken() {
         super();
      }

      public ArtifactTypeToken(Long id, String name) {
         super(id, name);
      }

      @Override
      public Long getGuid() {
         return getId();
      }
   }

}
