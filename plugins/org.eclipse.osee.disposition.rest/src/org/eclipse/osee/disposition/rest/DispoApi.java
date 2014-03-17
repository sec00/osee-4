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
package org.eclipse.osee.disposition.rest;

import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.rest.util.DispoFactory;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Angel Avila
 */
public interface DispoApi {

   // Queries

   IOseeBranch getDispoProgramById(DispoProgram program);

   ResultSet<IOseeBranch> getDispoPrograms();

   ResultSet<DispoSetData> getDispoSets(DispoProgram program);

   DispoSetData getDispoSetById(DispoProgram program, String dispoSetId);

   ResultSet<DispoItemData> getDispoItems(DispoProgram program, String dispoSetId);

   DispoItemData getDispoItemById(DispoProgram program, String itemId);

   ResultSet<DispoAnnotationData> getDispoAnnotations(DispoProgram program, String itemId);

   DispoAnnotationData getDispoAnnotationByIndex(DispoProgram program, String itemId, String annotationId);

   // Writes
   Identifiable<String> createDispoSet(DispoProgram program, DispoSetDescriptorData descriptor);

   Identifiable<String> createDispoItem(DispoProgram program, String setId, DispoItemData dispoItem);

   String createDispoAnnotation(DispoProgram program, String itemId, DispoAnnotationData annotation);

   boolean editDispoSet(DispoProgram program, String dispoSetId, DispoSetData newDispoSet);

   boolean editDispoItem(DispoProgram program, String itemId, DispoItemData newDispoItem);

   boolean editDispoAnnotation(DispoProgram program, String itemId, String annotationId, DispoAnnotationData newAnnotation);

   // Deletes

   boolean deleteDispoSet(DispoProgram program, String dispoSetId);

   boolean deleteDispoItem(DispoProgram program, String itemId);

   boolean deleteDispoAnnotation(DispoProgram program, String itemId, String annotationId);

   // Utilities
   boolean isUniqueSetName(DispoProgram program, String setName);

   boolean isUniqueItemName(DispoProgram program, String setId, String itemName);

   DispoFactory getDispoFactory();

}
