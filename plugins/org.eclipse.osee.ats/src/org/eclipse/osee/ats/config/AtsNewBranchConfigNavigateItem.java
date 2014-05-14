/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.core.client.config.AtsArtifactToken;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * Create ATS Configuration. See {@link AtsConfig2DataExample} for details.
 * 
 * @author Donald G. Dunne
 */
public class AtsNewBranchConfigNavigateItem extends XNavigateItemAction {

   public AtsNewBranchConfigNavigateItem(XNavigateItem parent) {
      super(parent, "ATS New Branch Config", FrameworkImage.GEAR);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      IOseeBranch branch = TokenFactory.createBranch("AAMrhl4ycAhM26BblpwA", "ATS Test");
      AtsUtilCore.setBranch(branch);

      AtsChangeSet changes = new AtsChangeSet(getName());
      XResultData results = new XResultData(false);
      Artifact folder = createAtsFolders(branch, changes);

      List<WorkDefinitionSheet> sheets = new ArrayList<WorkDefinitionSheet>();

      File supportFile =
         AtsWorkDefinitionSheetProviders.getSupportFile(Activator.PLUGIN_ID, "support/AtsBranchConfig.ats");
      sheets.add(new WorkDefinitionSheet("AtsBranchConfig", supportFile,
         org.eclipse.osee.ats.api.data.AtsArtifactToken.AtsBranchConfig));

      Set<String> stateNames = new HashSet<String>();
      AtsWorkDefinitionSheetProviders.importWorkDefinitionSheets(results, changes, folder, sheets, stateNames);
      AtsWorkDefinitionSheetProviders.createStateNameArtifact(stateNames, folder, changes);
      changes.execute();

      AtsUtilCore.setBranch(CoreBranches.COMMON);
   }

   public static Artifact createAtsFolders(IOseeBranch atsBranch, AtsChangeSet changes) throws OseeCoreException {

      Artifact headingArt = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.HeadingFolder, atsBranch);
      if (!headingArt.hasParent()) {
         Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(atsBranch);
         rootArt.addChild(headingArt);
         changes.add(rootArt);
      }
      return headingArt;
   }

}
