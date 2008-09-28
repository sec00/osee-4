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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;

/**
 * @author Donald G. Dunne
 */
public class KeyedBranchCache {
   private Map<String, Branch> keynameBranchMap = null;
   private static final KeyedBranchCache instance = new KeyedBranchCache();
   private static final String GET_BRANCH_NAMES_FROM_CONFIG = "SELECT * FROM osee_branch_definitions";

   private KeyedBranchCache() {
   }

   public static void createKeyedBranch(String keyname, Branch branch) throws OseeDataStoreException {
      instance.ensurePopulated();
      instance.keynameBranchMap.put(keyname.toLowerCase(), branch);
   }

   public static Branch getKeyedBranch(String keyname) throws OseeDataStoreException, BranchDoesNotExist {
      if (keyname == null) throw new IllegalArgumentException("keyname can not be null");

      instance.ensurePopulated();
      String lowerKeyname = keyname.toLowerCase();
      if (instance.keynameBranchMap.containsKey(lowerKeyname)) {
         return instance.keynameBranchMap.get(lowerKeyname);
      } else {
         throw new BranchDoesNotExist("The key \"" + keyname + "\" does not refer to any branch");
      }
   }

   private synchronized void ensurePopulated() throws OseeDataStoreException {
      if (keynameBranchMap == null) {
         populateCache();
      }
   }

   private void populateCache() throws OseeDataStoreException {
      keynameBranchMap = new HashMap<String, Branch>();

      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(GET_BRANCH_NAMES_FROM_CONFIG);

         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            try {
               keynameBranchMap.put(rSet.getString("static_branch_name").toLowerCase(),
                     BranchPersistenceManager.getBranch(rSet.getInt("mapped_branch_id")));
            } catch (BranchDoesNotExist ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         DbUtil.close(chStmt);
      }
   }
}