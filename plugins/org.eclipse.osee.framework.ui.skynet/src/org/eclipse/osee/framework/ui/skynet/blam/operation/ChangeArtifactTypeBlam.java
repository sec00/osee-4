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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * Changes the descriptor type of an artifact to the provided descriptor.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeArtifactTypeBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Change Artifact Type";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ChangeArtifactType.changeArtifactType(variableMap.getArtifacts("artifacts"),
         variableMap.getArtifactType("New Artifact Type"));
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" />" +
      //
      "<XWidget xwidgetType=\"XArtifactTypeComboViewer\" displayName=\"New Artifact Type\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   @Override
   public String getDescriptionUsage() {
      return "Log what the previous type of each artifact was because that information is loss after running this blam";
   }
}