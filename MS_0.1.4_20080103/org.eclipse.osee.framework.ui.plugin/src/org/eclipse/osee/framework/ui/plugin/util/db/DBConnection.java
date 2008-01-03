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
package org.eclipse.osee.framework.ui.plugin.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.data.DbInformation;
import org.eclipse.osee.framework.plugin.core.config.data.DbDetailData.ConfigField;
import org.eclipse.osee.framework.plugin.core.db.IConnection;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.plugin.util.OseeInfo;
import org.osgi.framework.Bundle;

/**
 * Connect to a Database Service
 */
public class DBConnection {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(DBConnection.class);
   public static final int CODE_VERSION_VALUE = 20080103;
   public static final String DB_VERSION_KEY = "version";

   private static Map<String, IConnection> connections = new HashMap<String, IConnection>();

   static {
      populateConnections();
   }

   public static Connection getNewConnection(DbInformation databaseService) throws SQLException {
      return getNewConnection(databaseService, true);
   }

   public static Connection getNewConnection(DbInformation databaseService, boolean validityCheck) throws SQLException {
      try {

         IConnection connectionFactory = connections.get(databaseService.getConnectionData().getDBDriver());
         if (connectionFactory == null) {
            throw new IllegalStateException(String.format(
                  "Unable to locate a Connection Factory for the driver type [%s].",
                  databaseService.getConnectionData().getDBDriver()));
         }

         String userName = databaseService.getDatabaseDetails().getFieldValue(ConfigField.UserName);
         String password = databaseService.getDatabaseDetails().getFieldValue(ConfigField.Password);

         // Connection properties and attributes are added in the
         // Connection Description portion of the Database Config XML file.
         Properties properties = databaseService.getConnectionData().getProperties();
         properties.setProperty("user", userName);
         properties.setProperty("password", password);

         String dbUrl = databaseService.getFormattedURL() + databaseService.getConnectionData().getAttributes();

         logger.log(Level.INFO, "Getting new connection: " + dbUrl);
         Connection connection = connectionFactory.getConnection(properties, dbUrl);
         connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

         if (validityCheck && !userName.equals("peer")) {
            try {
               ensureDatabaseCompatability(connection);
            } catch (Exception ex) {
               connection.close();
               throw ex;
            }
         }

         return connection;
      } catch (Throwable th) {
         logger.log(Level.SEVERE, "Unable to get database connection.", th);
         throw new SQLException("Unable to get a database connection: " + th.getMessage());
      }
   }

   private static void populateConnections() {
      Bundle bundle = PluginCoreActivator.getInstance().getBundle();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(bundle.getSymbolicName() + ".JdbcConnection", "Connection");

      for (IConfigurationElement element : elements) {
         String connectionClass = element.getAttribute("ConnectionFactory");
         String driver = element.getAttribute("Driver");
         try {
            IConnection connection =
                  (IConnection) Platform.getBundle(element.getContributor().getName()).loadClass(connectionClass).newInstance();
            connections.put(driver, connection);
         } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   private static String dbVersionStr = null;

   public static String getDatabaseVersion(Connection connection) throws SQLException {
      // NOTE: Can't use OSEEInfo class cause it uses connection handler
      String query = "SELECT OSEE_VALUE FROM OSEE_INFO WHERE OSEE_KEY = \'version\'";

      Statement statement = connection.createStatement();
      statement.setFetchSize(1);
      ResultSet rset = statement.executeQuery(query);
      if (rset.next()) {
         return rset.getString("OSEE_VALUE");
      }
      statement.close();
      return "";
   }

   private static void ensureDatabaseCompatability(Connection connection) throws Exception {
      if (dbVersionStr == null) {
         dbVersionStr = getDatabaseVersion(connection);
      }
      int dbVersionInt = 0;
      if (dbVersionStr != null) {
         try {
            dbVersionInt = new Integer(dbVersionStr);
         } catch (Exception ex) {
            // do nothing
         }
      }
      if (dbVersionStr == null || CODE_VERSION_VALUE < dbVersionInt) {
         String errorStr =
               String.format(
                     "This installation of OSEE \"%d\" is out of date and is not compatible with database version \"%s\".  Please restart OSEE and accept all updates.",
                     CODE_VERSION_VALUE, dbVersionStr);
         if (!OseeProperties.getInstance().isOverrideVersionCheck())
            throw new IllegalArgumentException(errorStr);
         else
            logger.log(Level.SEVERE, "Overriding Version Check - " + errorStr);
      }
   }

   public static void insertDbVersion() throws SQLException {
      OseeInfo.putValue(DB_VERSION_KEY, String.valueOf(CODE_VERSION_VALUE));
   }
}