/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTokens {

   // @formatter:off
   public static ArtifactToken AccessIdMap = ArtifactToken.valueOf(9885202, "Access Id Map - 0.26", COMMON, CoreArtifactTypes.GeneralData);
   public static ArtifactToken DefaultHierarchyRoot = ArtifactToken.valueOf(197818, "Default Hierarchy Root", CoreArtifactTypes.RootArtifact);
   public static ArtifactToken UniversalGroupRoot = ArtifactToken.valueOf(60807, "Root Artifact", CoreArtifactTypes.UniversalGroup);
   public static ArtifactToken Everyone = ArtifactToken.valueOf(48656, "AAABEbn4DKoAaR82FZsL3A", "Everyone", COMMON, CoreArtifactTypes.UserGroup);
   public static ArtifactToken OseeAdmin = ArtifactToken.valueOf(52247, "AAABHaItmnUAG6ZAYlFKag", "OseeAdmin", COMMON, CoreArtifactTypes.UserGroup);
   public static ArtifactToken OseeAccessAdmin = ArtifactToken.valueOf(8033605, "AGXiIJi2qxZnuXEdZVwA", "OseeAccessAdmin", COMMON, CoreArtifactTypes.UserGroup);
   public static ArtifactToken UserGroups = ArtifactToken.valueOf(80920, "AAABGTAGNY8BauB5GajiIQ", "User Groups", COMMON, CoreArtifactTypes.Folder);
   public static ArtifactToken OseeTypesFolder = ArtifactToken.valueOf(7911256, "AKsESORrehN02WEfF9gA", "OSEE Types and Access Control", COMMON, CoreArtifactTypes.Folder);
   public static ArtifactToken DataRightsFooters = ArtifactToken.valueOf(5443258, null, "DataRightsFooters",  COMMON,CoreArtifactTypes.GeneralData);
   public static ArtifactToken DocumentTemplates = ArtifactToken.valueOf(64970, null, "Document Templates", COMMON, CoreArtifactTypes.Folder);
   public static ArtifactToken FrameworkAccessModel = ArtifactToken.valueOf(35975422L, null, "Framework Access Model", COMMON, CoreArtifactTypes.AccessControlModel);
   public static ArtifactToken ProductLineFolder = ArtifactToken.valueOf(8255179, "Product Line", CoreArtifactTypes.Folder);
   public static ArtifactToken ProductsFolder = ArtifactToken.valueOf(10039752, "Products", CoreArtifactTypes.Folder);

   // @formatter:on

   private CoreArtifactTokens() {
      // Constants
   }
}
