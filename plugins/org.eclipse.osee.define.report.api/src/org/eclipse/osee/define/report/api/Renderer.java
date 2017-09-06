/*
 * Created on Aug 31, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.report.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public interface Renderer {

   public static final int SPECIALIZED_KEY_MATCH = 70;
   public static final int SPECIALIZED_MATCH = 60;
   public static final int PRESENTATION_SUBTYPE_MATCH = 50;
   public static final int PRESENTATION_TYPE = 40;
   public static final int SUBTYPE_TYPE_MATCH = 30;
   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int GENERAL_MATCH = 10;
   public static final int BASE_MATCH = 5;
   public static final int NO_MATCH = -1;

   public void renderAttribute(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer, Map<RendererOption, Object> rendererOptions);

   public int minimumRanking();

   public int getApplicabilityRating(PresentationType presentationType, ArtifactReadable artifact);

   public List<AttributeTypeToken> getOrderedAttributeTypes(ArtifactReadable artifact, Collection<? extends AttributeTypeToken> attributeTypes);
}
