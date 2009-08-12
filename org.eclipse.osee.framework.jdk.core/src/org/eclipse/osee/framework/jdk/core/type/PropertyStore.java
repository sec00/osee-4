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
package org.eclipse.osee.framework.jdk.core.type;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public class PropertyStore implements IPropertyStore, Serializable {
   private static final long serialVersionUID = 9076969425223251739L;

   private static final String EXCEPTION_MESSAGE = "No setting found for key: [%s]";

   private String storeId;
   private final Properties storageData;
   private final Properties storageArrays;

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof PropertyStore) {
         PropertyStore castObj = (PropertyStore) obj;
         return castObj.storeId.equals(storeId) && castObj.storageData.equals(storageData) && castObj.storageArrays.equals(storageArrays);
      }
      return false;
   }

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + storeId.hashCode();
      result = 31 * result + storageData.hashCode();
      result = 31 * result + storageArrays.hashCode();
      return result;
   }

   private PropertyStore(String storeId, Properties storageData, Properties storageArrays) {
      super();
      this.storeId = storeId;
      this.storageData = storageData;
      this.storageArrays = storageArrays;
   }

   public PropertyStore(String storeId) {
      this(storeId, new Properties(), new Properties());
   }

   public PropertyStore() {
      this("");
   }

   public PropertyStore(Properties properties) {
      this(Integer.toString(properties.hashCode()), properties, new Properties());
   }

   public String get(String key) {
      return (String) storageData.get(key);
   }

   public String[] getArray(String key) {
      return (String[]) storageArrays.get(key);
   }

   public boolean getBoolean(String key) {
      return Boolean.valueOf((String) storageData.get(key)).booleanValue();
   }

   public double getDouble(String key) throws NumberFormatException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Double(setting).doubleValue();
   }

   public float getFloat(String key) throws NumberFormatException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Float(setting).floatValue();
   }

   public int getInt(String key) throws NumberFormatException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Integer(setting).intValue();
   }

   public long getLong(String key) throws NumberFormatException {
      String setting = (String) storageData.get(key);
      if (setting == null) {
         throw new NumberFormatException(String.format(EXCEPTION_MESSAGE, key));
      }

      return new Long(setting).longValue();
   }

   public void put(String key, String[] value) {
      if (value == null) {
         value = new String[0];
      }
      storageArrays.put(key, value);
   }

   public void put(String key, double value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, float value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, int value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, long value) {
      put(key, String.valueOf(value));
   }

   public void put(String key, String value) {
      if (value == null) {
         value = "";
      }
      storageData.put(key, value);
   }

   public void put(String key, boolean value) {
      put(key, String.valueOf(value));
   }

   public String getId() {
      return storeId;
   }

   protected void setId(String name) {
      this.storeId = name;
   }

   protected Properties getItems() {
      return storageData;
   }

   protected Properties getArrays() {
      return storageArrays;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(storageData.toString().replaceAll(",", ",\n"));
      return builder.toString();
   }

   public void load(InputStream inputStream) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.load(this, inputStream);
   }

   public void load(Reader reader) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.load(this, reader);
   }

   public void save(Writer writer) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.save(this, writer);
   }

   public void save(OutputStream outputStream) throws Exception {
      PropertyStoreWriter storeWriter = new PropertyStoreWriter();
      storeWriter.save(this, outputStream);
   }

   public Set<String> arrayKeySet() {
      List<String> items = Collections.castAll(this.storageArrays.keySet());
      return Collections.toSet(items);
   }

   public Set<String> keySet() {
      List<String> items = Collections.castAll(this.storageData.keySet());
      return Collections.toSet(items);
   }
}
