/*
 * Created on Aug 31, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.report.api;

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.RENDER_AS_HUMAN_READABLE_TEXT;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class DefaultWordRenderer implements Renderer {

   private final OrcsApi orcsApi;
   private final DefineApi defineApi;
   private final String sessionId;

   public DefaultWordRenderer(OrcsApi orcsApi, DefineApi defineApi, String sessionId) {
      this.orcsApi = orcsApi;
      this.defineApi = defineApi;
      this.sessionId = sessionId;
   }

   @Override
   public void renderAttribute(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer, Map<RendererOption, Object> rendererOptions) {
      if (attributeType.equals(CoreAttributeTypes.WordTemplateContent)) {
         String data = null;
         LinkType linkType = rendererOptions.get(RendererOption.LINK_TYPE) == null ? null : LinkType.valueOf(
            (String) rendererOptions.get(RendererOption.LINK_TYPE));

         if (label.length() > 0) {
            producer.addParagraph(label);
         }

         TransactionId txId = null;
         if (artifact.isHistorical()) {
            txId = artifact.getTransaction();
         } else {
            txId = TransactionId.SENTINEL;
         }

         String oseeLink = ArtifactURL.getOpenInOseeLink(artifact, presentationType, sessionId, orcsApi).toString();

         WordTemplateContentData wtcData = new WordTemplateContentData();
         wtcData.setArtId(artifact.getUuid());
         wtcData.setBranch(artifact.getBranch());
         wtcData.setFooter(footer);
         wtcData.setIsEdit(presentationType == PresentationType.SPECIALIZED_EDIT);
         wtcData.setLinkType(linkType != null ? linkType.toString() : null);
         wtcData.setTxId(txId);
         wtcData.setSessionId(sessionId);
         wtcData.setOseeLink(oseeLink);
         ArtifactId view = rendererOptions.get(
            RendererOption.VIEW) == null ? ArtifactId.SENTINEL : (ArtifactId) rendererOptions.get(RendererOption.VIEW);
         wtcData.setViewId(view == null ? ArtifactId.SENTINEL : view);

         Pair<String, Set<String>> content = null;
         try {
            content = defineApi.getMSWordOperations().renderWordTemplateContent(wtcData);
         } catch (Exception e) {
            throw new OseeCoreException(e);
         }

         if (content != null) {
            data = content.getFirst();
         }

         if (presentationType == PresentationType.SPECIALIZED_EDIT) {
            OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
            producer.addEditParagraphNoEscape(linkBuilder.getStartEditImage(artifact.getGuid()));
            producer.addWordMl(data);
            producer.addEditParagraphNoEscape(linkBuilder.getEndEditImage(artifact.getGuid()));

         } else if (data != null) {
            producer.addWordMl(data);
         }
         producer.resetListValue();
      } else {
         boolean allAttrs = (boolean) rendererOptions.get(RendererOption.ALL_ATTRIBUTES);

         producer.startParagraph();

         if (allAttrs) {
            if (!attributeType.matches(CoreAttributeTypes.PlainTextContent)) {
               producer.addWordMl("<w:r><w:t> " + Xml.escape(attributeType.getName()) + ": </w:t></w:r>");
            } else {
               producer.addWordMl("<w:r><w:t> </w:t></w:r>");
            }
         } else {
            // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
            producer.addWordMl(label);
         }

         if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {
            // Do nothing. Relation Order will not be an attribute moving forward
         } else {
            String valueList = artifact.getAttributes(attributeType).toString();
            if (format.contains(">x<")) {
               producer.addWordMl(format.replace(">x<", ">" + Xml.escape(valueList).toString() + "<"));
            } else {
               producer.addTextInsideParagraph(valueList);
            }
            producer.endParagraph();
         }
      }
   }

   @Override
   public int minimumRanking() {
      return NO_MATCH;
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, ArtifactReadable artifact) {
      if (presentationType.matches(GENERALIZED_EDIT, GENERAL_REQUESTED, PRODUCE_ATTRIBUTE)) {
         return PRESENTATION_TYPE;
      }
      if (presentationType.matches(SPECIALIZED_EDIT, DEFAULT_OPEN)) {
         return GENERAL_MATCH;
      }
      if (presentationType.matches(PREVIEW, RENDER_AS_HUMAN_READABLE_TEXT)) {
         return BASE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public List<AttributeTypeToken> getOrderedAttributeTypes(ArtifactReadable artifact, Collection<? extends AttributeTypeToken> attributeTypes) {
      ArrayList<AttributeTypeToken> orderedAttributeTypes = new ArrayList<>(attributeTypes.size());
      AttributeTypeToken contentType = null;

      for (AttributeTypeToken attributeType : attributeTypes) {
         if (attributeType.matches(CoreAttributeTypes.WholeWordContent, CoreAttributeTypes.WordTemplateContent,
            CoreAttributeTypes.PlainTextContent)) {
            contentType = attributeType;
         } else {
            orderedAttributeTypes.add(attributeType);
         }
      }

      Collections.sort(orderedAttributeTypes);
      if (contentType != null) {
         orderedAttributeTypes.add(contentType);
      }
      return orderedAttributeTypes;
   }

}
