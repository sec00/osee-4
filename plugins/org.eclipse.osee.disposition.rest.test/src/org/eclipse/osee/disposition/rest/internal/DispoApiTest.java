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
package org.eclipse.osee.disposition.rest.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Angel Avila
 */
public class DispoApiTest {

   @Mock
   private Storage storage;
   @Mock
   private DispoResolutionValidator validator;
   @Mock
   private StorageProvider storageProvider;
   @Mock
   private IOseeBranch mockBranch;
   @Mock
   private DispoSet dispoSet;
   @Mock
   private DispoItem dispoItem;
   @Mock
   private ArtifactReadable author;
   @Mock
   private DispoProgram program;
   @Mock
   private Identifiable<String> setId;
   @Mock
   private Identifiable<String> itemId;
   @Mock
   private ArtifactId mockArtId;
   @Mock
   private DispoSetArtifact dispoSetArtifact;
   @Mock
   private JSONArray jsonArray;
   @Mock
   private List<Note> mockNotes;
   @Mock
   private JSONObject jsonObject;
   @Mock
   private DispoAnnotationData mockAnnotation;
   @Mock
   private Map<String, Discrepancy> mockDiscrepancies;
   @Mock
   private Discrepancy mockDiscrepancy;
   @Mock
   private List<DispoAnnotationData> mockAnnotations;
   @Mock
   private Iterator<String> mockKeys;
   @Mock
   private Date mockDate;
   @Mock
   private DispoDataFactory dataFactory;
   @Mock
   private DispoConnector dispoConnector;

   DispoApiImpl dispoApi = new DispoApiImpl();

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      when(program.getUuid()).thenReturn(23L);
      when(setId.getGuid()).thenReturn("ghijkl");
      when(itemId.getGuid()).thenReturn("mnopqr");
      when(mockArtId.getGuid()).thenReturn("artIdabc");

      when(mockBranch.getName()).thenReturn("branchName");
      when(storage.findUser()).thenReturn(author);
      when(storageProvider.get()).thenReturn(storage);

      dispoApi.setStorageProvider(storageProvider);
      dispoApi.setDataFactory(dataFactory);
      dispoApi.setDispoConnector(dispoConnector);
      dispoApi.setResolutionValidator(validator);

   }

   private <T> Answer<T> newAnswer(final T object) {
      return new Answer<T>() {

         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return object;
         }
      };
   }

   @Test
   public void testGetDispoPrograms() {
      ResultSet<IOseeBranch> programsSet = ResultSets.singleton(mockBranch);
      when(storage.getDispoBranches()).thenAnswer(newAnswer(programsSet));
      ResultSet<IOseeBranch> actual = dispoApi.getDispoPrograms();
      assertEquals(programsSet.iterator().next(), actual.iterator().next());
   }

   @Test
   public void testGetDispoSets() {
      List<DispoSet> dispoSetArts = Collections.singletonList(dispoSet);
      when(storage.findDispoSets(program, "code_coverge")).thenAnswer(newAnswer(dispoSetArts));

      when(dispoSet.getName()).thenReturn("name");
      when(dispoSet.getImportPath()).thenReturn("path");
      when(dispoSet.getNotesList()).thenReturn(mockNotes);
      when(dispoSet.getGuid()).thenReturn("setGuid");

      List<DispoSet> actualResultSet = dispoApi.getDispoSets(program, "code_coverage");
      DispoSet actualData = actualResultSet.iterator().next();
      assertEquals("setGuid", actualData.getGuid());
      assertEquals("name", actualData.getName());
      assertEquals("path", actualData.getImportPath());
      assertEquals(jsonArray, actualData.getNotesList());
   }

   @Test
   public void testGetDispoSetById() {
      when(storage.findDispoSetsById(program, setId.getGuid())).thenReturn(dispoSet);
      when(dispoSet.getName()).thenReturn("name");
      when(dispoSet.getImportPath()).thenReturn("path");
      when(dispoSet.getNotesList()).thenReturn(mockNotes);
      when(dispoSet.getGuid()).thenReturn("setGuid");

      DispoSet actual = dispoApi.getDispoSetById(program, setId.getGuid());
      assertEquals("setGuid", actual.getGuid());
      assertEquals("name", actual.getName());
      assertEquals("path", actual.getImportPath());
      assertEquals(jsonArray, actual.getNotesList());
   }

   @Test
   public void testGetDispoItems() {
      List<DispoItem> dispoItemArts = Collections.singletonList(dispoItem);
      when(storage.findDipoItems(program, setId.getGuid())).thenReturn(dispoItemArts);
      when(dispoItem.getName()).thenReturn("name");
      when(dispoItem.getGuid()).thenReturn("itemGuid");
      when(dispoItem.getCreationDate()).thenReturn(mockDate);
      when(dispoItem.getLastUpdate()).thenReturn(mockDate);
      when(dispoItem.getStatus()).thenReturn("status");
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);

      List<DispoItem> actualResultSet = dispoApi.getDispoItems(program, setId.getGuid());
      DispoItem actualData = actualResultSet.iterator().next();
      assertEquals("itemGuid", actualData.getGuid());
      assertEquals("name", actualData.getName());
      assertEquals(mockDate, actualData.getCreationDate());
      assertEquals(mockDate, actualData.getLastUpdate());
      assertEquals("status", actualData.getStatus());
      assertEquals(jsonObject, actualData.getDiscrepanciesList());
      assertEquals(mockAnnotations, actualData.getAnnotationsList());
   }

   @Test
   public void testGetDispoItemById() {
      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getName()).thenReturn("name");
      when(dispoItem.getGuid()).thenReturn("itemGuid");
      when(dispoItem.getCreationDate()).thenReturn(mockDate);
      when(dispoItem.getLastUpdate()).thenReturn(mockDate);
      when(dispoItem.getStatus()).thenReturn("status");
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);

      DispoItem actualData = dispoApi.getDispoItemById(program, itemId.getGuid());
      assertEquals("itemGuid", actualData.getGuid());
      assertEquals("name", actualData.getName());
      assertEquals(mockDate, actualData.getCreationDate());
      assertEquals(mockDate, actualData.getLastUpdate());
      assertEquals("status", actualData.getStatus());
      assertEquals(jsonObject, actualData.getDiscrepanciesList());
      assertEquals(mockAnnotations, actualData.getAnnotationsList());
   }

   @Test
   public void getDispoAnnotations() {
      String annotId = "dsf";
      int indexOfAnnot = 0;
      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(mockAnnotations.size()).thenReturn(1);
      when(mockAnnotations.get(indexOfAnnot)).thenReturn(mockAnnotation);
      when(mockAnnotation.getId()).thenReturn(annotId);
      when(mockAnnotation.getLocationRefs()).thenReturn("1-10");

      List<DispoAnnotationData> actualResultSet = dispoApi.getDispoAnnotations(program, itemId.getGuid());
      DispoAnnotationData actualData = actualResultSet.iterator().next();

      assertEquals(annotId, actualData.getId());
      assertEquals("1-10", actualData.getLocationRefs());
   }

   @Test
   public void getDispoAnnotationByIndex() {
      String idOfAnnot = "432";
      int indexOfAnnot = 0;
      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(mockAnnotations.size()).thenReturn(1);
      when(mockAnnotations.get(indexOfAnnot)).thenReturn(mockAnnotation);
      when(mockAnnotation.getId()).thenReturn(idOfAnnot);
      when(mockAnnotation.getLocationRefs()).thenReturn("1-10");
      DispoAnnotationData actualData = dispoApi.getDispoAnnotationById(program, itemId.getGuid(), idOfAnnot);
      dispoApi.getDispoAnnotationById(program, itemId.getGuid(), idOfAnnot);

      assertEquals(idOfAnnot, actualData.getId());
      assertEquals("1-10", actualData.getLocationRefs());
   }

   // Writers
   @Test
   public void testCreateDispositionSet() {
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setImportPath("C:\\");
      descriptor.setName("Test Disposition");

      DispoSetData setFromDescriptor = new DispoSetData();
      when(dataFactory.creteSetDataFromDescriptor(descriptor)).thenReturn(setFromDescriptor);

      when(storage.createDispoSet(author, program, setFromDescriptor)).thenReturn(mockArtId);
      Identifiable<String> createDispoSetId = dispoApi.createDispoSet(program, descriptor);
      assertEquals(mockArtId, createDispoSetId);
   }

   @Test
   public void testCreateDispositionAnnotation() {
      String expectedId = "dfs";
      DispoAnnotationData annotationToCreate = new DispoAnnotationData();
      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dataFactory.getNewId()).thenReturn(expectedId);
      when(dispoItem.getAssignee()).thenReturn("name");
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(dataFactory.createUpdatedItem(eq(mockAnnotations), eq(mockDiscrepancies))).thenReturn(dispoItem);
      when(dispoConnector.connectAnnotation(annotationToCreate, mockDiscrepancies)).thenReturn(false);
      annotationToCreate.setIsConnected(true); //Assume this Annotation was connected 

      // Only need to createUpdatedItem with updateStatus = True when annotation is valid and current status is INCOMPLETE 
      annotationToCreate.setResolution("VALID");
      when(dispoItem.getStatus()).thenReturn("COMPLETE");
      when(validator.validate(Matchers.any(DispoAnnotationData.class))).thenReturn(true);
      String acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate, "name");
      assertEquals(expectedId, acutal);

      when(dispoItem.getStatus()).thenReturn("PASS");
      acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate, "name");
      assertEquals(expectedId, acutal);

      when(dispoItem.getStatus()).thenReturn("INCOMPLETE");
      acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate, "name");
      assertEquals(expectedId, acutal);

      annotationToCreate.setResolution("INVALID");
      acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate, "name");
      assertEquals(expectedId, acutal);

      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(null); // shouldn't call dataFactory method
      acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate, "name");
      assertEquals("", acutal);

      verify(dispoConnector, times(4)).connectAnnotation(annotationToCreate, mockDiscrepancies);// Only tried to connect 3 times, excluded when annotations was invalid
   }

   @Test
   public void testEditDispoSet() {
      ArgumentCaptor<JSONArray> captor = ArgumentCaptor.forClass(JSONArray.class);
      DispoSetData newSet = new DispoSetData();

      when(storage.findDispoSetsById(program, setId.getGuid())).thenReturn(dispoSet);
      when(dispoSet.getNotesList()).thenReturn(mockNotes);

      String actual = dispoApi.editDispoSet(program, setId.getGuid(), newSet);
      assertFalse(Strings.isValid(actual)); // No report generated

      List<Note> setToEditNotes = new ArrayList<Note>();
      newSet.setNotesList(setToEditNotes);
      actual = dispoApi.editDispoSet(program, setId.getGuid(), newSet);
      assertFalse(Strings.isValid(actual)); // No report generated
      // Only should have merged Json Arrays once since the first newSet didn't have a Json Array
      verify(dataFactory, times(1)).mergeJsonArrays(eq(jsonArray), captor.capture());

      when(storage.findDispoSetsById(program, setId.getGuid())).thenReturn(null);
      actual = dispoApi.editDispoSet(program, setId.getGuid(), newSet);
      assertFalse(Strings.isValid(actual));// No report generated
   }

   @Test
   public void testEditDispoItem() {
      DispoItemData newItem = new DispoItemData();

      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);

      boolean actual = dispoApi.editDispoItem(program, itemId.getGuid(), newItem);
      assertTrue(actual);

      Map<String, Discrepancy> discrepanciesList = new HashMap<String, Discrepancy>();
      newItem.setDiscrepanciesList(discrepanciesList);
      actual = dispoApi.editDispoItem(program, itemId.getGuid(), newItem);
      assertFalse(actual);

      newItem.setAnnotationsList(mockAnnotations);
      actual = dispoApi.editDispoItem(program, itemId.getGuid(), newItem);
      assertFalse(actual);
   }

   @Test
   public void editDispoAnnotation() {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      DispoProgram programUuid = program;
      String itemUuid = itemId.getGuid();
      String expectedId = "faf";

      when(storage.findDispoItemById(programUuid, itemUuid)).thenReturn(dispoItem);
      when(dispoItem.getAssignee()).thenReturn("name");
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(mockDiscrepancies.get(expectedId)).thenReturn(mockDiscrepancy);
      when(mockAnnotations.size()).thenReturn(1);
      when(mockAnnotations.get(0)).thenReturn(mockAnnotation);
      // mocks for data util translation
      when(mockAnnotation.getId()).thenReturn(expectedId);
      when(mockAnnotation.getLocationRefs()).thenReturn("5-10");
      when(mockAnnotation.getResolution()).thenReturn("resOrig");
      when(mockAnnotation.getResolutionType()).thenReturn("CODE");
      when(mockAnnotation.getIsResolutionValid()).thenReturn(true); // We'll have the old annotation have a valid resolution to start

      // end

      // First with location refs, resolution type and resolution the same
      newAnnotation.setLocationRefs("5-10");
      newAnnotation.setResolution("resOrig");
      newAnnotation.setResolutionType("CODE");
      boolean actual = dispoApi.editDispoAnnotation(program, itemId.getGuid(), expectedId, newAnnotation, "name");
      assertTrue(actual);

      // Now change Location Refs, disconnector should be called
      newAnnotation.setLocationRefs("1-10");
      when(validator.validate(Matchers.any(DispoAnnotationData.class))).thenReturn(false);
      actual = dispoApi.editDispoAnnotation(program, itemId.getGuid(), expectedId, newAnnotation, "name");
      assertTrue(actual);

      // reset the resolution and change just the resolution type, disconnector and should be called
      newAnnotation.setLocationRefs("5-10");
      newAnnotation.setResolutionType("TEST");
      when(validator.validate(Matchers.any(DispoAnnotationData.class))).thenReturn(true);
      actual = dispoApi.editDispoAnnotation(program, itemId.getGuid(), expectedId, newAnnotation, "name");
      assertTrue(actual);

      // Reset resolution type, only change to resolution, disconnector is called
      newAnnotation.setResolutionType("CODE");
      newAnnotation.setResolution("NEW");
      when(validator.validate(Matchers.any(DispoAnnotationData.class))).thenReturn(true);
      actual = dispoApi.editDispoAnnotation(program, itemId.getGuid(), expectedId, newAnnotation, "name");
      assertTrue(actual);

      verify(dispoConnector, times(3)).connectAnnotation(any(DispoAnnotationData.class), eq(mockDiscrepancies));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void deletDispoAnnotation() {
      DispoProgram programUuid = program;
      String itemUuid = itemId.getGuid();
      String expectedId = "1";

      when(storage.findDispoItemById(programUuid, itemUuid)).thenReturn(dispoItem);
      when(dispoItem.getAssignee()).thenReturn("name");
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(mockAnnotations.size()).thenReturn(1);
      when(mockAnnotations.get(0)).thenReturn(mockAnnotation);
      // mocks for data util translation
      when(mockAnnotation.getId()).thenReturn(expectedId);
      // end
      // If the annotation being removed is invalid then createUpdatedItem should be called with 'false'
      DispoAnnotationData annotationInvalid = new DispoAnnotationData();
      annotationInvalid.setIsResolutionValid(false);

      boolean actual = dispoApi.deleteDispoAnnotation(program, itemId.getGuid(), expectedId, "name");
      assertTrue(actual);

      DispoAnnotationData annotationValid = new DispoAnnotationData();
      annotationValid.setIsResolutionValid(true);
      annotationValid.setIsConnected(true);
      annotationValid.setResolutionType("OTHER");

      when(mockAnnotations.get(0)).thenReturn(annotationValid);
      actual = dispoApi.deleteDispoAnnotation(program, itemId.getGuid(), expectedId, "name");
      verify(dataFactory, times(2)).createUpdatedItem(any(List.class), eq(mockDiscrepancies));
      assertTrue(actual);
   }
}
