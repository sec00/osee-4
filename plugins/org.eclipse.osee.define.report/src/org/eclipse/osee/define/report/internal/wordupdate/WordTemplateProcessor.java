/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.define.report.internal.wordupdate;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Partition;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SeverityCategory;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.define.report.api.DataRightsOperations;
import org.eclipse.osee.define.report.api.MSWordOperations;
import org.eclipse.osee.define.report.api.Renderer;
import org.eclipse.osee.define.report.api.RendererClassRegistry;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.RendererUtil;
import org.eclipse.osee.framework.core.util.ReportConstants;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.renderer.AttributeElement;
import org.eclipse.osee.orcs.renderer.MetadataElement;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.swt.program.Program;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Robert A. Fisher
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @link WordTemplateProcessorTest
 */
public class WordTemplateProcessor {

   private static final String ARTIFACT = "Artifact";
   private static final String ARTIFACT_TYPE = "Artifact Type";
   private static final Object ARTIFACT_ID = "Artifact Id";
   private static final String APPLICABILITY = "Applicability";
   private static final String INSERT_LINK = "INSERT_LINK_HERE";
   private static final String INSERT_ARTIFACT_HERE = "INSERT_ARTIFACT_HERE";
   private static final String NESTED_TEMPLATE = "NestedTemplate";
   public static final String PGNUMTYPE_START_1 = "<w:pgNumType [^>]*w:start=\"1\"/>";
   public static final String STYLES = "<w:lists>.*?</w:lists><w:styles>.*?</w:styles>";

   private static final Pattern headElementsPattern =
      Pattern.compile("(" + INSERT_ARTIFACT_HERE + ")" + "|" + INSERT_LINK,
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final Program wordApp = Program.findProgram("doc");

   private String slaveTemplate;
   private String slaveTemplateOptions;
   private String slaveTemplateStyles;

   private String elementType;
   private String overrideClassification;
   private BranchId branch;

   //Outlining Options
   private AttributeTypeId headingAttributeType;
   private boolean outlining;
   private boolean recurseChildren;
   private String outlineNumber;
   private String artifactName;

   //Attribute Options
   private String attributeLabel;
   private String attributeType;
   private String formatPre;
   private String formatPost;

   //Metadata Options
   private String metadataType;
   private String metadataLabel;
   private String metadataFormat;

   //Nested Template Options
   private String outlineType;
   private String sectionNumber;
   private String subDocName;
   private String key;
   private String value;
   private int nestedCount;

   private final List<AttributeElement> attributeElements = new LinkedList<>();
   private final List<MetadataElement> metadataElements = new LinkedList<>();
   final List<ArtifactId> nonTemplateArtifacts = new LinkedList<>();
   private final Set<String> ignoreAttributeExtensions = new HashSet<>();
   private final Set<ArtifactId> processedArtifacts = new HashSet<>();

   private boolean excludeFolders;
   private CharSequence paragraphNumber = null;
   private IArtifactType[] excludeArtifactTypes;
   private HashMap<ApplicabilityId, ApplicabilityToken> applicabilityTokens;
   private Set<ArtifactId> artifactsToExclude;

   // Needed as global
   private final OrcsApi orcsApi;
   private final DataRightsOperations dataRightsOperations;
   private final MSWordOperations msWordOperations;
   private final QueryFactory queryFactory;
   private Map<RendererOption, Object> rendererOptions;
   private final UserId user;
   private final String sessionId;

   public WordTemplateProcessor(OrcsApi orcsApi, DataRightsOperations dataRightsOperations, UserId user, String sessionId, MSWordOperations msWordOperations) {
      loadIgnoreAttributeExtensions();
      artifactsToExclude = new HashSet<>();
      this.user = user;
      this.sessionId = sessionId;
      this.orcsApi = orcsApi;
      this.dataRightsOperations = dataRightsOperations;
      this.msWordOperations = msWordOperations;
      queryFactory = orcsApi.getQueryFactory();
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts Only used by Publish SRS
    */
   public InputStream applyNestedTemplates(ArtifactId masterTemplateArtifact, ArtifactId slaveTemplateArtifact, List<ArtifactId> artifacts, BranchId branch, Map<RendererOption, Object> rendererOptions) {
      this.rendererOptions = rendererOptions;
      nestedCount = 0;
      ArtifactReadable masterTemplateReadable =
         queryFactory.fromBranch(CoreBranches.COMMON).andId(masterTemplateArtifact).getResults().getOneOrNull();
      String masterTemplate = masterTemplateReadable.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");

      String masterTemplateOptions =
         masterTemplateReadable.getSoleAttributeValue(CoreAttributeTypes.RendererOptions, "");

      slaveTemplate = "";
      slaveTemplateOptions = "";

      this.rendererOptions.put(RendererOption.TEMPLATE_OPTION, masterTemplateArtifact);

      slaveTemplateStyles = "";
      if (slaveTemplateArtifact != null) {
         ArtifactReadable slaveTemplateReadable =
            queryFactory.fromBranch(CoreBranches.COMMON).andId(masterTemplateArtifact).getResults().getOneOrNull();

         this.rendererOptions.put(RendererOption.TEMPLATE_OPTION, slaveTemplateArtifact);
         slaveTemplate = slaveTemplateReadable.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
         slaveTemplateOptions = slaveTemplateReadable.getSoleAttributeValue(CoreAttributeTypes.RendererOptions, "");

         ArtifactReadable slaveTemplateRelatedArtifacts =
            slaveTemplateReadable.getRelated(CoreRelationTypes.SupportingInfo_SupportingInfo).getAtMostOneOrNull();

         if (slaveTemplateRelatedArtifacts != null) {
            slaveTemplateStyles +=
               slaveTemplateRelatedArtifacts.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
         }

      }

      try {
         attributeElements.clear();
         metadataElements.clear();
         JSONObject jsonObject = new JSONObject(masterTemplateOptions);
         elementType = jsonObject.getString("ElementType");
         if (elementType.equals(ARTIFACT)) {
            parseAttributeOptions(masterTemplateOptions);
            parseMetadataOptions(masterTemplateOptions);
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      // Need to check if all attributes will be published.  If so set the AllAttributes option.
      // Assumes that all (*) will not be used when other attributes are specified
      this.rendererOptions.put(RendererOption.ALL_ATTRIBUTES, false);
      if (attributeElements.size() == 1) {
         String attributeName = attributeElements.get(0).getAttributeName();
         if (attributeName.equals("*")) {
            rendererOptions.put(RendererOption.ALL_ATTRIBUTES, true);

         }
      }
      ArtifactReadable masterTemplateRelatedArtifacts =
         masterTemplateReadable.getRelated(CoreRelationTypes.SupportingInfo_SupportingInfo).getOneOrNull();

      String masterTemplateStyles = "";

      if (masterTemplateRelatedArtifacts != null) {
         masterTemplateStyles +=
            masterTemplateRelatedArtifacts.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");

      }

      getExcludeArtifactTypes();

      IFile file = RendererUtil.getRenderFile(COMMON, PREVIEW, "/", masterTemplateReadable.getName(), ".xml");
      this.rendererOptions.put(RendererOption.RESULT_PATH_RETURN, file.getLocation().toOSString());

      List<ArtifactReadable> artifactReadables =
         queryFactory.fromBranch(branch).andIds(artifacts).getResults().getList();

      //TODO: return input stream
      //      AIFile.writeToFile(file, applyTemplate(artifactReadables, masterTemplate, masterTemplateOptions,
      //         masterTemplateStyles, file.getParent(), null, null, PREVIEW, this.rendererOptions));

      boolean noDisplay = this.rendererOptions.get(
         RendererOption.NO_DISPLAY) == null ? false : (boolean) this.rendererOptions.get(RendererOption.NO_DISPLAY);
      boolean pubDiff = this.rendererOptions.get(
         RendererOption.PUBLISH_DIFF) == null ? false : (boolean) this.rendererOptions.get(RendererOption.PUBLISH_DIFF);

      if (!noDisplay && !pubDiff) {
         RendererUtil.ensureFilenameLimit(file);
         wordApp.execute(file.getLocation().toFile().getAbsolutePath());
      }

      return null;
   }

   private IArtifactType[] getExcludeArtifactTypes() {
      excludeArtifactTypes = null;
      if (rendererOptions.get(RendererOption.EXCLUDE_ARTIFACT_TYPES) != null) {
         Object o = rendererOptions.get(RendererOption.EXCLUDE_ARTIFACT_TYPES);
         if (o instanceof Collection<?>) {
            Collection<?> coll = (Collection<?>) o;
            excludeArtifactTypes = new IArtifactType[coll.size()];
            int i = 0;
            Iterator<?> iterator = coll.iterator();
            while (iterator.hasNext()) {
               Object next = iterator.next();
               if (next instanceof IArtifactType) {
                  excludeArtifactTypes[i] = (IArtifactType) next;
                  i++;
               }
            }

         }
      }

      return excludeArtifactTypes;
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    *
    * @param folder = null when not using an extension template
    * @param outlineNumber if null will find based on first artifact
    */

   public InputStream applyTemplate(List<ArtifactReadable> artifacts, String templateContent, String templateOptions, String templateStyles, IContainer folder, String outlineNumber, String outlineType, PresentationType presentationType, Map<RendererOption, Object> rendererOptions) {
      this.rendererOptions = rendererOptions;
      excludeFolders =
         rendererOptions.get(RendererOption.EXCLUDE_FOLDERS) == null ? false : (boolean) rendererOptions.get(
            RendererOption.EXCLUDE_FOLDERS);
      ArtifactId view = (ArtifactId) rendererOptions.get(RendererOption.VIEW);

      if (!artifacts.isEmpty()) {
         branch = artifacts.get(0).getBranch();

         applicabilityTokens = new HashMap<>();

         Collection<ApplicabilityToken> appTokens =
            orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).values();

         for (ApplicabilityToken token : appTokens) {
            applicabilityTokens.put(token, token);
         }
      } else {
         branch = BranchId.SENTINEL;
      }

      artifactsToExclude = getExcludedArtifacts(artifacts, view == null ? ArtifactId.SENTINEL : view);

      WordMLProducer wordMl = null;
      CharBackedInputStream charBak = null;

      try {
         charBak = new CharBackedInputStream();
         wordMl = new WordMLProducer(charBak);

         templateContent = templateContent.replaceAll(PGNUMTYPE_START_1, "");

         if (!templateStyles.isEmpty()) {
            templateContent = templateContent.replaceAll(STYLES, templateStyles);
         }

         this.outlineNumber = outlineNumber == null ? peekAtFirstArtifactToGetParagraphNumber(templateContent, null,
            artifacts) : outlineNumber;
         templateContent = wordMl.setHeadingNumbers(this.outlineNumber, templateContent, outlineType);

         Matcher matcher = headElementsPattern.matcher(templateContent);

         int lastEndIndex = 0;
         while (matcher.find()) {
            wordMl.addWordMl(templateContent.substring(lastEndIndex, matcher.start()));
            lastEndIndex = matcher.end();

            JSONObject jsonObject = new JSONObject(templateOptions);
            elementType = jsonObject.getString("ElementType");

            if (elementType.equals(ARTIFACT)) {
               parseOutliningOptions(templateOptions);

               if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() == 1) {
                  // for single edit override outlining options
                  outlining = false;
               }
               processArtifactSet(templateOptions, artifacts, wordMl, outlineType, presentationType,
                  (ArtifactId) rendererOptions.get(RendererOption.VIEW));

            } else if (elementType.equals(NESTED_TEMPLATE)) {
               parseNestedTemplateOptions(templateOptions, folder, wordMl, presentationType);
            } else {
               throw new OseeArgumentException("Invalid input [%s]", "");
            }
         }

         String endOfTemplate = templateContent.substring(lastEndIndex);
         // Write out the last of the template
         wordMl.addWordMl(updateFooter(endOfTemplate));

         //         displayNonTemplateArtifacts(nonTemplateArtifacts,
         //            "Only artifacts of type Word Template Content are supported in this case.");

      } catch (CharacterCodingException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      return charBak;
   }

   private void parseNestedTemplateOptions(String templateOptions, IContainer folder, WordMLProducer wordMl, PresentationType presentationType) {
      try {
         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray nestedTemplateOptions = jsonObject.getJSONArray("NestedTemplates");
         JSONObject options = null;

         if (nestedCount < nestedTemplateOptions.length()) {
            options = nestedTemplateOptions.getJSONObject(nestedCount);
            nestedCount++;
            outlineType = options.getString("OutlineType");
            if (outlineType.isEmpty()) {
               outlineType = null;
            }
            sectionNumber = options.getString("SectionNumber");
            subDocName = options.getString("SubDocName");
            key = options.getString("Key");
            // rendererOption is either ID or NAME
            RendererOption rendererOption = RendererOption.valueOf(key.toUpperCase());
            value = options.getString("Value");

            rendererOptions.put(rendererOption, value);

            String artifactName = (String) rendererOptions.get("Name");
            String artifactId = (String) rendererOptions.get("Id");
            String orcsQuery = (String) rendererOptions.get("OrcsQuery");
            BranchId branch = (BranchId) rendererOptions.get("Branch");
            List<ArtifactReadable> artifacts = null;

            if (Strings.isValid(artifactId)) {
               artifacts = queryFactory.fromBranch(branch).andId(
                  ArtifactId.valueOf(Long.valueOf(artifactId))).excludeDeleted().getResults().getList();
            } else if (Strings.isValid(artifactName)) {
               artifacts =
                  queryFactory.fromBranch(branch).andNameEquals(artifactName).excludeDeleted().getResults().getList();

            } else if (Strings.isValid(orcsQuery)) {
               StringWriter writer = new StringWriter();
               ScriptContext context = new SimpleScriptContext();
               context.setWriter(writer);
               context.setErrorWriter(writer);
               try {
                  orcsApi.getScriptEngine().eval(orcsQuery, context);

                  artifacts = parseOrcsQueryResult(writer.toString(), branch);
               } catch (ScriptException ex) {
                  throw new OseeCoreException(ex);
               }
            }

            String subDocFileName = subDocName + ".xml";
            boolean isDiff =
               rendererOptions.get(RendererOption.PUBLISH_DIFF) == null ? false : (boolean) rendererOptions.get(
                  RendererOption.PUBLISH_DIFF);

            if (isDiff) {
               // TODO: keep on client
               //               WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);
               //               templateFileDiffer.generateFileDifferences(artifacts, "/results/" + subDocFileName, sectionNumber,
               //                  outlineType, recurseChildren);
            } else {
               // TODO: return inputstream
               //          IFile file = folder.getFile(new Path(subDocFileName));
               //               AIFile.writeToFile(file, applyTemplate(artifacts, slaveTemplate, slaveTemplateOptions,
               //                  slaveTemplateStyles, folder, sectionNumber, outlineType, presentationType, rendererOptions));
            }
            wordMl.createHyperLinkDoc(subDocFileName);
         }

      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private List<ArtifactReadable> parseOrcsQueryResult(String result, BranchId branch) {
      ArrayList<ArtifactReadable> artifacts = new ArrayList<>();
      try {
         JSONObject jsonObject = new JSONObject(result);
         JSONArray results = jsonObject.getJSONArray("results");
         if (results.length() >= 1) {
            JSONArray artifactIds = results.getJSONObject(0).getJSONArray("artifacts");
            JSONObject id = null;
            for (int i = 0; i < artifactIds.length(); i++) {
               id = artifactIds.getJSONObject(i);
               Long artifactId = id.getLong("id");
               ArtifactReadable artifact = queryFactory.fromBranch(branch).andId(
                  ArtifactId.valueOf(artifactId)).excludeDeleted().getResults().getOneOrNull();

               artifacts.add(artifact);
            }
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      return artifacts;
   }

   private void parseAttributeOptions(String templateOptions) {
      try {
         attributeElements.clear();

         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray attributeOptions = jsonObject.getJSONArray("AttributeOptions");
         JSONObject options = null;

         for (int i = 0; i < attributeOptions.length(); i++) {
            options = attributeOptions.getJSONObject(i);
            attributeType = options.getString("AttrType");
            attributeLabel = options.getString("Label");
            formatPre = options.getString("FormatPre");
            formatPost = options.getString("FormatPost");

            AttributeElement attrElement = new AttributeElement();
            AttributeTypeId attributeTypeId = orcsApi.getOrcsTypes().getAttributeTypes().getByName(attributeType);

            if (attributeType.equals("*") || attributeTypeId != null) {
               attrElement.setElements(attributeType, attributeLabel, formatPre, formatPost);
               attributeElements.add(attrElement);
            }

         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void parseOutliningOptions(String templateOptions) {
      try {
         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray optionsArray = jsonObject.getJSONArray("OutliningOptions");
         JSONObject options = optionsArray.getJSONObject(0);

         outlining = options.getBoolean("Outlining");
         recurseChildren = options.getBoolean("RecurseChildren");
         String headingAttrType = options.getString("HeadingAttributeType");
         headingAttributeType = orcsApi.getOrcsTypes().getAttributeTypes().getByName(headingAttrType);

         artifactName = options.getString("ArtifactName");

      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void parseMetadataOptions(String metadataOptions) {
      try {
         JSONObject jsonObject = new JSONObject(metadataOptions);

         if (!jsonObject.has("MetadataOptions")) {
            return;
         }

         JSONArray optionsArray = jsonObject.getJSONArray("MetadataOptions");
         JSONObject options = optionsArray.getJSONObject(0);

         metadataType = options.getString("Type");
         metadataFormat = options.getString("Format");
         metadataLabel = options.getString("Label");

         MetadataElement metadataElement = new MetadataElement();

         metadataElement.setElements(metadataType, metadataFormat, metadataLabel);
         metadataElements.add(metadataElement);

      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private String updateFooter(String endOfTemplate) {
      // footer cleanup
      endOfTemplate = endOfTemplate.replaceAll(ReportConstants.FTR, "");
      endOfTemplate =
         endOfTemplate.replaceFirst(ReportConstants.PAGE_SZ, ReportConstants.CONTINUOUS + ReportConstants.PG_SZ);
      return endOfTemplate;
   }

   protected String peekAtFirstArtifactToGetParagraphNumber(String template, String nextParagraphNumber, List<ArtifactReadable> artifacts) {
      String startParagraphNumber = "1";
      if (artifacts != null) {
         Matcher matcher = headElementsPattern.matcher(template);

         if (matcher.find()) {
            String elementType = matcher.group(0);

            if (elementType != null && elementType.equals(INSERT_ARTIFACT_HERE) && !artifacts.isEmpty()) {
               ArtifactReadable artifact = artifacts.iterator().next();
               if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
                  String paragraphNum = artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
                  if (Strings.isValid(paragraphNum)) {
                     startParagraphNumber = paragraphNum;
                  }
               }
            }
         }
      }
      return startParagraphNumber;
   }

   private void processArtifactSet(String templateOptions, List<ArtifactReadable> artifacts, WordMLProducer wordMl, String outlineType, PresentationType presentationType, ArtifactId view) {
      nonTemplateArtifacts.clear();
      rendererOptions.put(RendererOption.VIEW, view == null ? ArtifactId.SENTINEL : view);

      if (Strings.isValid(outlineNumber)) {
         wordMl.setNextParagraphNumberTo(outlineNumber);
      }

      // Don't extract the settings from the template if already done.
      if (attributeElements.isEmpty()) {
         parseAttributeOptions(templateOptions);
      }

      if (metadataElements.isEmpty()) {
         parseMetadataOptions(templateOptions);
      }

      boolean diff = rendererOptions.get(RendererOption.PUBLISH_DIFF) == null ? false : (boolean) rendererOptions.get(
         RendererOption.PUBLISH_DIFF);

      if (diff) {
         // TODO: keep on client
         //    WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(null, null);
         //         templateFileDiffer.generateFileDifferences(artifacts, "/results/", outlineNumber, outlineType,
         //            recurseChildren);
      } else {
         List<ArtifactId> allArtifacts = new ArrayList<>();
         boolean recurseOnLoad =
            rendererOptions.get(RendererOption.RECURSE_ON_LOAD) == null ? false : (boolean) rendererOptions.get(
               RendererOption.RECURSE_ON_LOAD);
         boolean origPublishDiff =
            rendererOptions.get(RendererOption.ORIG_PUBLISH_AS_DIFF) == null ? false : (boolean) rendererOptions.get(
               RendererOption.ORIG_PUBLISH_AS_DIFF);

         if (recurseChildren || (recurseOnLoad && !origPublishDiff)) {
            for (ArtifactReadable art : artifacts) {

               allArtifacts.add(art);
               // Is this valid for isHistorical?
               TransactionId tx = art.getTransaction();
               TransactionId modTx = art.getLastModifiedTransaction();
               if (!tx.isLessThan(modTx)) {
                  allArtifacts.addAll(art.getDescendants());
               }
            }
         } else {
            allArtifacts.addAll(artifacts);
         }

         DataRightResult dataRightResult =
            dataRightsOperations.getDataRights(allArtifacts, branch, overrideClassification);

         for (ArtifactReadable artifact : artifacts) {
            processObjectArtifact(artifact, wordMl, outlineType, presentationType, dataRightResult);
         }
      }
      // maintain a list of artifacts that have been processed so we do not
      // have duplicates.
      processedArtifacts.clear();
   }

   private Set<ArtifactId> getExcludedArtifacts(List<ArtifactReadable> artifacts, ArtifactId view) {
      if (artifacts != null && !artifacts.isEmpty() && view.isValid()) {
         return orcsApi.getQueryFactory().applicabilityQuery().getExcludedArtifacts(branch, view);
      }

      return new HashSet<>();
   }

   private void processObjectArtifact(ArtifactReadable artifact, WordMLProducer wordMl, String outlineType, PresentationType presentationType, DataRightResult dataRightResult) {
      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.WholeWordContent) && !artifact.isAttributeTypeValid(
         CoreAttributeTypes.NativeContent)) {
         // If the artifact has not been processed
         if (!processedArtifacts.contains(artifact)) {

            boolean ignoreArtifact = excludeFolders && artifact.isOfType(
               CoreArtifactTypes.Folder) && !artifactsToExclude.contains(artifact.getId());

            boolean ignoreArtType = excludeArtifactTypes != null && artifact.isOfType(excludeArtifactTypes);
            boolean publishInline = artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false);
            boolean startedSection = false;
            boolean templateOnly =
               rendererOptions.get(RendererOption.TEMPLATE_ONLY) == null ? false : (boolean) rendererOptions.get(
                  RendererOption.TEMPLATE_ONLY);
            boolean includeUUIDs =
               rendererOptions.get(RendererOption.INCLUDE_UUIDS) == null ? false : (boolean) rendererOptions.get(
                  RendererOption.INCLUDE_UUIDS);

            if (!ignoreArtifact && !ignoreArtType) {
               if (outlining && !templateOnly) {
                  String headingText = artifact.getSoleAttributeValue(headingAttributeType, "");

                  if (includeUUIDs) {
                     String UUIDtext = String.format(" <UUID = %s>", artifact.getId());
                     headingText = headingText.concat(UUIDtext);
                  }

                  boolean mergeTag =
                     rendererOptions.get(RendererOption.ADD_MERGE_TAG) == null ? false : (Boolean) rendererOptions.get(
                        RendererOption.ADD_MERGE_TAG);

                  if (mergeTag) {
                     headingText = headingText.concat(" [MERGED]");
                  }

                  if (!publishInline && !templateOnly) {
                     paragraphNumber = wordMl.startOutlineSubSection("Times New Roman", headingText, outlineType);
                     startedSection = true;
                  }

                  if (paragraphNumber == null) {
                     paragraphNumber = wordMl.startOutlineSubSection();
                     startedSection = true;
                  }

                  boolean updateParagraphNumbers = rendererOptions.get(
                     RendererOption.UPDATE_PARAGRAPH_NUMBERS) == null ? false : (boolean) rendererOptions.get(
                        RendererOption.UPDATE_PARAGRAPH_NUMBERS);

                  if (updateParagraphNumbers && !publishInline) {
                     if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
                        TransactionBuilder tx =
                           orcsApi.getTransactionFactory().createTransaction(branch, user, "Update Paragraph Number");
                        tx.setSoleAttributeValue(artifact, CoreAttributeTypes.ParagraphNumber,
                           paragraphNumber.toString());
                        tx.commit();
                     }
                  }
               }

               String pageType = artifact.getSoleAttributeAsString(CoreAttributeTypes.PageType, "Portrait");
               PageOrientation orientation = PageOrientation.fromString(pageType);
               String footer = dataRightResult.getContent(artifact.getId(), orientation);

               processMetadata(artifact, wordMl);

               processAttributes(artifact, wordMl, presentationType, publishInline, footer);
            }

            // Check for option that may have been set from Publish with Diff BLAM to recurse

            boolean recurse =
               rendererOptions.get(RendererOption.RECURSE_ON_LOAD) == null ? false : (boolean) rendererOptions.get(
                  RendererOption.RECURSE_ON_LOAD);
            boolean origDiff =
               rendererOptions.get(RendererOption.ORIG_PUBLISH_AS_DIFF) == null ? false : (boolean) rendererOptions.get(
                  RendererOption.ORIG_PUBLISH_AS_DIFF);

            if (recurseChildren && !recurse || (recurse && !origDiff)) {
               for (ArtifactReadable childArtifact : artifact.getChildren()) {
                  processObjectArtifact(childArtifact, wordMl, outlineType, presentationType, dataRightResult);
               }
            }

            if (startedSection) {
               wordMl.endOutlineSubSection();
            }
            processedArtifacts.add(artifact);
         }
      } else {
         nonTemplateArtifacts.add(artifact);
      }
   }

   private void processMetadata(ArtifactReadable artifact, WordMLProducer wordMl) {
      for (MetadataElement metadataElement : metadataElements) {
         processMetadata(artifact, wordMl, metadataElement.getType(), metadataElement.getFormat());
      }
   }

   private void processAttributes(ArtifactReadable artifact, WordMLProducer wordMl, PresentationType presentationType, boolean publishInLine, String footer) throws OseeCoreException {
      Renderer bestRenderer =
         OsgiUtil.getService(this.getClass(), RendererClassRegistry.class).getBestRenderer(presentationType, artifact);

      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();

         boolean allAttrs =
            rendererOptions.get(RendererOption.ALL_ATTRIBUTES) == null ? false : (boolean) rendererOptions.get(
               RendererOption.ALL_ATTRIBUTES);

         if (allAttrs || attributeName.equals("*")) {

            for (AttributeTypeToken attributeType : getAttributeTypeOrderList(artifact, bestRenderer)) {

               if (!outlining || !attributeType.equals(headingAttributeType)) {
                  processAttribute(artifact, wordMl, attributeElement, attributeType, true, presentationType,
                     publishInLine, footer, bestRenderer);
               }
            }
         } else {
            AttributeTypeId attributeType = orcsApi.getOrcsTypes().getAttributeTypes().getByName(attributeName);

            if (artifact.isAttributeTypeValid(attributeType)) {
               processAttribute(artifact, wordMl, attributeElement,
                  AttributeTypeToken.valueOf(attributeType.getId(), attributeName), false, presentationType,
                  publishInLine, footer, bestRenderer);
            }
         }
      }
   }

   // TODO: DisplayLogicAttribute, RdtAttributes, and RdtTableAttributes have override method
   // Update - they will have their own wordml rendered stored in db as wordtemplatecontent
   private List<AttributeTypeToken> getAttributeTypeOrderList(ArtifactReadable artifact, Renderer bestRenderer) {
      AttributeTypeToken contentType = null;
      Collection<AttributeTypeToken> attributeTypes = artifact.getValidAttributeTypes();
      return bestRenderer.getOrderedAttributeTypes(artifact, attributeTypes);
      //      ArrayList<AttributeTypeToken> orderedAttributeTypes = new ArrayList<>(attributes.size());
      //
      //      for (AttributeTypeToken attribute : attributes) {
      //         if (attribute.matches(CoreAttributeTypes.WholeWordContent, CoreAttributeTypes.WordTemplateContent,
      //            CoreAttributeTypes.PlainTextContent)) {
      //            contentType = attribute;
      //         } else {
      //            orderedAttributeTypes.add(attribute);
      //         }
      //      }
      //      Collections.sort(orderedAttributeTypes);
      //      if (contentType != null) {
      //         orderedAttributeTypes.add(contentType);
      //      }

      //   return orderedAttributeTypes;
   }

   private void processMetadata(ArtifactReadable artifact, WordMLProducer wordMl, String name, String format) {
      wordMl.startParagraph();

      String value = "";
      if (name.equals(APPLICABILITY)) {
         ApplicabilityToken applicabilityToken = applicabilityTokens.get(artifact.getApplicability());
         value = applicabilityToken.getName();
      } else if (name.equals(ARTIFACT_TYPE)) {
         value = artifact.getArtifactType().getName();
      } else if (name.equals(ARTIFACT_ID)) {
         value = artifact.getIdString();
      }
      if (format.contains(">x<")) {
         wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(name + ": " + value).toString() + "<"));
      } else {
         wordMl.addTextInsideParagraph(name + ": " + value);
      }
      wordMl.endParagraph();
   }

   private void processAttribute(ArtifactReadable artifact, WordMLProducer wordMl, AttributeElement attributeElement, AttributeTypeToken attributeType, boolean allAttrs, PresentationType presentationType, boolean publishInLine, String footer, Renderer bestRenderer) throws OseeCoreException {
      rendererOptions.put(RendererOption.ALL_ATTRIBUTES, allAttrs);
      // This is for SRS Publishing. Do not publish unspecified attributes
      if (!allAttrs && attributeType.matches(Partition, SeverityCategory)) {
         if (artifact.isAttributeTypeValid(Partition)) {
            for (AttributeReadable<Object> partition : artifact.getAttributes(Partition)) {
               if (partition == null || partition.getValue() == null || partition.getValue().equals("Unspecified")) {
                  return;
               }
            }
         }
      }

      boolean templateOnly = rendererOptions.get(
         RendererOption.TEMPLATE_ONLY) == null ? false : (boolean) rendererOptions.get(RendererOption.TEMPLATE_ONLY);

      if (templateOnly && attributeType.notEqual(WordTemplateContent)) {
         return;
      }

      /**
       * In some cases this returns no attributes at all, including no wordTemplateContent, even though it exists This
       * happens when wordTemplateContent is blank, so the else if condition takes this into account.
       */
      ResultSet<? extends AttributeReadable<Object>> attributes = artifact.getAttributes(attributeType);

      if (!attributes.isEmpty()) {
         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }

         // Do not publish relation order during publishing
         boolean inPubMode =
            rendererOptions.get(RendererOption.IN_PUBLISH_MODE) == null ? false : (boolean) rendererOptions.get(
               RendererOption.IN_PUBLISH_MODE);

         if (inPubMode && CoreAttributeTypes.RelationOrder.equals(attributeType)) {
            return;
         }

         if (!(publishInLine && artifact.isAttributeTypeValid(WordTemplateContent)) || attributeType.equals(
            WordTemplateContent)) {
            renderAttribute(attributeType, presentationType, artifact, wordMl, attributeElement.getFormat(),
               attributeElement.getLabel(), footer, rendererOptions, bestRenderer);
         }
      } else if (attributeType.equals(WordTemplateContent)) {
         renderAttribute(attributeType, presentationType, artifact, wordMl, attributeElement.getFormat(),
            attributeElement.getLabel(), footer, rendererOptions, bestRenderer);
      }
   }

   // TODO: DisplayLogicRequirement - csidRequirementAttribute, ButtonRequirmentArtifact  - RdtAttributes, PageTableArtifact - Display table has override method, 
   private void renderAttribute(AttributeTypeToken attributeType, PresentationType presentationType, ArtifactReadable artifact, WordMLProducer wordMl, String format, String label, String footer, Map<RendererOption, Object> rendererOptions, Renderer bestRenderer) {
      bestRenderer.renderAttribute(attributeType, artifact, presentationType, wordMl, format, label, footer,
         rendererOptions);
      //      if (attributeType.equals(CoreAttributeTypes.WordTemplateContent)) {
      //         String data = null;
      //         LinkType linkType = rendererOptions.get(RendererOption.LINK_TYPE) == null ? null : LinkType.valueOf(
      //            (String) rendererOptions.get(RendererOption.LINK_TYPE));
      //
      //         if (label.length() > 0) {
      //            wordMl.addParagraph(label);
      //         }
      //
      //         TransactionId txId = null;
      //         if (artifact.isHistorical()) {
      //            txId = artifact.getTransaction();
      //         } else {
      //            txId = TransactionId.SENTINEL;
      //         }
      //
      //         String oseeLink = ArtifactURL.getOpenInOseeLink(artifact, presentationType, sessionId, orcsApi).toString();
      //
      //         WordTemplateContentData wtcData = new WordTemplateContentData();
      //         wtcData.setArtId(artifact.getUuid());
      //         wtcData.setBranch(artifact.getBranch());
      //         wtcData.setFooter(footer);
      //         wtcData.setIsEdit(presentationType == PresentationType.SPECIALIZED_EDIT);
      //         wtcData.setLinkType(linkType != null ? linkType.toString() : null);
      //         wtcData.setTxId(txId);
      //         wtcData.setSessionId(sessionId);
      //         wtcData.setOseeLink(oseeLink);
      //         ArtifactId view = rendererOptions.get(
      //            RendererOption.VIEW) == null ? ArtifactId.SENTINEL : (ArtifactId) rendererOptions.get(RendererOption.VIEW);
      //         wtcData.setViewId(view == null ? ArtifactId.SENTINEL : view);
      //
      //         Pair<String, Set<String>> content = null;
      //         try {
      //            content = msWordOperations.renderWordTemplateContent(wtcData);
      //         } catch (Exception e) {
      //            throw new OseeCoreException(e);
      //         }
      //
      //         if (content != null) {
      //            data = content.getFirst();
      //         }
      //
      //         if (presentationType == PresentationType.SPECIALIZED_EDIT) {
      //            OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
      //            wordMl.addEditParagraphNoEscape(linkBuilder.getStartEditImage(artifact.getGuid()));
      //            wordMl.addWordMl(data);
      //            wordMl.addEditParagraphNoEscape(linkBuilder.getEndEditImage(artifact.getGuid()));
      //
      //         } else if (data != null) {
      //            wordMl.addWordMl(data);
      //         }
      //         wordMl.resetListValue();
      //      } else {
      //         boolean allAttrs =
      //            rendererOptions.get(RendererOption.ALL_ATTRIBUTES) == null ? false : (boolean) rendererOptions.get(
      //               RendererOption.ALL_ATTRIBUTES);
      //
      //         wordMl.startParagraph();
      //
      //         if (allAttrs) {
      //            if (!attributeType.matches(CoreAttributeTypes.PlainTextContent)) {
      //               wordMl.addWordMl("<w:r><w:t> " + Xml.escape(attributeType.getName()) + ": </w:t></w:r>");
      //            } else {
      //               wordMl.addWordMl("<w:r><w:t> </w:t></w:r>");
      //            }
      //         } else {
      //            // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
      //            wordMl.addWordMl(label);
      //         }
      //
      //         if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {
      //            // Do nothing. Relation Order will not be an attribute moving forward
      //         } else {
      //            String valueList = artifact.getAttributes(attributeType).toString();
      //            if (format.contains(">x<")) {
      //               wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(valueList).toString() + "<"));
      //            } else {
      //               wordMl.addTextInsideParagraph(valueList);
      //            }
      //            wordMl.endParagraph();
      //         }
      //      }
   }

   private void loadIgnoreAttributeExtensions() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      if (extensionRegistry != null) {
         IExtensionPoint point =
            extensionRegistry.getExtensionPoint("org.eclipse.osee.framework.ui.skynet.IgnorePublishAttribute");
         if (point != null) {
            IExtension[] extensions = point.getExtensions();
            for (IExtension extension : extensions) {
               IConfigurationElement[] elements = extension.getConfigurationElements();
               for (IConfigurationElement element : elements) {
                  ignoreAttributeExtensions.add(element.getAttribute("name"));
               }
            }
         }
      }
   }

}
