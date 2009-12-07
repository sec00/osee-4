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
package org.eclipse.osee.coverage.vcast;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.vcast.VcpResultsFile.ResultsValue;
import org.eclipse.osee.coverage.vcast.VcpSourceFile.SourceValue;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class VectorCastAdaCoverageImporter implements ICoverageImporter {

   private CoverageImport coverageImport;
   private final IVectorCastCoverageImportProvider vectorCastCoverageImportProvider;

   public VectorCastAdaCoverageImporter(IVectorCastCoverageImportProvider vectorCastCoverageImportProvider) {
      this.vectorCastCoverageImportProvider = vectorCastCoverageImportProvider;
   }

   @Override
   public CoverageImport run() {
      coverageImport = new CoverageImport("VectorCast Import");
      if (!Strings.isValid(vectorCastCoverageImportProvider.getVCastDirectory())) {
         coverageImport.getLog().logError("VectorCast directory must be specified");
         return coverageImport;
      }
      File file = new File(vectorCastCoverageImportProvider.getVCastDirectory());
      if (!file.exists()) {
         coverageImport.getLog().logError(
               String.format("VectorCast directory doesn't exist [%s]",
                     vectorCastCoverageImportProvider.getVCastDirectory()));
         return coverageImport;
      }
      VCastVcp vCastVcp = null;
      try {
         vCastVcp = new VCastVcp(vectorCastCoverageImportProvider.getVCastDirectory());
      } catch (Exception ex) {
         coverageImport.getLog().logError("Exception reading vcast.vcp file: " + ex.getLocalizedMessage());
         return coverageImport;
      }

      coverageImport.setLocation(vectorCastCoverageImportProvider.getVCastDirectory());

      // Create file and subprogram Coverage Units and execution line Coverage Items
      Map<String, CoverageUnit> fileNumToCoverageUnit = new HashMap<String, CoverageUnit>();
      for (VcpSourceFile vcpSourceFile : vCastVcp.sourceFiles) {
         try {
            CoverageDataFile coverageDataFile = vcpSourceFile.getCoverageDataFile();
            for (CoverageDataUnit coverageDataUnit : coverageDataFile.getCoverageDataUnits()) {
               CoverageUnit fileCoverageUnit =
                     new CoverageUnit(null, vcpSourceFile.getValue(SourceValue.SOURCE_FILENAME), "");
               String fileNamespace = vectorCastCoverageImportProvider.getFileNamespace(coverageDataUnit.getName());
               fileCoverageUnit.setNamespace(fileNamespace);
               CoverageUnit parent = coverageImport.getOrCreateParent(fileCoverageUnit.getNamespace());
               if (parent != null) {
                  parent.addCoverageUnit(fileCoverageUnit);
               } else {
                  coverageImport.addCoverageUnit(fileCoverageUnit);
               }
               VcpSourceLisFile vcpSourceLisFile = vcpSourceFile.getVcpSourceLisFile();
               fileCoverageUnit.setFileContents(vcpSourceLisFile.getText());
               int methodNum = 0;
               for (CoverageDataSubProgram coverageDataSubProgram : coverageDataUnit.getSubPrograms()) {
                  methodNum++;
                  CoverageUnit methodCoverageUnit =
                        new CoverageUnit(fileCoverageUnit, coverageDataSubProgram.getName(), "");
                  fileCoverageUnit.addCoverageUnit(methodCoverageUnit);
                  methodCoverageUnit.setOrderNumber(String.valueOf(methodNum));
                  for (LineNumToBranches lineNumToBranches : coverageDataSubProgram.getLineNumToBranches()) {
                     CoverageItem coverageItem =
                           new CoverageItem(methodCoverageUnit, CoverageMethodEnum.Not_Covered,
                                 String.valueOf(lineNumToBranches.getLineNum()));
                     Pair<String, Boolean> lineData =
                           vcpSourceLisFile.getExecutionLine(String.valueOf(methodNum),
                                 String.valueOf(lineNumToBranches.getLineNum()));
                     coverageItem.setName(lineData.getFirst());
                     if (lineData.getSecond()) {
                        coverageItem.setCoverageMethod(CoverageMethodEnum.Exception_Handling);
                     }
                     methodCoverageUnit.addCoverageItem(coverageItem);
                  }
               }
               fileNumToCoverageUnit.put(String.valueOf(coverageDataUnit.getIndex()), fileCoverageUnit);
            }
         } catch (Exception ex) {
            coverageImport.getLog().logError(
                  String.format("Error processing coverage for [%s].  " + ex.getLocalizedMessage(), vcpSourceFile));
            continue;

         }
      }

      for (VcpResultsFile vcpResultsFile : vCastVcp.resultsFiles) {
         String testUnitName = vcpResultsFile.getValue(ResultsValue.FILENAME);
         for (String fileNum : vcpResultsFile.getVcpResultsDatFile().getFileNumbers()) {
            CoverageUnit coverageUnit = fileNumToCoverageUnit.get(fileNum);
            if (coverageUnit == null) {
               coverageImport.getLog().logError(
                     String.format("coverageUnit doesn't exist for unit_number [%s]", fileNum));
               continue;
            }
            for (Pair<String, HashSet<String>> methodExecutionPair : vcpResultsFile.getVcpResultsDatFile().getMethodExecutionPairs(
                  fileNum)) {
               String methodNum = methodExecutionPair.getFirst();
               Set<String> executeNums = methodExecutionPair.getSecond();
               for (String executeNum : executeNums) {
                  // Find or create new coverage item for method num /execution line
                  CoverageItem coverageItem = coverageUnit.getCoverageItem(executeNum);
                  if (coverageItem == null) {
                     coverageImport.getLog().logError(
                           String.format("Can't retrieve method [%s] from coverageUnit [%s] for test unit [%s]",
                                 methodNum, coverageUnit, testUnitName));
                  } else {
                     coverageItem.setCoverageMethod(CoverageMethodEnum.Test_Unit);
                     coverageItem.addTestUnitName(testUnitName);
                  }
               }
            }
         }
      }
      return coverageImport;
   }

   @Override
   public String getName() {
      return "VectorCast Import";
   }

}
