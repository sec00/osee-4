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
package org.eclipse.osee.ats.rest.internal;

import com.fasterxml.jackson.core.JsonFactory;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.agile.AgileEndpointImpl;
import org.eclipse.osee.ats.rest.internal.agile.operations.SprintDataUiOperation;
import org.eclipse.osee.ats.rest.internal.agile.operations.SprintSummaryOperation;
import org.eclipse.osee.ats.rest.internal.config.ActionableItemResource;
import org.eclipse.osee.ats.rest.internal.config.AtsConfigEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.ConvertAtsConfigGuidAttributes;
import org.eclipse.osee.ats.rest.internal.config.ConvertCreateUpdateAtsConfig;
import org.eclipse.osee.ats.rest.internal.config.ConvertResource;
import org.eclipse.osee.ats.rest.internal.config.CountryEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.CountryResource;
import org.eclipse.osee.ats.rest.internal.config.InsertionActivityEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.InsertionActivityResource;
import org.eclipse.osee.ats.rest.internal.config.InsertionEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.InsertionResource;
import org.eclipse.osee.ats.rest.internal.config.ProgramEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.ProgramResource;
import org.eclipse.osee.ats.rest.internal.config.ReportResource;
import org.eclipse.osee.ats.rest.internal.config.TeamResource;
import org.eclipse.osee.ats.rest.internal.config.UserResource;
import org.eclipse.osee.ats.rest.internal.config.VersionResource;
import org.eclipse.osee.ats.rest.internal.cpa.CpaResource;
import org.eclipse.osee.ats.rest.internal.cpa.CpaServiceRegistry;
import org.eclipse.osee.ats.rest.internal.notify.AtsNotifyEndpointImpl;
import org.eclipse.osee.ats.rest.internal.util.health.AtsHealthEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.ActionUiResource;
import org.eclipse.osee.ats.rest.internal.workitem.AtsActionEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsAttributeEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsTaskEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsTeamWfEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsWorkPackageEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.StateResource;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ConvertWorkDefinitionToAttributes;
import org.eclipse.osee.ats.rest.internal.workitem.workdef.AtsWorkDefEndpointImpl;
import org.eclipse.osee.ats.rest.internal.world.WorldResource;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.template.engine.OseeTemplateTokens;

/**
 * @author John Misinco
 */
@ApplicationPath("ats")
public class AtsApplication extends Application {

   private final Set<Object> singletons = new HashSet<>();

   private Log logger;
   private static OrcsApi orcsApi;
   private IAtsServer atsServer;
   private CpaServiceRegistry cpaRegistry;
   private JdbcService jdbcService;

   private ExecutorAdmin executorAdmin;

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      AtsApplication.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setCpaServiceRegistry(CpaServiceRegistry cpaRegistry) {
      this.cpaRegistry = cpaRegistry;
   }

   public void start() {
      IResourceRegistry registry = new ResourceRegistry();
      OseeTemplateTokens.register(registry);
      JsonFactory jsonFactory = JsonUtil.getFactory();

      // Register conversions
      ConvertCreateUpdateAtsConfig atsConfgConversion = new ConvertCreateUpdateAtsConfig(orcsApi);
      atsServer.addAtsDatabaseConversion(atsConfgConversion);
      atsServer.addAtsDatabaseConversion(new ConvertAtsConfigGuidAttributes());
      atsServer.addAtsDatabaseConversion(new ConvertWorkDefinitionToAttributes());

      // Register agile html report operations
      atsServer.getAgileSprintHtmlReportOperations().add(new SprintSummaryOperation(atsServer, registry));
      atsServer.getAgileSprintHtmlReportOperations().add(new SprintDataUiOperation(atsServer, registry));

      // Resources
      singletons.add(new VersionResource(atsServer, orcsApi));
      singletons.add(new TeamResource(atsServer, orcsApi));
      singletons.add(new ActionableItemResource(atsServer, orcsApi));

      singletons.add(new CountryResource(atsServer, orcsApi));
      singletons.add(new ProgramResource(atsServer, orcsApi));
      singletons.add(new InsertionResource(atsServer, orcsApi));
      singletons.add(new InsertionActivityResource(atsServer, orcsApi));

      singletons.add(new AtsActionEndpointImpl(atsServer, orcsApi));
      singletons.add(new StateResource(atsServer));
      singletons.add(new ConvertResource(atsServer));
      singletons.add(new CpaResource(orcsApi, atsServer, cpaRegistry));
      singletons.add(new UserResource(atsServer.getUserService()));
      singletons.add(new WorldResource(atsServer));
      singletons.add(new AtsHealthEndpointImpl(atsServer, jdbcService));
      singletons.add(new AtsWorkDefEndpointImpl(atsServer, orcsApi));

      // Endpoints
      singletons.add(new AgileEndpointImpl(atsServer, registry, jdbcService));
      singletons.add(new CountryEndpointImpl(atsServer));
      singletons.add(new ProgramEndpointImpl(atsServer));
      singletons.add(new InsertionEndpointImpl(atsServer));
      singletons.add(new InsertionActivityEndpointImpl(atsServer));
      singletons.add(new AtsConfigEndpointImpl(atsServer, orcsApi, logger, executorAdmin));
      singletons.add(new AtsTaskEndpointImpl(atsServer, orcsApi));
      singletons.add(new AtsNotifyEndpointImpl(atsServer));
      singletons.add(new AtsWorkPackageEndpointImpl(atsServer, logger));
      singletons.add(new AtsTeamWfEndpointImpl(atsServer));
      singletons.add(new AtsAttributeEndpointImpl(atsServer, orcsApi));

      // UIs
      singletons.add(new ActionUiResource(atsServer, logger));
      singletons.add(new ReportResource(orcsApi, atsServer));

      logger.warn("ATS Application Started - %s", System.getProperty("OseeApplicationServer"));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

   public static OrcsApi getOrcsApi() {
      return orcsApi;
   }

}
