/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.types.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndexProvider;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesLoaderFactory;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumTypes;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTypesImpl implements OrcsTypes {

   private final OrcsTypesIndexProvider indexProvider;

   private final Log logger;
   private final OrcsSession session;
   private final OrcsTypesDataStore dataStore;
   private final OrcsTypesLoaderFactory loaderFactory;

   private final ArtifactTypes artifactTypes;
   private final AttributeTypes attributeTypes;
   private final RelationTypes relationTypes;
   private final EnumTypes enumTypes;

   public OrcsTypesImpl(Log logger, OrcsSession session, OrcsTypesDataStore dataStore, OrcsTypesLoaderFactory loaderFactory, OrcsTypesIndexProvider indexProvider) {
      this.logger = logger;
      this.session = session;
      this.dataStore = dataStore;
      this.loaderFactory = loaderFactory;

      this.indexProvider = indexProvider;

      this.artifactTypes = new ArtifactTypesImpl(indexProvider);
      this.attributeTypes = new AttributeTypesImpl(indexProvider, indexProvider);
      this.relationTypes = new RelationTypesImpl(indexProvider);
      this.enumTypes = new EnumTypesImpl(indexProvider);
   }

   @Override
   public ArtifactTypes getArtifactTypes() {
      return artifactTypes;
   }

   @Override
   public AttributeTypes getAttributeTypes() {
      return attributeTypes;
   }

   @Override
   public RelationTypes getRelationTypes() {
      return relationTypes;
   }

   @Override
   public EnumTypes getEnumTypes() {
      return enumTypes;
   }

   @Override
   public void invalidateAll() {
      indexProvider.invalidate();
   }

   @Override
   public void loadTypes(final IResource resource) {
      indexProvider.setLoader(loaderFactory.createTypesLoader(session, resource));
   }

   @Override
   public void loadTypes(String model) {
      try {
         loadTypes(new ByteResource("http.osee.model", model.getBytes("UTF-8")));
         invalidateAll();
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private static final class ByteResource implements IResource {
      private final String filename;
      private final byte[] bytes;

      public ByteResource(String filename, byte[] bytes) {
         super();
         this.filename = filename;
         this.bytes = bytes;
      }

      @Override
      public InputStream getContent() {
         return new ByteArrayInputStream(bytes);
      }

      @Override
      public URI getLocation() {
         String modelName = filename;
         if (!modelName.endsWith(".osee")) {
            modelName += ".osee";
         }
         try {
            return new URI("osee:/" + modelName);
         } catch (URISyntaxException ex) {
            throw new OseeCoreException(ex, "Error creating URI for [%s]", modelName);
         }
      }

      @Override
      public String getName() {
         return filename;
      }

      @Override
      public boolean isCompressed() {
         return false;
      }
   }

   @Override
   public Callable<Void> writeTypes(final OutputStream outputStream) {
      return new CancellableCallable<Void>() {
         @Override
         public Void call() throws Exception {
            logger.trace("Writing OrcsTypes for session [%s]", session);
            IResource resource = indexProvider.getOrcsTypesResource();
            InputStream inputStream = null;
            try {
               inputStream = resource.getContent();
               checkForCancelled();
               Lib.inputStreamToOutputStream(inputStream, outputStream);
            } catch (Exception ex) {
               OseeCoreException.wrapAndThrow(ex);
            } finally {
               Lib.close(inputStream);
               Lib.close(outputStream);
            }
            return null;
         }
      };
   }

   @Override
   public Callable<Void> purgeArtifactsByArtifactType(Collection<? extends ArtifactTypeToken> artifactTypes) {
      return dataStore.purgeArtifactsByArtifactType(session, artifactTypes);
   }

   @Override
   public Callable<Void> purgeAttributesByAttributeType(Collection<? extends AttributeTypeId> attributeTypes) {
      return dataStore.purgeAttributesByAttributeType(session, attributeTypes);
   }

   @Override
   public Callable<Void> purgeRelationsByRelationType(Collection<? extends IRelationType> relationTypes) {
      return dataStore.purgeRelationsByRelationType(session, relationTypes);
   }

}
