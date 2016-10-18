package org.eclipse.osee.ote.message.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.eclipse.osee.framework.jdk.core.util.io.StringOutputStream;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Utility class that will save or load an {@link ElementSaveFile}. 
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class OTEJsonSaveFile {

   /**
    * Load a JSON representation of an ElementSaveFile into a {@link ElementSaveFile} object.
    * 
    * @param file
    * @return
    * @throws JsonParseException
    * @throws JsonMappingException
    * @throws IOException
    */
   public static ElementSaveFile loadSaveFile(File file) throws JsonParseException, JsonMappingException, IOException{
      FileInputStream is = null;
      ElementSaveFile config = null;
      try {
         is = new FileInputStream(file);
         ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(is, ElementSaveFile.class);
      } finally {
         if (is != null) {
            try {
               is.close();
            } catch (IOException ex) {
            }
         }
      }
      return config;
   }
   
   /**
    * Load a JSON representation of an ElementSaveFile into a {@link ElementSaveFile} object.
    * 
    * @param file
    * @return
    * @throws JsonParseException
    * @throws JsonMappingException
    * @throws IOException
    */
   public static ElementSaveFile loadSaveFile(InputStream stream) throws JsonParseException, JsonMappingException, IOException{
      ElementSaveFile config = null;
      ObjectMapper mapper = new ObjectMapper();
      config = mapper.readValue(stream, ElementSaveFile.class);
      return config;
   }
   
   /**
    * Load a JSON representation of an ElementSaveFile into a {@link ElementSaveFile} object.
    * 
    * @param file
    * @return
    * @throws JsonParseException
    * @throws JsonMappingException
    * @throws IOException
    */
   public static ElementSaveFile loadSaveFile(String saveFile) throws JsonParseException, JsonMappingException, IOException{
      ElementSaveFile config = null;
      ObjectMapper mapper = new ObjectMapper();
      config = mapper.readValue(saveFile, ElementSaveFile.class);
      return config;
   }
   
   /**
    * Write a {@link ElementSaveFile} object to the filesystem as JSON.
    * 
    * @param file
    * @param data
    * @throws FileNotFoundException
    */
   public static void writeSaveFile(File file, ElementSaveFile data) throws FileNotFoundException{
      FileOutputStream os = null;
      try {
         os = new FileOutputStream(file);
         ObjectMapper mapper = new ObjectMapper();
         try{
            mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
            mapper.writeValue(os, data);
         } catch (Exception ex){
            OseeLog.log(OTEJsonSaveFile.class, Level.SEVERE, ex);
         }
      } finally {
         if (os != null) {
            try {
               os.close();
            } catch (IOException ex) {
            }
         }
      }
   }
   
   /**
    * Write a {@link ElementSaveFile} object to the filesystem as JSON.
    * 
    * @param file
    * @param data
    * @throws FileNotFoundException
    */
   public static String writeSaveFile(ElementSaveFile data) {
      StringOutputStream os = null;
      try {
         os = new StringOutputStream();
         ObjectMapper mapper = new ObjectMapper();
         try{
            mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
            mapper.writeValue(os, data);
         } catch (Exception ex){
            OseeLog.log(OTEJsonSaveFile.class, Level.SEVERE, ex);
         }
      } finally {
         if (os != null) {
            os.close();
         }
      }
      if(os != null){
         return os.toString();
      }
      return "";
   }
   
   public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException{
      File testFile = new File("C:\\userdata\\test.xyplot");
      
      ElementSaveFile data = new ElementSaveFile();
      ElementSave el = new ElementSave();
      ElementSave el2 = new ElementSave();
      el.setPath("osee.test.core.message.pubsub.test1+el1");
      el2.setPath("osee.test.core.message.pubsub.test1+el2");
      for(int i = 0; i < 20; i++){
         el.getData().getTime().add((double)i);
         el.getData().getValue().add((double)i * 0.5);
         el2.getData().getTime().add((double)i);
         el2.getData().getValue().add((double)i * 2);
      }
      el.getConfig().put("tool1.param1", "true");
      el.getConfig().put("tool1.param3", "1.89");
      el.getConfig().put("tool3.param2", "false");
      data.getElements().add(el);
      data.getElements().add(el2);
      OTEJsonSaveFile.writeSaveFile(testFile, data);
      
      OTEJsonSaveFile.loadSaveFile(testFile);
      
      System.out.println("success: " + testFile.getAbsolutePath());
      
   }
   
}
