/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Date;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public interface AttributeTypeToken<T> extends AttributeTypeId, FullyNamed, HasDescription, NamedId {
   static final AttributeTypeToken<Object> SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL);
   static final String APPLICATION_ZIP = "application/zip";
   static final String TEXT_CALENDAR = "text/calendar";
   static final String TEXT_URI_LIST = "text/uri-list";
   static final String APPLICATION_MSWORD = "application/msword";

   String getMediaType();

   String getDefaultValue();

   Class<?> getValueType();

   default boolean isEnumerated() {
      return getValueType().equals(Enum.class);
   }

   default boolean isBooleanType() {
      return getValueType().equals(Enum.class);
   }

   default boolean isIntegerType() {
      return getValueType().equals(Integer.class);
   }

   default boolean isDateType() {
      return getValueType().equals(Date.class);
   }

   default boolean isFloatingType() {
      return getValueType().equals(Float.class);
   }

   default boolean isStringType() {
      return getValueType().equals(String.class);
   }

   default boolean isLongType() {
      return getValueType().equals(Long.class);
   }

   default boolean isArtifactIdType() {
      return getValueType().equals(ArtifactId.class);
   }

   public static <T> AttributeTypeToken<T> cast(AttributeTypeToken<?> attributeType, Class<T> clazz) {
      if (attributeType.getValueType().equals(clazz)) {
         return (AttributeTypeToken<T>) attributeType;
      }
      throw new OseeArgumentException("Attribute type %s has value type %s not %s", attributeType,
         attributeType.getValueType(), clazz.getName());
   }

   public static <T> AttributeTypeToken<T> valueOf(String id) {
      return valueOf(Long.valueOf(id), Named.SENTINEL);
   }

   String getMediaType();

   static <T> AttributeTypeToken<T> valueOf(int id, String name) {
      return valueOf(Long.valueOf(id), name, "", null, MediaType.TEXT_PLAIN);
   }

   static <T> AttributeTypeToken<T> valueOf(Long id, String name) {
      return valueOf(id, name, "", null, MediaType.TEXT_PLAIN);
   }

   static <T> AttributeTypeToken<T> valueOf(int id, String name, String description) {
      return valueOf(Long.valueOf(id), name, description, null, MediaType.TEXT_PLAIN);
   }

   static <T> AttributeTypeToken<T> valueOf(Long id, String name, String description) {
      return valueOf(id, name, description, null, MediaType.TEXT_PLAIN);
   }


   }

   static AttributeTypeArtifactId createArtifactId(Long id, String name, String mediaType, String description) {
      return createArtifactId(id, name, mediaType, description, determineTaggerType(mediaType));
      }
      
   static AttributeTypeArtifactId createArtifactIdNoTag(Long id, String name, String mediaType, String description) {
      return createArtifactId(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
         @Override
         public String getMediaType() {
            return mediaType;
   }

         @Override
         public String getDefaultValue() {
            return null;
         }
      }
      return new AttributeTypeImpl(id, name, description, valueType, mediaType);
   }

   public static AttributeTypeToken<String> createEnum(Long id, String name, String mediaType, String description, String... enumNames) {
      return valueOf(id, name, description, Enum.class, mediaType);
   }

   public static AttributeTypeToken<String> createString(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, String.class, mediaType);
   }

   public static AttributeTypeToken<Boolean> createBoolean(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, Boolean.class, mediaType);
   }

   public static AttributeTypeToken<Date> createDate(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, String.class, mediaType);
   }

   public static AttributeTypeToken<ArtifactId> createArtifactId(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, ArtifactId.class, mediaType);
   }

   public static AttributeTypeToken<BranchId> createBranchId(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, BranchId.class, mediaType);
   }

   public static AttributeTypeToken<InputStream> createInputStream(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, InputStream.class, mediaType);
   }

   public static AttributeTypeToken<Integer> createInteger(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, Integer.class, mediaType);
   }

   static AttributeTypeBoolean createBoolean(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeBoolean(id, name, mediaType, description, taggerType);
   }

   static AttributeTypeBoolean createBoolean(Long id, String name, String mediaType, String description) {
      return createBoolean(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   static AttributeTypeBoolean createBooleanNoTag(Long id, String name, String mediaType, String description) {
      return createBoolean(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static AttributeTypeBranchId createBranchId(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeBranchId(id, name, mediaType, description, taggerType);
   }

   static AttributeTypeBranchId createBranchId(Long id, String name, String mediaType, String description) {
      return createBranchId(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   static AttributeTypeBranchId createBranchIdNoTag(Long id, String name, String mediaType, String description) {
      return createBranchId(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static AttributeTypeDate createDate(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeDate(id, name, mediaType, description, taggerType);
   }

   static AttributeTypeDate createDate(Long id, String name, String mediaType, String description) {
      return createDate(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   static AttributeTypeDate createDateNoTag(Long id, String name, String mediaType, String description) {
      return createDate(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static AttributeTypeDouble createDouble(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeDouble(id, name, mediaType, description, taggerType);
   }

   static AttributeTypeDouble createDouble(Long id, String name, String mediaType, String description) {
      return createDouble(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   static AttributeTypeDouble createDoubleNoTag(Long id, String name, String mediaType, String description) {
      return createDouble(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static AttributeTypeEnum createEnum(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType, String... enumNames) {
      return new AttributeTypeEnum(id, name, mediaType, description, taggerType, enumNames);
   }

   static AttributeTypeEnum createEnum(Long id, String name, String mediaType, String description, String... enumNames) {
      return createEnum(id, name, mediaType, description, determineTaggerType(mediaType), enumNames);
   }

   static AttributeTypeEnum createEnumNoTag(Long id, String name, String mediaType, String description, String... enumNames) {
      return createEnum(id, name, mediaType, description, TaggerTypeToken.SENTINEL, enumNames);
   }

   static AttributeTypeInputStream createInputStream(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeInputStream(id, name, mediaType, description, taggerType);
   }

   static AttributeTypeInputStream createInputStream(Long id, String name, String mediaType, String description) {
      return createInputStream(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   static AttributeTypeInputStream createInputStreamNoTag(Long id, String name, String mediaType, String description) {
      return createInputStream(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static AttributeTypeInteger createInteger(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeInteger(id, name, mediaType, description, taggerType);
   }

   static AttributeTypeInteger createInteger(Long id, String name, String mediaType, String description) {
      return createInteger(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   static AttributeTypeInteger createIntegerNoTag(Long id, String name, String mediaType, String description) {
      return createInteger(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   static AttributeTypeString createString(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return new AttributeTypeString(id, name, mediaType, description, taggerType);
   }

   static AttributeTypeString createString(Long id, String name, String mediaType, String description) {
      return createString(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   static AttributeTypeString createStringNoTag(Long id, String name, String mediaType, String description) {
      return createString(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   /**
    * return the default tagger for the given mediaType
    */
   static TaggerTypeToken determineTaggerType(String mediaType) {
      switch (mediaType) {
         case "application/msword":
         case MediaType.TEXT_HTML:
            return TaggerTypeToken.XmlTagger;
         default:
            return TaggerTypeToken.PlainTextTagger;
      }
   }
}