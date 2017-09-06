/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report;

import java.io.InputStream;
import java.util.Set;
import org.eclipse.osee.define.report.api.DefineApi;
import org.eclipse.osee.define.report.api.MSWordEndpoint;
import org.eclipse.osee.define.report.api.NestedTemplateData;
import org.eclipse.osee.define.report.api.TemplateData;
import org.eclipse.osee.define.report.api.WordTemplateContentData;
import org.eclipse.osee.define.report.api.WordUpdateChange;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author David W. Miller
 */
public final class MSWordEndpointImpl implements MSWordEndpoint {

   private final DefineApi defineApi;

   public MSWordEndpointImpl(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      return defineApi.getMSWordOperations().updateWordArtifacts(data);
   }

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) {
      return defineApi.getMSWordOperations().renderWordTemplateContent(data);
   }

   @Override
   public InputStream applyTemplate(TemplateData data) {
      return defineApi.getMSWordOperations().applyTemplate(data.getArtifacts(), data.getBranch(), data.getUser(), data.getSessionId(),
         data.getTemplateContent(), data.getTemplateOptions(), data.getTemplateStyles(), data.getFolder(),
         data.getOutlineNumber(), data.getOutlineType(), data.getPresentationType(), data.getRendererOptions());
   }

   @Override
   public InputStream applyNestedTemplates(NestedTemplateData data) {
      return defineApi.getMSWordOperations().applyNestedTemplates(data.getMasterTemplate(), data.getSlaveTemplate(),
         data.getArtifacts(), data.getBranch(), data.getUser(), data.getSessionId(), data.getRendererOptions());

   }
}
