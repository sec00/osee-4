/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.util;

import org.eclipse.osee.framework.ui.plugin.security.JvmAuthentication;

public class DemoAuthentication extends JvmAuthentication {

   private static final DemoAuthentication instance = new DemoAuthentication();

   private DemoAuthentication() {
   }

   public static DemoAuthentication getInstance() {
      return instance;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.security.AbstractAuthentication#isAuthenticated()
    */
   @Override
   public boolean isAuthenticated() {
      userCredentials.setFieldAndValidity(
            org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Id, true, "Jason Baker");
      userCredentials.setFieldAndValidity(
            org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Name, true, "Jason Baker");
      authenticationStatus =
            org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication.AuthenticationStatus.Success;
      return true;
   }

   public boolean authenticate(String userName, String password, String domain) {
      System.err.println("DemoAuthentication: FIX for release");
      {
         userCredentials.setFieldAndValidity(
               org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Id, true, "Jason Baker");
         userCredentials.setFieldAndValidity(
               org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Name, true,
               "Jason Baker");
         authenticationStatus =
               org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication.AuthenticationStatus.Success;
      }
      return true;
      //      if (password.equals("osee") && domain.equals("osee")) {
      //         userCredentials.setFieldAndValidity(
      //               org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Id, true, userName);
      //         userCredentials.setFieldAndValidity(
      //               org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Name, true, userName);
      //         authenticationStatus =
      //               org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication.AuthenticationStatus.Success;
      //         return true;
      //      } else {
      //         return false;
      //      }
   }

   public boolean isLoginAllowed() {
      System.err.println("DemoAuthentication: return true for release");
      return false;
   }

}