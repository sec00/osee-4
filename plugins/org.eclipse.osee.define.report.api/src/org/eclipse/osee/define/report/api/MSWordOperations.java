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
package org.eclipse.osee.define.report.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.Pair;

public interface MSWordOperations {

   public WordUpdateChange updateWordArtifacts(WordUpdateData data);

   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data);

   InputStream applyTemplate(List<ArtifactId> artifacts, BranchId branch, UserId user, String sessionId, String templateContent, String templateOptions, String templateStyles, IContainer folder, String outlineNumber, String outlineType, PresentationType presentationType, Map<RendererOption, Object> rendererOptions);

   InputStream applyNestedTemplates(ArtifactId masterTemplateArtifact, ArtifactId slaveTemplateArtifact, List<ArtifactId> artifacts, BranchId branch, UserId user, String sessionId, Map<RendererOption, Object> rendererOptions);

}
