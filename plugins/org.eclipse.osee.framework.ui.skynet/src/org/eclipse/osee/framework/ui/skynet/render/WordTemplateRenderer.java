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

package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.DIFF;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.report.api.NestedTemplateData;
import org.eclipse.osee.define.report.api.TemplateData;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.RendererUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WordTemplateCompare;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.w3c.dom.Element;

/**
 * Renders WordML content.
 *
 * @author Jeff C. Phillips
 */
public class WordTemplateRenderer extends WordRenderer {
   private static final String INSERT_LINK = "INSERT_LINK_HERE";
   private static final String INSERT_ARTIFACT_HERE = "INSERT_ARTIFACT_HERE";

   private static final Pattern headElementsPattern =
      Pattern.compile("(" + INSERT_ARTIFACT_HERE + ")" + "|" + INSERT_LINK,
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final String EMBEDDED_OBJECT_NO = "w:embeddedObjPresent=\"no\"";
   private static final String EMBEDDED_OBJECT_YES = "w:embeddedObjPresent=\"yes\"";
   private static final String STYLES = "<w:styles>.*?</w:styles>";
   private static final String STYLES_END = "</w:styles>";
   private static final String OLE_START = "<w:docOleData>";
   private static final String OLE_END = "</w:docOleData>";

   private final IComparator comparator;

   public WordTemplateRenderer(Map<RendererOption, Object> options) {
      super(options);
      this.comparator = new WordTemplateCompare(this);
   }

   public WordTemplateRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public WordTemplateRenderer newInstance() {
      return new WordTemplateRenderer(new HashMap<RendererOption, Object>());
   }

   @Override
   public WordTemplateRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new WordTemplateRenderer(rendererOptions);
   }

   public void publish(Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts) throws OseeCoreException {
      List<ArtifactId> artifactTokens = new ArrayList<>();
      artifactTokens.addAll(artifacts);

      Map<RendererOption, Object> rendererOptions = getRendererOptions();

      BranchId branch = artifacts.iterator().next().getBranch();
      NestedTemplateData data = new NestedTemplateData();
      data.setArtifacts(artifactTokens);
      data.setBranch(branch);
      data.setMasterTemplate(masterTemplateArtifact);
      data.setSlaveTemplate(slaveTemplateArtifact);
      data.setRendererOptions(rendererOptions);
      data.setUser(UserId.valueOf(UserManager.getUser().getId()));
      data.setSessionId(ClientSessionManager.getSessionId());

      IFile file = RendererUtil.getRenderFile(COMMON, PREVIEW, "/", masterTemplateArtifact.getName(), ".xml");
      rendererOptions.put(RendererOption.RESULT_PATH_RETURN, file.getLocation().toOSString());

      ServiceUtil.getOseeClient().getMSWordEndpoint().applyNestedTemplates(data);
   }

   /**
    * Displays a list of artifacts in the Artifact Explorer that could not be multi edited because they contained
    * artifacts that had an OLEData attribute.
    */
   private void displayNotMultiEditArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               WordUiUtil.displayUnhandledArtifacts(artifacts, warningString);
            }
         });
      }
   }

   public static byte[] getFormattedContent(Element formattedItemElement) throws XMLStreamException {
      ByteArrayOutputStream data = new ByteArrayOutputStream((int) Math.pow(2, 10));
      XMLStreamWriter xmlWriter = null;
      try {
         xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(data, "UTF-8");
         for (Element e : Jaxp.getChildDirects(formattedItemElement)) {
            Jaxp.writeNode(xmlWriter, e, false);
         }
      } finally {
         if (xmlWriter != null) {
            xmlWriter.flush();
            xmlWriter.close();
         }
      }
      return data.toByteArray();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      int rating = NO_MATCH;
      if (!presentationType.matches(GENERALIZED_EDIT, GENERAL_REQUESTED)) {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.WordTemplateContent)) {
            if (presentationType.matches(DEFAULT_OPEN, PREVIEW)) {
               if (artifact.getAttributeCount(WordTemplateContent) > 0) {
                  rating = PRESENTATION_SUBTYPE_MATCH;
               } else {
                  rating = SUBTYPE_TYPE_MATCH;
               }
            } else {
               rating = PRESENTATION_SUBTYPE_MATCH;
            }
         } else if (presentationType.matches(PREVIEW, DIFF)) {
            rating = BASE_MATCH;
         }
      }
      return rating;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
      final List<Artifact> notMultiEditableArtifacts = new LinkedList<>();
      Artifact template;
      String templateContent = "";
      String templateOptions = "";
      String templateStyles = "";

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(null, presentationType);
         if (template != null) {
            templateContent = template.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent);
            templateOptions = template.getSoleAttributeValue(CoreAttributeTypes.RendererOptions);

            List<Artifact> templateRelatedArtifacts =
               template.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportingInfo);

            if (templateRelatedArtifacts != null) {

               if (templateRelatedArtifacts.size() == 1) {
                  templateStyles = templateRelatedArtifacts.get(0).getSoleAttributeValueAsString(
                     CoreAttributeTypes.WholeWordContent, "");
               } else if (templateRelatedArtifacts.size() > 1) {
                  OseeLog.log(this.getClass(), Level.INFO,
                     "More than one style relation currently not supported. Defaulting to styles defined in the template.");
               }
            }
         }
      } else {
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact, presentationType);
         if (template != null) {
            templateContent = template.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent);
            templateOptions = template.getSoleAttributeValue(CoreAttributeTypes.RendererOptions);

            List<Artifact> templateRelatedArtifacts =
               template.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportingInfo);

            if (templateRelatedArtifacts != null) {
               if (templateRelatedArtifacts.size() == 1) {
                  templateStyles = templateRelatedArtifacts.get(0).getSoleAttributeValueAsString(
                     CoreAttributeTypes.WholeWordContent, "");
               } else {
                  OseeLog.log(this.getClass(), Level.INFO,
                     "More than one style relation currently not supported. Defaulting to styles defined in the template.");
               }
            }
         }

         if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() > 1) {
            // currently we can't support the editing of multiple artifacts with OLE data
            for (Artifact artifact : artifacts) {
               if (!artifact.getSoleAttributeValue(CoreAttributeTypes.WordOleData, "").equals("")) {
                  notMultiEditableArtifacts.add(artifact);
               }
            }
            displayNotMultiEditArtifacts(notMultiEditableArtifacts,
               "Do not support editing of multiple artifacts with OLE data");
            artifacts.removeAll(notMultiEditableArtifacts);
         } else { // support OLE data when appropriate
            if (!firstArtifact.getSoleAttributeValue(CoreAttributeTypes.WordOleData, "").equals("")) {
               templateContent = templateContent.replaceAll(EMBEDDED_OBJECT_NO, EMBEDDED_OBJECT_YES);

               //Add in new template styles now so OLE Data doesn't get lost
               if (!templateStyles.isEmpty()) {
                  templateContent = templateContent.replace(STYLES, templateStyles);
                  templateStyles = "";
               }

               templateContent = templateContent.replaceAll(STYLES_END,
                  STYLES_END + OLE_START + firstArtifact.getSoleAttributeValue(CoreAttributeTypes.WordOleData,
                     "") + OLE_END);
            }
         }
      }

      templateContent = WordUtil.removeGUIDFromTemplate(templateContent);

      List<ArtifactId> artifactTokens = new ArrayList<>();
      artifactTokens.addAll(artifacts);
      BranchId branch = artifacts.iterator().next().getBranch();
      // TODO: CHECK HERE FOR DIFF? - how will this work?
      Map<RendererOption, Object> rendererOptions = getRendererOptions();
      String outlineNumber = (String) getRendererOptionValue(RendererOption.OUTLINE_TYPE);
      String outlineType = null;
      boolean recurseChildren = (boolean) getRendererOptionValue(RendererOption.RECURSE);

      CharBackedInputStream charBak = null;
      //      if ((boolean) rendererOptions.get(PUBLISH_DIFF) || (boolean) rendererOptions.get(ORIG_PUBLISH_AS_DIFF)) {
      //         try {
      //            charBak = new CharBackedInputStream();
      //
      //            WordMLProducer wordMl = new WordMLProducer(charBak);
      //            outlineNumber = peekAtFirstArtifactToGetParagraphNumber(templateContent, null, artifacts);
      //            //set heading numbers for wordml using templatecontent
      //            templateContent = wordMl.setHeadingNumbers(outlineNumber, templateContent, outlineType);
      //
      //            Matcher matcher = headElementsPattern.matcher(templateContent);
      //
      //            int lastEndIndex = 0;
      //            // add wordml from template content based on matcher
      //            if (matcher.find()) {
      //               wordMl.addWordMl(templateContent.substring(lastEndIndex, matcher.start()));
      //               lastEndIndex = matcher.end();
      //            }
      //
      //            // set next paragraph number
      //            if (Strings.isValid(outlineNumber)) {
      //               wordMl.setNextParagraphNumberTo(outlineNumber);
      //            }
      //
      //            WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(this);
      //            templateFileDiffer.generateFileDifferences(artifacts, "/results/", outlineNumber, outlineType,
      //               recurseChildren);
      //
      //            String endOfTemplate = templateContent.substring(lastEndIndex);
      //
      //            wordMl.addWordMl(updateFooter(endOfTemplate));
      //
      //         } catch (Exception ex) {
      //            // throw
      //            throw new OseeCoreException(ex);
      //         }
      //
      //         return charBak;
      //      } else {

      TemplateData data = new TemplateData();
      data.setArtifacts(artifactTokens);
      data.setBranch(branch);
      data.setTemplateContent(templateContent);
      data.setTemplateOptions(templateOptions);
      data.setTemplateStyles(templateStyles);
      data.setFolder(null);
      data.setOutlineNumber(null);
      data.setOutlineType((String) rendererOptions.get(RendererOption.OUTLINE_TYPE));
      data.setPresentationType(presentationType);
      data.setRendererOptions(getRendererOptions());
      data.setUser(UserId.valueOf(UserManager.getUser().getId()));
      data.setSessionId(ClientSessionManager.getSessionId());

      return ServiceUtil.getOseeClient().getMSWordEndpoint().applyTemplate(data);
      //   }
   }

   protected String peekAtFirstArtifactToGetParagraphNumber(String template, String nextParagraphNumber, List<Artifact> artifacts) throws OseeCoreException {
      String startParagraphNumber = "1";
      if (artifacts != null) {
         Matcher matcher = headElementsPattern.matcher(template);

         if (matcher.find()) {
            String elementType = matcher.group(0);

            if (elementType != null && elementType.equals(INSERT_ARTIFACT_HERE) && !artifacts.isEmpty()) {
               Artifact artifact = artifacts.iterator().next();
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

   protected Artifact getTemplate(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      // if USE_TEMPLATE_ONCE then only the first two artifacts will use the whole template (since they are diff'd with each other)
      // The settings from the template are stored previously and will be used, just not the content of the Word template

      boolean useTemplateOnce = (boolean) getRendererOptionValue(RendererOption.USE_TEMPLATE_ONCE);
      boolean firstTime = (boolean) getRendererOptionValue(RendererOption.FIRST_TIME);
      boolean secondTime = (boolean) getRendererOptionValue(RendererOption.SECOND_TIME);
      String option = (String) getRendererOptionValue(RendererOption.TEMPLATE_OPTION);
      ArtifactId templateArt = (ArtifactId) getRendererOptionValue(RendererOption.TEMPLATE_ARTIFACT);

      if (option != null && option.toString().isEmpty()) {
         option = null;
      }

      if (templateArt != null && templateArt.isValid() && (!useTemplateOnce || useTemplateOnce && (firstTime || secondTime))) {
         if (useTemplateOnce) {
            if (secondTime) {
               updateOption(RendererOption.SECOND_TIME, false);
            }
            if (firstTime) {
               updateOption(RendererOption.FIRST_TIME, false);
               updateOption(RendererOption.SECOND_TIME, true);
            }
         }

         if (templateArt instanceof Artifact) {
            return (Artifact) templateArt;
         } else {
            return ArtifactQuery.getArtifactFromId(templateArt, CoreBranches.COMMON);
         }

      }
      if (useTemplateOnce && !firstTime && !secondTime) {
         option = null;
      }
      return TemplateManager.getTemplate(this, artifact, presentationType, option);
   }

   @Override
   public IComparator getComparator() {
      return comparator;
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new UpdateArtifactOperation(file, artifacts, branch, false);
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor imageDescriptor = ImageManager.getProgramImageDescriptor("doc");
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "MS Word Edit", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "MS Word Preview", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "MS Word Preview with children", imageDescriptor,
         RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_WITH_RECURSE_VALUE.getKey()));
   }

}
