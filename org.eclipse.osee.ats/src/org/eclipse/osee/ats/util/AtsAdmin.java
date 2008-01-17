/*
 * Created on Jan 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;

/**
 * @author Donald G. Dunne
 */
public class AtsAdmin {

   private static ArtifactTypeNameSearch search =
         new ArtifactTypeNameSearch("General Document", "AtsAdmin", AtsPlugin.getAtsBranch());
   private static Artifact atsAdminArtifact;
   private static boolean searched = false;

   public static boolean isAtsAdmin() {
      OseeProperties.getInstance().setDeveloper(System.getProperty("AtsAdmin") != null);
      if (System.getProperty("AtsAdmin") != null) {
         return true;
      }
      if (!searched) {
         atsAdminArtifact = search.getSingletonArtifact(Artifact.class);
         searched = true;
      }
      if (atsAdminArtifact != null) {
         return AccessControlManager.getInstance().checkObjectPermission(
               SkynetAuthentication.getInstance().getAuthenticatedUser(), atsAdminArtifact, PermissionEnum.FULLACCESS);
      }
      return false;
   }
}
