package org.eclipse.osee.ote.io.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.eclipse.osee.ote.io.OTEServerFolder;
import org.eclipse.osee.ote.io.SystemOutput;
import org.eclipse.osee.ote.io.SystemOutputListener;
import org.eclipse.osee.ote.properties.OtePropertiesCore;

public class SystemOutputImpl implements SystemOutput {

   private static final String SYSTEM_OUT_FILE = "systemout.txt";

   private PrintStream oldErr;
   private PrintStream oldOut;
   private InputStream oldIn;
   private SpecializedOut out;
   private BufferedOutputStream outputStream;
   private SpecializedInputStream in;
   private String newline;

   private OTEServerFolder serverFolder;
   
   public void start() {
      oldErr = System.err;
      oldOut = System.out;
      oldIn = System.in;
      String ioRedirect = OtePropertiesCore.ioRedirect.getValue();
      if(ioRedirect != null){
         if(Boolean.parseBoolean(ioRedirect)){
            String ioRedirectFile = OtePropertiesCore.ioRedirectFile.getValue();
            if(ioRedirectFile != null){
               if(Boolean.parseBoolean(ioRedirectFile)){
                  String workingDirectory = serverFolder.getCurrentServerFolder().getAbsolutePath();
                  File wd = new File(workingDirectory);
                  if(wd.exists() && wd.isDirectory()){
                     try {
                        outputStream = new BufferedOutputStream(new FileOutputStream(new File(workingDirectory, SYSTEM_OUT_FILE)));
                     } catch (FileNotFoundException e) {
                     }
                  }
               }
            }
            out = new SpecializedOut(new SpecializedOutputStream(outputStream == null ? oldOut : outputStream));
            newline = OtePropertiesCore.lineSeparator.getValue();
            in = new SpecializedInputStream(oldIn);
            System.setIn(in);
            System.setOut(out);
            System.setErr(out);      
         }
      }
   }

   public void stop(){
      if(out != null){
         out.flush();    
      }
   }
   
   public void bindOTEServerFolder(OTEServerFolder folder) {
      this.serverFolder = folder;
   }
   
   public void unbindOTEServerFolder(OTEServerFolder folder) {
      this.serverFolder = null;
   }
   
   @Override
   public void addListener(SystemOutputListener listener){
      if(out != null){
         out.addListener(listener);
      }
   }
   
   @Override
   public void removeListener(SystemOutputListener listener){
      if(out != null){
         out.removeListener(listener);
      }
   }
   
   @Override
   public synchronized void write(String input){
      if(in != null){
         System.out.println(input);
         in.add(input+newline);
      }
   }
   
   public void resetIO(){
      System.setIn(oldIn);
      System.setOut(oldOut);
      System.setErr(oldErr);
   }
   
}
