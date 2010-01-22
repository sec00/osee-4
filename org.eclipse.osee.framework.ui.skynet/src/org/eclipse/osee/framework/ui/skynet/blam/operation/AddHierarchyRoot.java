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
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class AddHierarchyRoot extends AbstractBlam {

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Artifact hierarchyRoot = variableMap.getArtifacts("artifact").get(0);
      hierarchyRoot.reflect(BranchManager.getBranch(CoreBranches.SYSTEM_ROOT));
      hierarchyRoot.persist();
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifact\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }
}