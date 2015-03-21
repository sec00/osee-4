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

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientBuilder;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - URL Test", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      Job background = new Job("Testing") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            JdbcClient build =
               JdbcClientBuilder.oracle("lba9", "sun817.msc.az.boeing.com", 1521).dbUsername("osee_client").dbPassword(
                  "osee_client").build();

            final AtomicInteger oracleExceptions = new AtomicInteger(0);
            final AtomicInteger serverExceptions = new AtomicInteger(0);
            int counter = 0;
            while (true) {
               try {

                  String appServer = OseeClientProperties.getOseeApplicationServer();
                  System.out.println(counter++);
                  URI uri = UriBuilder.fromUri(appServer).path("lba/promote/engrbuild/teamdef/2010115").build();
                  String result =
                     JaxRsClient.newClient().target(uri).request(MediaType.APPLICATION_JSON).get(String.class);

                  if (!result.contains("AFejmw6LyzZ32ZTxMfAA")) {
                     throw new OseeStateException("Unexpected result [%s]", result);
                  }
               } catch (Exception ex) {
                  String errorStr = String.format("Server Exceptions %s ", ex.getMessage());
                  System.err.println(errorStr);
                  serverExceptions.incrementAndGet();
                  OseeLog.log(org.eclipse.osee.ats.internal.Activator.class, Level.SEVERE, errorStr, ex);
               }

               //               try {
               //                  Thread.sleep(200);
               //               } catch (InterruptedException ex) {
               //                  // do nothing
               //               }
            }

         }
      };
      Jobs.startJob(background);

   }
}
