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
package org.eclipse.osee.jdbc.internal;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.Map;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcException;

/**
 * @author Roberto E. Escobar
 */
public class JdbcConnectionFactoryManager {

   private final Map<String, JdbcConnectionFactory> factories;

   public JdbcConnectionFactoryManager(Map<String, JdbcConnectionFactory> factories) {
      this.factories = factories;
   }

   public MetaData getMetaData(JdbcConnectionInfo dbInfo) {
      JdbcConnectionFactory proxiedFactory = getFactory(dbInfo.getDriver());
      try {
         return getMetaData(proxiedFactory, dbInfo);
      } catch (Exception ex) {
         throw JdbcException.newJdbcException(ex);
      }
   }

   public JdbcConnectionFactory getFactory(String driver) {
      JdbcConnectionFactory factory = factories.get(driver);
      if (factory == null) {
         factory = new DefaultConnectionFactory(driver);
         factories.put(driver, factory);
      }
      return factory;
   }

   private MetaData getMetaData(JdbcConnectionFactory proxiedFactory, JdbcConnectionInfo dbInfo) throws Exception {
      MetaData metaData = new MetaData();
      Connection connection = null;
      try {
         connection = proxiedFactory.getConnection(dbInfo);
         DatabaseMetaData metadata = connection.getMetaData();
         metaData.setTxIsolationLevelSupported(metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
         metaData.setValidationQuery(JdbcDbType.getValidationSql(metadata));
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
      return metaData;
   }

   public static final class MetaData {
      private boolean isTxIsolationLevelSupported;
      private String validationQuery;

      public boolean isTxIsolationLevelSupported() {
         return isTxIsolationLevelSupported;
      }

      public void setTxIsolationLevelSupported(boolean isTxIsolationLevelSupported) {
         this.isTxIsolationLevelSupported = isTxIsolationLevelSupported;
      }

      public String getValidationQuery() {
         return validationQuery;
      }

      public void setValidationQuery(String validationQuery) {
         this.validationQuery = validationQuery;
      }

   }

   private static final class DefaultConnectionFactory implements JdbcConnectionFactory {

      private final String driver;

      public DefaultConnectionFactory(String driver) {
         this.driver = driver;
      }

      @Override
      public Connection getConnection(JdbcConnectionInfo dbInfo) throws Exception {
         try {
            Class.forName(driver);
         } catch (Exception ex) {
            throw newJdbcException("Unable to find connection factory with driver [%s]", driver);
         }
         return DriverManager.getConnection(dbInfo.getUri(), dbInfo.getProperties());
      }

      @Override
      public String getDriver() {
         return driver;
      }
   }

}