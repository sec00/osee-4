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
package org.eclipse.osee.ats.core.context;

import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * ATS Context stores the context configuration and user selection on the Common branch. It should not change based on
 * which ATS Branch is selected.
 * 
 * @author Donald G. Dunne
 */
public class AtsContextStore {

   private static final String ATS_CONTEXT_USER_DEFAULT_UUID_STATIC_ID = "atsContextUser:";
   private static final String ATS_CONTEXT_CONFIG_STATIC_ID = "atsContextConfig:";

   // Property Store Uuid array
   private static String CONTEXT_UUIDS = "atsContextBranchUuids";
   // Property Store id
   private static String CONFIG_STORE_ID = "AtsConfigStore";
   private final IAttributeResolver attrResolver;

   public AtsContextStore(IAttributeResolver attrResolver) {
      super();
      this.attrResolver = attrResolver;
   }

   public void write(AtsContext context) throws Exception {
      writeAtsConfig(context);
      writeUserToDefaultId(context);
   }

   private void writeUserToDefaultId(AtsContext context) {
      for (String userId : context.getUserIds()) {
         boolean found = false;
         String contextUuid = context.getUserContextUuid(userId);
         for (IAttribute<Object> attr : attrResolver.getAttributes(CoreBranches.COMMON,
            CoreArtifactTokens.GlobalPreferences, CoreAttributeTypes.StaticId)) {
            if (((String) attr.getValue()).startsWith(ATS_CONTEXT_USER_DEFAULT_UUID_STATIC_ID)) {
               attr.setValue(getUserStoreStr(userId, contextUuid));
               found = true;
               break;
            }
         }
         if (!found) {
            attrResolver.addAttribute(CoreBranches.COMMON, CoreArtifactTokens.GlobalPreferences,
               CoreAttributeTypes.StaticId, getUserStoreStr(userId, contextUuid));
         }
      }
   }

   private String getUserStoreStr(String userId, String contextUuid) {
      return String.format("%s%s:%s", ATS_CONTEXT_USER_DEFAULT_UUID_STATIC_ID, userId, contextUuid);
   }

   private void writeAtsConfig(AtsContext context) throws Exception {
      PropertyStore store = new PropertyStore(CONFIG_STORE_ID);
      store.put(CONTEXT_UUIDS, context.getContextUuids().toArray(new String[context.getContextUuids().size()]));
      for (String uuid : context.getContextUuids()) {
         store.put(uuid, context.getContextName(uuid));
      }
      writeAtsConfigStr(store.save());
   }

   public void writeAtsConfigStr(String string) {
      IAttribute<Object> foundAttr = null;
      for (IAttribute<Object> attr : attrResolver.getAttributes(CoreBranches.COMMON,
         CoreArtifactTokens.GlobalPreferences, CoreAttributeTypes.StaticId)) {
         if (((String) attr.getValue()).startsWith(ATS_CONTEXT_CONFIG_STATIC_ID)) {
            attr.setValue(getConfigStoreStr(string));
            foundAttr = attr;
            break;
         }
      }
      if (foundAttr == null) {
         attrResolver.addAttribute(CoreBranches.COMMON, CoreArtifactTokens.GlobalPreferences,
            CoreAttributeTypes.StaticId, getConfigStoreStr(string));
      }
   }

   private String getConfigStoreStr(String string) {
      return ATS_CONTEXT_CONFIG_STATIC_ID + string;
   }

   public AtsContext read() throws Exception {
      AtsContext context = new AtsContext();
      readAtsConfig(context);
      readUserToDefaultId(context);
      return context;
   }

   private PropertyStore readAtsConfig(AtsContext context) throws Exception {
      PropertyStore store = readAtsConfigStore();
      for (String uuid : store.getArray(CONTEXT_UUIDS)) {
         context.addContext(uuid, store.get(uuid));
      }
      return store;
   }

   public void readUserToDefaultId(AtsContext context) {
      for (String str : attrResolver.getAttributesToStringList(CoreBranches.COMMON,
         CoreArtifactTokens.GlobalPreferences, CoreAttributeTypes.StaticId)) {
         if (str.startsWith(ATS_CONTEXT_USER_DEFAULT_UUID_STATIC_ID)) {
            String values[] = str.split(":");
            context.setUserContextUuid(values[1], values[2]);
            break;
         }
      }
   }

   private String readAtsConfigStr() {
      String result = null;
      for (String str : attrResolver.getAttributesToStringList(CoreBranches.COMMON,
         CoreArtifactTokens.GlobalPreferences, CoreAttributeTypes.StaticId)) {
         if (str.startsWith(ATS_CONTEXT_CONFIG_STATIC_ID)) {
            result = str.replaceFirst(ATS_CONTEXT_CONFIG_STATIC_ID, "");
            break;
         }
      }
      return result;
   }

   private PropertyStore readAtsConfigStore() throws Exception {
      PropertyStore store = null;
      String atsContextConfigStr = readAtsConfigStr();
      if (Strings.isValid(atsContextConfigStr)) {
         store = new PropertyStore(CONFIG_STORE_ID);
         store.load(atsContextConfigStr);
      } else {
         store = getDefaultConfigStore();
      }
      return store;
   }

   private PropertyStore getDefaultConfigStore() {
      PropertyStore configStore = new PropertyStore(CONFIG_STORE_ID);
      String commonUuidStr = String.valueOf(CoreBranches.COMMON.getUuid());
      configStore.put(CONTEXT_UUIDS, new String[] {commonUuidStr});
      configStore.put(commonUuidStr, CoreBranches.COMMON.getName());
      return configStore;
   }

}
