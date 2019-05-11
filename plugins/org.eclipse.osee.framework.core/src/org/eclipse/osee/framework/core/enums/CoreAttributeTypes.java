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
package org.eclipse.osee.framework.core.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactId;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AbstractAttributeType;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeBranchId;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeInputStream;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Roberto E. Escobar
 */
public final class CoreAttributeTypes {
   private static final List<? extends AttributeTypeToken> tokens = new ArrayList<>();

   // @formatter:off
   public static final AttributeTypeString AFHA = add(AttributeTypeToken.createString(1152921504606847139L, "AFHA", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString AccessContextId = add(AttributeTypeToken.createStringNoTag(1152921504606847102L, "Access Context Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean Active = add(AttributeTypeToken.createBooleanNoTag(1152921504606847065L, "Active", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Annotation = add(AttributeTypeToken.createString(1152921504606847094L, "Annotation", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeArtifactId ArtifactReference = add(AttributeTypeToken.createArtifactId(1153126013769613560L, "Artifact Reference", AttributeTypeToken.APPLICATION_HTTP, "Light-weight artifact reference"));
   public static final AttributeTypeArtifactId BaselinedBy = add(AttributeTypeToken.createArtifactIdNoTag(1152921504606847247L, "Baselined By", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate BaselinedTimestamp = add(AttributeTypeToken.createDateNoTag(1152921504606847244L, "Baselined Timestamp", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeBranchId BranchReference = add(AttributeTypeToken.createBranchId(1153126013769613563L, "Branch Reference", AttributeTypeToken.APPLICATION_HTTP, "Light-weight branch reference"));
   public static final AttributeTypeEnum CSCI = add(AttributeTypeToken.createEnum(1152921504606847136L, "CSCI", MediaType.TEXT_PLAIN, "", "CoreUnit:0", "Framework:1", "Interface:2", "Navigation:3", "Unspecified:4", "Visual:5"));
   public static final AttributeTypeString Category = add(AttributeTypeToken.createString(1152921504606847121L, "Category", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger CircuitBreakerID = add(AttributeTypeToken.createIntegerNoTag(188458869981238L, "Circuit Breaker ID", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString City = add(AttributeTypeToken.createString(1152921504606847068L, "City", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean CommonNALRequirement = add(AttributeTypeToken.createBooleanNoTag(1152921504606847105L, "Common NAL Requirement", MediaType.TEXT_PLAIN, "Requirement that is common to all NCORE (Networked Common Operating Real-time Environment) Application Layers"));
   public static final AttributeTypeString Company = add(AttributeTypeToken.createString(1152921504606847066L, "Company", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString CompanyTitle = add(AttributeTypeToken.createString(1152921504606847067L, "Company Title", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum Component = add(AttributeTypeToken.createEnum(1152921504606847125L, "Component", MediaType.TEXT_PLAIN, "", "Top level product component:0", "Unspecified:1"));
   public static final AttributeTypeString ContentURL = add(AttributeTypeToken.createString(1152921504606847100L, "Content URL", MediaType.WILDCARD, ""));
   public static final AttributeTypeString Country = add(AttributeTypeToken.createString(1152921504606847072L, "Country", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean CrewInterfaceRequirement = add(AttributeTypeToken.createBooleanNoTag(1152921504606847106L, "Crew Interface Requirement", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DataRightsBasis = add(AttributeTypeToken.createString(72057594037928276L, "Data Rights Basis", MediaType.TEXT_PLAIN, "The basis or rationale for the Data Rights Classification selected such as developed under program X"));
   public static final AttributeTypeEnum DataRightsClassification = add(AttributeTypeToken.createEnum(1152921504606847317L, "Data Rights Classification", MediaType.TEXT_PLAIN, "Restricted Rights:  Rights are retained by the company\n\nRestricted Rights Mixed:  contains some Restricted Rights that need separation of content with other rights\n\nOther:  does not contain content with Restricted Rights\n\nUnspecified: not yet specified", "Export Controlled ITAR:5", "Government Purpose Rights:1", "Limited Rights:4", "Proprietary:3", "Restricted Rights:0", "Unspecified:2"));
   public static final AttributeTypeBoolean DefaultGroup = add(AttributeTypeToken.createBoolean(1152921504606847086L, "Default Group", MediaType.TEXT_PLAIN, "Specifies whether to automatically add new users into this group"));
   public static final AttributeTypeString DefaultMailServer = add(AttributeTypeToken.createString(1152921504606847063L, "osee.config.Default Mail Server", MediaType.TEXT_PLAIN, "fully qualified name of the machine running the SMTP server which will be used by default for sending email"));
   public static final AttributeTypeString DefaultTrackingBranch = add(AttributeTypeToken.createString(1152921504606847709L, "Default Tracking Branch", MediaType.WILDCARD, ""));
   public static final AttributeTypeString DefaultValue = add(AttributeTypeToken.createString(2221435335730390044L, "Default Value", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Description = add(AttributeTypeToken.createString(1152921504606847090L, "Description", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean Developmental = add(AttributeTypeToken.createBooleanNoTag(1152921504606847137L, "Developmental", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Dictionary = add(AttributeTypeToken.createString(1152921504606847083L, "Dictionary", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DisplayText = add(AttributeTypeToken.createStringNoTag(188458869981237L, "Display Text", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum DoorsHierarchy = add(AttributeTypeToken.createEnum(1873562488122323009L, "Doors Hierarchy", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum DoorsID = add(AttributeTypeToken.createEnum(8243262488122393232L, "Doors ID", MediaType.TEXT_PLAIN, "External doors id for import support"));
   public static final AttributeTypeEnum DoorsModID = add(AttributeTypeToken.createEnum(5326122488147393161L, "Doors Mod ID", MediaType.TEXT_PLAIN, "Modified External doors id for import support"));
   public static final AttributeTypeString Effectivity = add(AttributeTypeToken.createStringNoTag(1152921504606847108L, "Effectivity", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Email = add(AttributeTypeToken.createString(1152921504606847082L, "Email", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString ExcludePath = add(AttributeTypeToken.createString(1152921504606847708L, "Exclude Path", MediaType.WILDCARD, ""));
   public static final AttributeTypeString Extension = add(AttributeTypeToken.createStringNoTag(1152921504606847064L, "Extension", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum FDAL = add(AttributeTypeToken.createEnumNoTag(8007959514939954596L, "FDAL", MediaType.TEXT_PLAIN, "Functional Development Assurance Level", "A:0", "B:1", "C:2", "D:3", "E:4", "Unspecified:5"));
   public static final AttributeTypeString FDALRationale = add(AttributeTypeToken.createStringNoTag(926274413268034710L, "FDAL Rationale", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString FavoriteBranch = add(AttributeTypeToken.createStringNoTag(1152921504606847074L, "Favorite Branch", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString FaxPhone = add(AttributeTypeToken.createStringNoTag(1152921504606847081L, "Fax Phone", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean FeatureMultivalued = add(AttributeTypeToken.createBoolean(3641431177461038717L, "Feature Multivalued", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum FeatureValueType = add(AttributeTypeToken.createEnum(31669009535111027L, "Feature Value Type", MediaType.TEXT_PLAIN, "", "Boolean:1", "Decimal:2", "Integer:3", "String:0"));
   public static final AttributeTypeString FileSystemPath = add(AttributeTypeToken.createString(1152921504606847707L, "File System Path", MediaType.WILDCARD, ""));
   public static final AttributeTypeString FunctionalCategory = add(AttributeTypeToken.createStringNoTag(1152921504606847871L, "Functional Category", MediaType.TEXT_PLAIN, "Functional Category in support of System Safety Report"));
   public static final AttributeTypeEnum FunctionalGrouping = add(AttributeTypeToken.createEnum(1741310787702764470L, "Functional Grouping", MediaType.TEXT_PLAIN, "", "Avionics:0", "Electrical:3", "Engine/Fuel/Hydraulics:2", "VMS/Flight Control:1"));
   public static final AttributeTypeEnum GFECFE = add(AttributeTypeToken.createEnum(1152921504606847144L, "GFE / CFE", MediaType.TEXT_PLAIN, "", "CFE:0", "GFE:1", "Unspecified:2"));
   public static final AttributeTypeString GeneralStringData = add(AttributeTypeToken.createStringNoTag(1152921504606847096L, "General String Data", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString GitChangeId = add(AttributeTypeToken.createString(1152921504606847702L, "Git Change-Id", MediaType.TEXT_PLAIN, "Change-Id embedded in Git commit message that is intended to be immutable even during rebase and amending the commit"));
   public static final AttributeTypeDate GitCommitAuthorDate = add(AttributeTypeToken.createDate(1152921504606847704L, "Git Commit Author Date", MediaType.TEXT_PLAIN, "when this commit was originally made"));
   public static final AttributeTypeString GitCommitMessage = add(AttributeTypeToken.createString(1152921504606847705L, "Git Commit Message", MediaType.TEXT_PLAIN, "Full message minus Change-Id"));
   public static final AttributeTypeString GitCommitSHA = add(AttributeTypeToken.createString(1152921504606847703L, "Git Commit SHA", MediaType.TEXT_PLAIN, "SHA-1 checksum of the Git commit's content and header"));
   public static final AttributeTypeString GraphitiDiagram = add(AttributeTypeToken.createStringNoTag(1152921504606847319L, "Graphiti Diagram", MediaType.TEXT_XML, "xml definition of an Eclipse Graphiti Diagram"));
   public static final AttributeTypeString HTMLContent = add(AttributeTypeToken.createString(1152921504606847869L, "HTML Content", MediaType.TEXT_HTML, "HTML format text must be a valid xhtml file"));
   public static final AttributeTypeString Hazard = add(AttributeTypeToken.createString(1152921504606847138L, "Hazard", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum HazardSeverity = add(AttributeTypeToken.createEnum(1152921504606847141L, "Hazard Severity", MediaType.TEXT_PLAIN, "", "Catastrophic, I:0", "Critical, II:2", "Major, III:3", "Marginal, III:4", "Minor, IV:5", "Negligible, IV:6", "No Effect, V:7", "Severe-Major, II:1", "Unspecified:8"));
   public static final AttributeTypeBoolean IAPlan = add(AttributeTypeToken.createBoolean(1253931514616857210L, "IA Plan", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum IDAL = add(AttributeTypeToken.createEnumNoTag(2612838829556295211L, "IDAL", MediaType.TEXT_PLAIN, "Item Development Assurance Level", "A:0", "B:1", "C:2", "D:3", "E:4", "Unspecified:5"));
   public static final AttributeTypeString IDALRationale = add(AttributeTypeToken.createStringNoTag(2517743638468399405L, "IDAL Rationale", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString IdValue = add(AttributeTypeToken.createString(72057896045641815L, "Id Value", MediaType.TEXT_PLAIN, "Key-Value attribute where key (attribute id) is supplied by framework and value is supplied by user."));
   public static final AttributeTypeInputStream ImageContent = add(AttributeTypeToken.createInputStreamNoTag(1152921504606847868L, "Image Content", MediaType.WILDCARD, "Binary Image content"));
   public static final AttributeTypeEnum LegacyDAL = add(AttributeTypeToken.createEnumNoTag(1152921504606847120L, "Legacy DAL", MediaType.TEXT_PLAIN, "Legacy Development Assurance Level (original DAL)", "A:0", "B:1", "C:2", "D:3", "E:4", "Unspecified:5"));
   public static final AttributeTypeString LegacyId = add(AttributeTypeToken.createStringNoTag(1152921504606847107L, "Legacy Id", MediaType.TEXT_PLAIN, "unique identifier from an external system"));
   public static final AttributeTypeString MaintainerText = add(AttributeTypeToken.createStringNoTag(188458874335285L, "Maintainer Text", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString MobilePhone = add(AttributeTypeToken.createStringNoTag(1152921504606847080L, "Mobile Phone", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Name = add(AttributeTypeToken.createString(1152921504606847088L, "Name", MediaType.TEXT_PLAIN, "Descriptive Name"));
   public static final AttributeTypeInputStream NativeContent = add(AttributeTypeToken.createInputStreamNoTag(1152921504606847097L, "Native Content", MediaType.APPLICATION_OCTET_STREAM, "content that will be edited by a native program"));
   public static final AttributeTypeString Notes = add(AttributeTypeToken.createString(1152921504606847085L, "Notes", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OseeAppDefinition = add(AttributeTypeToken.createStringNoTag(1152921504606847380L, "Osee App Definition", MediaType.APPLICATION_JSON, "Json that defines the parameters, action(s), and metadata of an OSEE Single Page App"));
   public static final AttributeTypeEnum PageOrientation = add(AttributeTypeToken.createEnum(1152921504606847091L, "Page Orientation", MediaType.TEXT_PLAIN, "Page Orientation: Landscape/Portrait", "Landscape:1", "Portrait:0"));
   public static final AttributeTypeString ParagraphNumber = add(AttributeTypeToken.createString(1152921504606847101L, "Paragraph Number", MediaType.TEXT_PLAIN, "This is the corresponding section number from the outline of document from which this artifact was imported"));
   public static final AttributeTypeEnum Partition = add(AttributeTypeToken.createEnum(1152921504606847111L, "Partition", MediaType.TEXT_PLAIN, "", "Communication:1", "Flight Control:2", "Graphics Handler:0", "Input/Output Processor:3", "Navigation:4", "Unspecified:5"));
   public static final AttributeTypeString Phone = add(AttributeTypeToken.createStringNoTag(1152921504606847079L, "Phone", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString PlainTextContent = add(AttributeTypeToken.createString(1152921504606847866L, "Plain Text Content", MediaType.TEXT_PLAIN, "plain text file"));
   public static final AttributeTypeBoolean PotentialSecurityImpact = add(AttributeTypeToken.createBoolean(1152921504606847109L, "Potential Security Impact", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean PublishInline = add(AttributeTypeToken.createBoolean(1152921504606847122L, "PublishInline", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum QualificationMethod = add(AttributeTypeToken.createEnum(1152921504606847113L, "Qualification Method", MediaType.TEXT_PLAIN, "Demonstration:  The operation of the CSCI, or a part of the CSCI, that relies on observable functional operation not requiring the use of instrumentation, special test equipment, or subsequent analysis.\n\nTest:  The operation of the CSCI, or a part of the CSCI, using instrumentation or other special test equipment to collect data for later analysis.\n\nAnalysis:  The processing of accumulated data obtained from other qualification methods.  Examples are reduction, interpretation, or extrapolation of test results.\n\nInspection:  The visual examination of CSCI code, documentation, etc.\n\nSpecial Qualification Methods:  Any special qualification methods for the CSCI, such as special tools, techniques, procedures, facilities, and acceptance limits.\n\nLegacy:  Requirement, design, or implementation has not changed since last qualification (use sparingly - Not to be used with functions implemented in internal software).\n\nUnspecified:  The qualification method has yet to be set.", "Analysis:2", "Demonstration:0", "Inspection:3", "Legacy:6", "Similarity:4", "Special Qualification:5", "Test:1", "Unspecified:7"));
   public static final AttributeTypeString RelationOrder = add(AttributeTypeToken.createStringNoTag(1152921504606847089L, "Relation Order", MediaType.TEXT_PLAIN, "Defines relation ordering information"));
   public static final AttributeTypeString RendererOptions = add(AttributeTypeToken.createString(904L, "Renderer Options", MediaType.APPLICATION_JSON, ""));
   public static final AttributeTypeString RepositoryURL = add(AttributeTypeToken.createString(1152921504606847700L, "Repository URL", MediaType.WILDCARD, ""));
   public static final AttributeTypeBoolean RequireConfirmation = add(AttributeTypeToken.createBooleanNoTag(188458869981239L, "Require Confirmation", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeInteger ReviewId = add(AttributeTypeToken.createIntegerNoTag(1152921504606847245L, "Review Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString ReviewStoryId = add(AttributeTypeToken.createString(1152921504606847246L, "Review Story Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString SFHA = add(AttributeTypeToken.createString(1152921504606847140L, "SFHA", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString SafetyImpact = add(AttributeTypeToken.createString(1684721504606847095L, "Safety Impact", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum SafetySeverity = add(AttributeTypeToken.createEnum(846763346271224762L, "Safety Severity", MediaType.TEXT_PLAIN, "", "(1) Catastrophic:0", "(2) Critical:1", "(3) Marginal:2", "(4) Negligible:3"));
   public static final AttributeTypeEnum SeverityCategory = add(AttributeTypeToken.createEnum(1152921504606847114L, "Severity Category", MediaType.TEXT_PLAIN, "Severity Category Classification", "I:0", "II:1", "III:2", "IV:3", "NH:4", "Unspecified:5"));
   public static final AttributeTypeEnum SoftwareControlCategory = add(AttributeTypeToken.createEnum(1958401980089733639L, "Software Control Category", MediaType.TEXT_PLAIN, "Software Control Category Classification", "1(AT):0", "2(SAT):1", "3(RFT):2", "4(IN):3", "5(NSI):4", "Unspecified:5"));
   public static final AttributeTypeString SoftwareControlCategoryRationale = add(AttributeTypeToken.createStringNoTag(750929222178534710L, "Software Control Category Rationale", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean SoftwareSafetyImpact = add(AttributeTypeToken.createBooleanNoTag(8318805403746485981L, "Software Safety Impact", MediaType.TEXT_PLAIN, "Software Safety Impact"));
   public static final AttributeTypeString StartPage = add(AttributeTypeToken.createStringNoTag(1152921504606847135L, "osee.wi.Start Page", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString State = add(AttributeTypeToken.createString(1152921504606847070L, "State", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString StaticId = add(AttributeTypeToken.createString(1152921504606847095L, "Static Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Street = add(AttributeTypeToken.createString(1152921504606847069L, "Street", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString SubjectMatterExpert = add(AttributeTypeToken.createString(72057594037928275L, "Subject Matter Expert", MediaType.TEXT_PLAIN, "Name of the Subject Matter Expert"));
   public static final AttributeTypeEnum Subsystem = add(AttributeTypeToken.createEnum(1152921504606847112L, "Subsystem", MediaType.TEXT_PLAIN, "", "Chassis:3", "Communications:4", "Controls:7", "Data_Management:5", "Electrical:6", "Hydraulics:8", "Lighting:9", "Navigation:10", "Propulsion:11", "Robot_API:0", "Robot_Survivability_Equipment:1", "Robot_Systems_Management:2", "Unknown:12", "Unspecified:13"));
   public static final AttributeTypeEnum TISTestCategory = add(AttributeTypeToken.createEnumNoTag(1152921504606847119L, "TIS Test Category", MediaType.TEXT_PLAIN, "TIS Test Category", "DEV:1", "SPEC_COMP:0", "USG:2"));
   public static final AttributeTypeString TISTestNumber = add(AttributeTypeToken.createStringNoTag(1152921504606847116L, "TIS Test Number", MediaType.TEXT_PLAIN, "Test Number"));
   public static final AttributeTypeEnum TISTestType = add(AttributeTypeToken.createEnumNoTag(1152921504606847118L, "TIS Test Type", MediaType.TEXT_PLAIN, "TIS Test Type", "In Operation:1", "Stationary Vehicle:0"));
   public static final AttributeTypeBoolean TechnicalPerformanceParameter = add(AttributeTypeToken.createBooleanNoTag(1152921504606847123L, "Technical Performance Parameter", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString TemplateMatchCriteria = add(AttributeTypeToken.createString(1152921504606847087L, "Template Match Criteria", MediaType.TEXT_PLAIN, "Criteria that determines what template is selected ie: 'Render Artifact PresentationType Option'"));
   public static final AttributeTypeEnum TestProcedureStatus = add(AttributeTypeToken.createEnumNoTag(1152921504606847075L, "Test Procedure Status", MediaType.TEXT_PLAIN, "", "Completed -- Analysis in Work:1", "Completed -- Passed:2", "Completed -- With Issues:3", "Completed -- With Issues Resolved:4", "Not Performed:0", "Partially Complete:5"));
   public static final AttributeTypeString TestScriptGUID = add(AttributeTypeToken.createStringNoTag(1152921504606847301L, "Test Script GUID", MediaType.TEXT_PLAIN, "Test Case GUID"));
   public static final AttributeTypeString Transition = add(AttributeTypeToken.createStringNoTag(1152921504606847133L, "osee.wi.Transition", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString UriGeneralStringData = add(AttributeTypeToken.createStringNoTag(1152921504606847381L, "Uri General String Data", AttributeTypeToken.TEXT_URI_LIST, ""));
   public static final AttributeTypeArtifactId UserArtifactId = add(AttributeTypeToken.createArtifactIdNoTag(1152921504606847701L, "User Artifact Id", MediaType.TEXT_PLAIN, "Artifact id of an artifact of type User"));
   public static final AttributeTypeString UserId = add(AttributeTypeToken.createString(1152921504606847073L, "User Id", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString UserSettings = add(AttributeTypeToken.createStringNoTag(1152921504606847076L, "User Settings", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Value = add(AttributeTypeToken.createString(861995499338466438L, "Value", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString VerificationAcceptanceCriteria = add(AttributeTypeToken.createStringNoTag(1152921504606847117L, "Verification Acceptance Criteria", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum VerificationEvent = add(AttributeTypeToken.createEnumNoTag(1152921504606847124L, "Verification Event", MediaType.TEXT_PLAIN, "", "Unspecified:0"));
   public static final AttributeTypeEnum VerificationLevel = add(AttributeTypeToken.createEnum(1152921504606847115L, "Verification Level", MediaType.TEXT_PLAIN, "", "Component:2", "N/A:4", "Subsystem:1", "System:0", "Unspecified:3"));
   public static final AttributeTypeString WebPreferences = add(AttributeTypeToken.createStringNoTag(1152921504606847386L, "Web Preferences", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Website = add(AttributeTypeToken.createString(1152921504606847084L, "Website", AttributeTypeToken.APPLICATION_HTTP, ""));
   public static final AttributeTypeString WholeWordContent = add(AttributeTypeToken.createString(1152921504606847099L, "Whole Word Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema"));
   public static final AttributeTypeString WordOleData = add(AttributeTypeToken.createStringNoTag(1152921504606847092L, "Word Ole Data", AttributeTypeToken.APPLICATION_MSWORD, "Word Ole Data"));
   public static final AttributeTypeString WordTemplateContent = add(AttributeTypeToken.createString(1152921504606847098L, "Word Template Content", AttributeTypeToken.APPLICATION_MSWORD, "value must comply with WordML xml schema"));
   public static final AttributeTypeString WorkData = add(AttributeTypeToken.createStringNoTag(1152921504606847126L, "osee.wi.Work Data", MediaType.TEXT_XML, ""));
   public static final AttributeTypeString XViewerCustomization = add(AttributeTypeToken.createString(1152921504606847077L, "XViewer Customization", MediaType.WILDCARD, ""));
   public static final AttributeTypeString XViewerDefaults = add(AttributeTypeToken.createStringNoTag(1152921504606847078L, "XViewer Defaults", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Zip = add(AttributeTypeToken.createString(1152921504606847071L, "Zip", MediaType.WILDCARD, ""));
   // @formatter:on

   private CoreAttributeTypes() {
      // Constants
   }

   private static <T extends AbstractAttributeType<?>> T add(T attributeType) {
      tokens.add(attributeType);
      return attributeType;
   }

   public static void putTokensInMap(Map<Long, AttributeTypeToken> map) {
      for (AttributeTypeToken token : tokens) {
         map.put(token.getId(), token);
      }
   }
}