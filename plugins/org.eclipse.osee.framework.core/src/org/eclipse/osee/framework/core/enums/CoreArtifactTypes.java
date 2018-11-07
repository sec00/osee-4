/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTypes {

   private static final List<ArtifactTypeToken> tokens = new ArrayList<>();

   // @formatter:off
   public static final ArtifactTypeToken Artifact = add(ArtifactTypeToken.valueOf(1L, true, "Artifact"));
   public static final ArtifactTypeToken AbstractAccessControlled = add(ArtifactTypeToken.valueOf(17L, true, "Abstract Access Controlled", Artifact));
   public static final ArtifactTypeToken AbstractHeading = add(ArtifactTypeToken.valueOf(805L, true, "Abstract Heading", Artifact));
   public static final ArtifactTypeToken AbstractImplementationDetails = add(ArtifactTypeToken.valueOf(921211884L, true, "Abstract Implementation Details", Artifact));
   public static final ArtifactTypeToken Requirement = add(ArtifactTypeToken.valueOf(21L, false, "Requirement", Artifact));
   public static final ArtifactTypeToken AbstractSpecRequirement = add(ArtifactTypeToken.valueOf(58551193202327573L, false, "Abstract Spec Requirement", Requirement));
   public static final ArtifactTypeToken AbstractSoftwareRequirement = add(ArtifactTypeToken.valueOf(23L, true, "Abstract Software Requirement", AbstractSpecRequirement));
   public static final ArtifactTypeToken AbstractSubsystemRequirement = add(ArtifactTypeToken.valueOf(797L, true, "Abstract Subsystem Requirement", AbstractSpecRequirement));
   public static final ArtifactTypeToken AbstractSystemRequirement = add(ArtifactTypeToken.valueOf(796L, true, "Abstract System Requirement", AbstractSpecRequirement));
   public static final ArtifactTypeToken AbstractTestResult = add(ArtifactTypeToken.valueOf(38L, true, "Abstract Test Result", Artifact));
   public static final ArtifactTypeToken GeneralData = add(ArtifactTypeToken.valueOf(12L, false, "General Data", Artifact));
   public static final ArtifactTypeToken AccessControlModel = add(ArtifactTypeToken.valueOf(2L, false, "Access Control Model", GeneralData));
   public static final ArtifactTypeToken BranchView = add(ArtifactTypeToken.valueOf(5849078277209560034L, false, "Branch View", Artifact));
   public static final ArtifactTypeToken Breaker = add(ArtifactTypeToken.valueOf(188458869981236L, false, "Breaker", Artifact));
   public static final ArtifactTypeToken CertificationBaselineEvent = add(ArtifactTypeToken.valueOf(99L, false, "Certification Baseline Event", Artifact));
   public static final ArtifactTypeToken CodeUnit = add(ArtifactTypeToken.valueOf(58L, false, "Code Unit", Artifact));
   public static final ArtifactTypeToken Component = add(ArtifactTypeToken.valueOf(57L, false, "Component", Artifact));
   public static final ArtifactTypeToken MSWord = add(ArtifactTypeToken.valueOf(16L, true, "MS Word", Artifact));
   public static final ArtifactTypeToken MSWordTemplate = add(ArtifactTypeToken.valueOf(19L, false, "MS Word Template", MSWord));
   public static final ArtifactTypeToken CustomerRequirementMSWord = add(ArtifactTypeToken.valueOf(809L, false, "Customer Requirement - MS Word", AbstractSpecRequirement, MSWordTemplate));
   public static final ArtifactTypeToken Design = add(ArtifactTypeToken.valueOf(346L, false, "Design", MSWordTemplate));
   public static final ArtifactTypeToken DesignDescriptionMSWord = add(ArtifactTypeToken.valueOf(807L, false, "Design Description - MS Word", AbstractHeading, MSWordTemplate));
   public static final ArtifactTypeToken DirectSoftwareRequirement = add(ArtifactTypeToken.valueOf(22L, true, "Direct Software Requirement", AbstractSoftwareRequirement));
   public static final ArtifactTypeToken DocumentDescriptionMSWord = add(ArtifactTypeToken.valueOf(806L, false, "Document Description - MS Word", AbstractHeading, MSWordTemplate));
   public static final ArtifactTypeToken EnumeratedArtifact = add(ArtifactTypeToken.valueOf(4619295485563766003L, false, "Enumerated Artifact", Artifact));
   public static final ArtifactTypeToken Feature = add(ArtifactTypeToken.valueOf(87L, false, "Feature", Artifact));
   public static final ArtifactTypeToken FeatureDefinition = add(ArtifactTypeToken.valueOf(5849078290088170402L, false, "Feature Definition", GeneralData));
   public static final ArtifactTypeToken Folder = add(ArtifactTypeToken.valueOf(11L, false, "Folder", Artifact));
   public static final ArtifactTypeToken Function = add(ArtifactTypeToken.valueOf(34L, true, "Function", MSWordTemplate));
   public static final ArtifactTypeToken NativeArtifact = add(ArtifactTypeToken.valueOf(20L, true, "Native Artifact", Artifact));
   public static final ArtifactTypeToken GeneralDocument = add(ArtifactTypeToken.valueOf(14L, false, "General Document", NativeArtifact));
   public static final ArtifactTypeToken GitCommit = add(ArtifactTypeToken.valueOf(100L, false, "Git Commit", Artifact));
   public static final ArtifactTypeToken GitRepository = add(ArtifactTypeToken.valueOf(97L, false, "Git Repository", Artifact));
   public static final ArtifactTypeToken GlobalPreferences = add(ArtifactTypeToken.valueOf(3L, false, "Global Preferences", Artifact));
   public static final ArtifactTypeToken GroupArtifact = add(ArtifactTypeToken.valueOf(6L, false, "Group Artifact", Artifact));
   public static final ArtifactTypeToken HTMLArtifact = add(ArtifactTypeToken.valueOf(798L, false, "HTML Artifact", Artifact));
   public static final ArtifactTypeToken HardwareRequirement = add(ArtifactTypeToken.valueOf(33L, false, "Hardware Requirement", AbstractSpecRequirement, MSWordTemplate));
   public static final ArtifactTypeToken HeadingHTML = add(ArtifactTypeToken.valueOf(804L, false, "Heading - HTML", AbstractHeading, HTMLArtifact));
   public static final ArtifactTypeToken HeadingMSWord = add(ArtifactTypeToken.valueOf(56L, false, "Heading - MS Word", AbstractHeading, MSWordTemplate));
   public static final ArtifactTypeToken ImageArtifact = add(ArtifactTypeToken.valueOf(800L, false, "Image Artifact", Artifact));
   public static final ArtifactTypeToken ImplementationDetails = add(ArtifactTypeToken.valueOf(26L, false, "Implementation Details", AbstractImplementationDetails));
   public static final ArtifactTypeToken ImplementationDetailsDataDefinition = add(ArtifactTypeToken.valueOf(279578L, false, "Implementation Details Data Definition", ImplementationDetails));
   public static final ArtifactTypeToken ImplementationDetailsDrawing = add(ArtifactTypeToken.valueOf(209690L, false, "Implementation Details Drawing", ImplementationDetails));
   public static final ArtifactTypeToken ImplementationDetailsFunction = add(ArtifactTypeToken.valueOf(139802L, false, "Implementation Details Function", ImplementationDetails));
   public static final ArtifactTypeToken PlainText = add(ArtifactTypeToken.valueOf(784L, false, "Plain Text", Artifact));
   public static final ArtifactTypeToken ImplementationDetailsPlainText = add(ArtifactTypeToken.valueOf(638269899L, false, "Implementation Details Plain Text", AbstractImplementationDetails, PlainText));
   public static final ArtifactTypeToken ImplementationDetailsProcedure = add(ArtifactTypeToken.valueOf(69914L, false, "Implementation Details Procedure", ImplementationDetails));
   public static final ArtifactTypeToken IndirectSoftwareRequirement = add(ArtifactTypeToken.valueOf(25L, false, "Indirect Software Requirement", AbstractSoftwareRequirement, MSWordTemplate));
   public static final ArtifactTypeToken InterfaceRequirement = add(ArtifactTypeToken.valueOf(32L, false, "Interface Requirement", AbstractSpecRequirement, MSWordTemplate));
   public static final ArtifactTypeToken MSWordWholeDocument = add(ArtifactTypeToken.valueOf(18L, false, "MS Word Whole Document", MSWord));
   public static final ArtifactTypeToken MSWordStyles = add(ArtifactTypeToken.valueOf(2578L, false, "MS Word Styles", MSWordWholeDocument));
   public static final ArtifactTypeToken ModelDiagram = add(ArtifactTypeToken.valueOf(98L, false, "Model Diagram", Artifact));
   public static final ArtifactTypeToken OSEEApp = add(ArtifactTypeToken.valueOf(89L, false, "OSEE App", Artifact));
   public static final ArtifactTypeToken OseeApp = add(ArtifactTypeToken.valueOf(89L, false, "OSEE App", Artifact));
   public static final ArtifactTypeToken OseeTypeDefinition = add(ArtifactTypeToken.valueOf(60L, false, "Osee Type Definition", Artifact));
   public static final ArtifactTypeToken OseeTypeEnum = add(ArtifactTypeToken.valueOf(5447805027409642344L, false, "Osee Type Enum", EnumeratedArtifact));
   public static final ArtifactTypeToken RendererTemplate = add(ArtifactTypeToken.valueOf(9L, false, "Renderer Template", MSWordWholeDocument));
   public static final ArtifactTypeToken RootArtifact = add(ArtifactTypeToken.valueOf(10L, false, "Root Artifact", Artifact));
   public static final ArtifactTypeToken SafetyAssessment = add(ArtifactTypeToken.valueOf(59L, false, "Safety Assessment", Artifact));
   public static final ArtifactTypeToken SoftwareDesign = add(ArtifactTypeToken.valueOf(45L, false, "Software Design", Design));
   public static final ArtifactTypeToken SoftwareRequirement = add(ArtifactTypeToken.valueOf(24L, "Software Requirement"));
   public static final ArtifactTypeToken SoftwareRequirementDataDefinition = add(ArtifactTypeToken.valueOf(793L, false, "Software Requirement Data Definition", IndirectSoftwareRequirement));
   public static final ArtifactTypeToken SoftwareRequirementDrawing = add(ArtifactTypeToken.valueOf(29L, false, "Software Requirement Drawing", IndirectSoftwareRequirement));
   public static final ArtifactTypeToken SoftwareRequirementFunction = add(ArtifactTypeToken.valueOf(28L, false, "Software Requirement Function", IndirectSoftwareRequirement));
   public static final ArtifactTypeToken SoftwareRequirementHtml = add(ArtifactTypeToken.valueOf(42L, false, "Software Requirement - HTML", AbstractSoftwareRequirement, HTMLArtifact));
   public static final ArtifactTypeToken SoftwareRequirementPlainText = add(ArtifactTypeToken.valueOf(792L, "Software Requirement Plain Text"));
   public static final ArtifactTypeToken SoftwareRequirementProcedure = add(ArtifactTypeToken.valueOf(27L, false, "Software Requirement Procedure", IndirectSoftwareRequirement));
   public static final ArtifactTypeToken UserGroup = add(ArtifactTypeToken.valueOf(7L, "User Group"));
   public static final ArtifactTypeToken SubscriptionGroup = add(ArtifactTypeToken.valueOf(6753071794573299176L, false, "Subscription Group", UserGroup));
   public static final ArtifactTypeToken SubsystemDesign = add(ArtifactTypeToken.valueOf(43L, false, "Subsystem Design", Design));
   public static final ArtifactTypeToken SubsystemFunction = add(ArtifactTypeToken.valueOf(36L, false, "Subsystem Function", Function, SubsystemDesign));
   public static final ArtifactTypeToken SubsystemRequirementHTML = add(ArtifactTypeToken.valueOf(795L, false, "Subsystem Requirement - HTML", AbstractSubsystemRequirement, HTMLArtifact));
   public static final ArtifactTypeToken SubsystemRequirementMSWord = add(ArtifactTypeToken.valueOf(31L, false, "Subsystem Requirement - MS Word", AbstractSubsystemRequirement, MSWordTemplate));
   public static final ArtifactTypeToken SupportDocument = add(ArtifactTypeToken.valueOf(13L, false, "Support Document", MSWordTemplate));
   public static final ArtifactTypeToken Url = add(ArtifactTypeToken.valueOf(15L, false, "Url", Artifact));
   public static final ArtifactTypeToken SupportingContent = add(ArtifactTypeToken.valueOf(49L, false, "Supporting Content", Url));
   public static final ArtifactTypeToken SystemDesign = add(ArtifactTypeToken.valueOf(44L, false, "System Design", Design));
   public static final ArtifactTypeToken SystemFunction = add(ArtifactTypeToken.valueOf(35L, false, "System Function", Function, SystemDesign));
   public static final ArtifactTypeToken SystemRequirementHTML = add(ArtifactTypeToken.valueOf(794L, false, "System Requirement - HTML", AbstractSystemRequirement, HTMLArtifact));
   public static final ArtifactTypeToken SystemRequirementMSWord = add(ArtifactTypeToken.valueOf(30L, false, "System Requirement - MS Word", AbstractSystemRequirement, MSWordTemplate));
   public static final ArtifactTypeToken TestUnit = add(ArtifactTypeToken.valueOf(4L, true, "Test Unit", Artifact));
   public static final ArtifactTypeToken TestCase = add(ArtifactTypeToken.valueOf(82L, false,"Test Case", TestUnit));
   public static final ArtifactTypeToken TestInformationSheet = add(ArtifactTypeToken.valueOf(41L, false, "Test Information Sheet", MSWordTemplate, TestUnit));
   public static final ArtifactTypeToken TestPlanElement = add(ArtifactTypeToken.valueOf(37L, false, "Test Plan Element", MSWordTemplate));
   public static final ArtifactTypeToken TestProcedure = add(ArtifactTypeToken.valueOf(46L, false, "Test Procedure", TestUnit));
   public static final ArtifactTypeToken TestProcedureWML = add(ArtifactTypeToken.valueOf(47L, false, "Test Procedure WML", MSWordWholeDocument, TestProcedure));
   public static final ArtifactTypeToken TestResultNative = add(ArtifactTypeToken.valueOf(39L, false, "Test Result Native", AbstractTestResult, NativeArtifact));
   public static final ArtifactTypeToken TestResultWML = add(ArtifactTypeToken.valueOf(40L, false, "Test Result WML"));
   public static final ArtifactTypeToken TestRun = add(ArtifactTypeToken.valueOf(85L, false, "Test Run", AbstractTestResult, MSWordWholeDocument));
   public static final ArtifactTypeToken TestRunDisposition = add(ArtifactTypeToken.valueOf(84L, false, "Test Run Disposition", Artifact));
   public static final ArtifactTypeToken TestSupport = add(ArtifactTypeToken.valueOf(83L, false, "Test Support", TestUnit));
   public static final ArtifactTypeToken UniversalGroup = add(ArtifactTypeToken.valueOf(8L, false, "Universal Group", GroupArtifact));
   public static final ArtifactTypeToken User = add(ArtifactTypeToken.valueOf(5L, false, "User", Artifact));
   public static final ArtifactTypeToken WholeWord = add(ArtifactTypeToken.valueOf(18L, false, "MS Word Whole Document", MSWord));
   public static final ArtifactTypeToken WorkItemDefinition = add(ArtifactTypeToken.valueOf(50L, true, "Work Item Definition", Artifact));
   public static final ArtifactTypeToken WorkFlowDefinition = add(ArtifactTypeToken.valueOf(52L, false, "Work Flow Definition", WorkItemDefinition));
   public static final ArtifactTypeToken WorkPageDefinition = add(ArtifactTypeToken.valueOf(51L, false, "Work Page Definition", WorkItemDefinition));
   public static final ArtifactTypeToken WorkRuleDefinition = add(ArtifactTypeToken.valueOf(53L, false, "Work Rule Definition", WorkItemDefinition));
   public static final ArtifactTypeToken WorkWidgetDefinition = add(ArtifactTypeToken.valueOf(54L, false, "Work Widget Definition", WorkItemDefinition));
   public static final ArtifactTypeToken XViewerGlobalCustomization = add(ArtifactTypeToken.valueOf(55L, false, "XViewer Global Customization", Artifact));
// @formatter:on

   private CoreArtifactTypes() {
      // Constants
   }

   public static void putTokensInMap(Map<Long, ArtifactTypeToken> map) {
      for (ArtifactTypeToken token : tokens) {
         map.put(token.getId(), token);
      }
   }

   private static ArtifactTypeToken add(ArtifactTypeToken token) {
      tokens.add(token);
      return token;
   }
}