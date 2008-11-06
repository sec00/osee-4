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
package org.eclipse.osee.ats.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserCache;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IDefaultInitialBranchesProvider;

/**
 * @author Robert A. Fisher
 */
public class TeamBasedDefaultBranchProvider implements IDefaultInitialBranchesProvider {

   public Collection<Branch> getDefaultInitialBranches() {
      User user = UserCache.getUser();
      try {
         Collection<TeamDefinitionArtifact> teams =
               user.getRelatedArtifacts(AtsRelation.TeamMember_Team, TeamDefinitionArtifact.class);
         Collection<Branch> branches = new LinkedList<Branch>();

         Branch branch;
         for (TeamDefinitionArtifact team : teams) {
            branch = team.getTeamBranch();
            if (branch != null) {
               branches.add(branch);
            }
         }

         return branches;
      } catch (Exception ex) {
         OseeLog.log(TeamBasedDefaultBranchProvider.class, Level.WARNING,  ex);
      }

      return null;
   }

}
