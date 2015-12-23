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
package org.eclipse.osee.disposition.rest.importer;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.importer.DispoSetCopier;
import org.eclipse.osee.disposition.rest.internal.report.OperationReport;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class AnnotationCopierTest {

   private final DispoConnector connector = new DispoConnector();

   String mockId1 = "gdd";
   String mockId2 = "gfs";
   String mockId3 = "gsdf";
   String mockId4 = "gfsdf";
   String mockId5 = "gfsfsdf";
   String mockId6 = "gfsfsfdf";
   String mockId7 = "gfsfsffdf";

   String mockAnnotId1 = "agdd";
   String mockAnnotId2 = "agfs";
   String mockAnnotId3 = "agsdf";
   String mockAnnotId4 = "agfsdf";
   String mockAnnotId5 = "agfsfsdf";

   Discrepancy discrepancy1;
   Discrepancy discrepancy2;
   Discrepancy discrepancy3;
   Discrepancy discrepancy4;
   Discrepancy discrepancy5;

   DispoItemData sourceItem;

   @Before
   public void setup() throws JSONException {
      sourceItem = new DispoItemData();
      sourceItem.setGuid(mockId4);
      JSONArray sourceAnnotations = new JSONArray();
      JSONObject sourceDescrepancies = new JSONObject();

      discrepancy1 = new Discrepancy();
      discrepancy1.setId(mockId1);
      discrepancy1.setLocation(2);
      discrepancy1.setText("One");
      sourceDescrepancies.put(discrepancy1.getId(), DispoUtil.discrepancyToJsonObj(discrepancy1));

      discrepancy2 = new Discrepancy();
      discrepancy2.setId(mockId2);
      discrepancy2.setLocation(3);
      discrepancy2.setText("TTWO");
      sourceDescrepancies.put(discrepancy2.getId(), DispoUtil.discrepancyToJsonObj(discrepancy2));

      discrepancy3 = new Discrepancy();
      discrepancy3.setId(mockId3);
      discrepancy3.setLocation(5);
      discrepancy3.setText("TTHRREEE");
      sourceDescrepancies.put(discrepancy3.getId(), DispoUtil.discrepancyToJsonObj(discrepancy3));

      discrepancy4 = new Discrepancy();
      discrepancy4.setId(mockId6);
      discrepancy4.setLocation(6);
      discrepancy4.setText("F");
      sourceDescrepancies.put(discrepancy4.getId(), DispoUtil.discrepancyToJsonObj(discrepancy4));

      discrepancy5 = new Discrepancy();
      discrepancy5.setId(mockId7);
      discrepancy5.setLocation(7);
      discrepancy5.setText("fff");
      sourceDescrepancies.put(discrepancy5.getId(), DispoUtil.discrepancyToJsonObj(discrepancy5));

      DispoAnnotationData annotation1 = new DispoAnnotationData();
      annotation1.setId(mockAnnotId1);
      annotation1.setLocationRefs("2");
      annotation1.setResolutionType(DispoStrings.Test_Unit_Resolution);
      annotation1.setResolution("file.dat");
      annotation1.setIndex(0);
      annotation1.setIdsOfCoveredDiscrepancies(new JSONArray());
      connector.connectAnnotation(annotation1, sourceDescrepancies);

      DispoAnnotationData annotation2 = new DispoAnnotationData();
      annotation2.setId(mockAnnotId2);
      annotation2.setLocationRefs("3");
      annotation2.setResolutionType(DispoStrings.Test_Unit_Resolution);
      annotation2.setResolution("file2.dat");
      annotation2.setIndex(1);
      annotation2.setIdsOfCoveredDiscrepancies(new JSONArray());
      connector.connectAnnotation(annotation2, sourceDescrepancies);

      DispoAnnotationData annotation3 = new DispoAnnotationData();
      annotation3.setId(mockAnnotId3);
      annotation3.setLocationRefs("5-7");
      annotation3.setResolutionType("Other");
      annotation3.setResolution("Manual Annotation");
      annotation3.setIndex(2);
      annotation3.setIdsOfCoveredDiscrepancies(new JSONArray());
      connector.connectAnnotation(annotation3, sourceDescrepancies);

      DispoAnnotationData annotation4 = new DispoAnnotationData();
      annotation4.setId(mockAnnotId4);
      annotation4.setLocationRefs("8");
      annotation4.setResolutionType("Other");
      annotation4.setResolution("Manual Annotation2");
      annotation4.setIndex(3);
      annotation4.setIdsOfCoveredDiscrepancies(new JSONArray());
      connector.connectAnnotation(annotation4, sourceDescrepancies);

      sourceAnnotations.put(annotation1.getIndex(), DispoUtil.annotationToJsonObj(annotation1));
      sourceAnnotations.put(annotation2.getIndex(), DispoUtil.annotationToJsonObj(annotation2));
      sourceAnnotations.put(annotation3.getIndex(), DispoUtil.annotationToJsonObj(annotation3));
      sourceAnnotations.put(annotation4.getIndex(), DispoUtil.annotationToJsonObj(annotation4));

      sourceItem.setAnnotationsList(sourceAnnotations);
      sourceItem.setDiscrepanciesList(sourceDescrepancies);
   }

   @Test
   public void testCopyEntireSetNoAnnotationsOnDestItem() throws Exception {
      DispoItemData destItem = new DispoItemData();
      destItem.setStatus(DispoStrings.Item_InComplete);
      destItem.setGuid(mockId5);
      destItem.setAnnotationsList(new JSONArray());
      destItem.setDiscrepanciesList(new JSONObject());

      // First set the Destination Item's discrepancies the same as the source
      JSONObject destDiscrepancies = new JSONObject();
      destDiscrepancies.put(discrepancy1.getId(), DispoUtil.discrepancyToJsonObj(discrepancy1));
      destDiscrepancies.put(discrepancy2.getId(), DispoUtil.discrepancyToJsonObj(discrepancy2));
      destDiscrepancies.put(discrepancy3.getId(), DispoUtil.discrepancyToJsonObj(discrepancy3));
      destDiscrepancies.put(discrepancy4.getId(), DispoUtil.discrepancyToJsonObj(discrepancy4));
      destDiscrepancies.put(discrepancy5.getId(), DispoUtil.discrepancyToJsonObj(discrepancy5));

      destItem.setDiscrepanciesList(destDiscrepancies);
      OperationReport report = new OperationReport();

      DispoSetCopier copier = new DispoSetCopier(connector);
      List<DispoItem> toModify = copier.copyAllDispositions(Collections.singletonMap(destItem.getName(), destItem),
         Collections.singletonList((DispoItem) sourceItem), true, report);

      DispoItem modifiedItem = toModify.get(0);
      JSONArray modifiedItemAnnotations = modifiedItem.getAnnotationsList();
      DispoAnnotationData newAnnotation =
         DispoUtil.jsonObjToDispoAnnotationData(modifiedItemAnnotations.getJSONObject(0));
      DispoAnnotationData newAnnotation2 =
         DispoUtil.jsonObjToDispoAnnotationData(modifiedItemAnnotations.getJSONObject(1));

      // We should of copied all NON-Default Anntotations
      Assert.assertEquals("5-7", newAnnotation.getLocationRefs());
      Assert.assertEquals("Manual Annotation", newAnnotation.getResolution());
      Assert.assertEquals("8", newAnnotation2.getLocationRefs());
      Assert.assertEquals("Manual Annotation2", newAnnotation2.getResolution());
      Assert.assertEquals(2, modifiedItemAnnotations.length());
      Assert.assertEquals(mockId5, modifiedItem.getGuid());
   }

   @Test
   public void testCopyEntireSetSomeAnnotationsOnDest() throws JSONException {
      DispoItemData destItem = new DispoItemData();
      destItem.setStatus(DispoStrings.Item_InComplete);
      destItem.setGuid(mockId5);
      destItem.setAnnotationsList(new JSONArray());
      destItem.setDiscrepanciesList(new JSONObject());

      // First set the Destination Item's discrepancies the same as the source
      JSONObject destDiscrepancies = new JSONObject();
      destDiscrepancies.put(discrepancy1.getId(), DispoUtil.discrepancyToJsonObj(discrepancy1));
      destDiscrepancies.put(discrepancy2.getId(), DispoUtil.discrepancyToJsonObj(discrepancy2));
      destDiscrepancies.put(discrepancy3.getId(), DispoUtil.discrepancyToJsonObj(discrepancy3));
      destDiscrepancies.put(discrepancy4.getId(), DispoUtil.discrepancyToJsonObj(discrepancy4));
      destDiscrepancies.put(discrepancy5.getId(), DispoUtil.discrepancyToJsonObj(discrepancy5));
      destItem.setDiscrepanciesList(destDiscrepancies);

      // We're gonna set this annotation on the Dest Item this Annotation should not get overwritten but
      DispoAnnotationData annotationD1 = new DispoAnnotationData();
      String expectedId = "DIID";
      annotationD1.setIdsOfCoveredDiscrepancies(new JSONArray());
      annotationD1.setId(expectedId);
      annotationD1.setLocationRefs("8");
      annotationD1.setResolutionType(DispoStrings.Test_Unit_Resolution);
      annotationD1.setResolution("file444.dat");
      annotationD1.setIndex(0);
      JSONArray destAnnotations = new JSONArray();
      destAnnotations.put(annotationD1.getIndex(), DispoUtil.annotationToJsonObj(annotationD1));

      destItem.setAnnotationsList(destAnnotations);
      destItem.setDiscrepanciesList(destDiscrepancies);

      OperationReport report = new OperationReport();
      DispoSetCopier copier = new DispoSetCopier(connector);
      List<DispoItem> toModify = copier.copyAllDispositions(Collections.singletonMap(destItem.getName(), destItem),
         Collections.singletonList((DispoItem) sourceItem), true, report);

      DispoItem modifiedItem = toModify.get(0);
      JSONArray modifiedItemAnnotations = modifiedItem.getAnnotationsList();
      DispoAnnotationData origAnnotation =
         DispoUtil.jsonObjToDispoAnnotationData(modifiedItemAnnotations.getJSONObject(0));
      DispoAnnotationData newAnnotation =
         DispoUtil.jsonObjToDispoAnnotationData(modifiedItemAnnotations.getJSONObject(1));

      // We should of copied all NON-Default Anntotations
      Assert.assertEquals(expectedId, origAnnotation.getId());
      Assert.assertEquals("5-7", newAnnotation.getLocationRefs());
      Assert.assertEquals("Manual Annotation", newAnnotation.getResolution());
      Assert.assertEquals(2, modifiedItemAnnotations.length());
      Assert.assertEquals(mockId5, modifiedItem.getGuid());
   }

}
