package org.eclipse.osee.ote.io.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.eclipse.osee.ote.io.SystemOutputListener;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This activator will redirect System.out, System.err, and System.in if -Dote.io.redirect=true so 
 * that a client to a test server can see all of the output and send input via an eclipse client console.
 * If -Dote.io.redirect.file=true System.out and System.err will be silenced to the console/process and 
 * sent to a file in the users working directory.  This is intended to be used by utilities that want to
 * launch a test server but then do not want to deal with redirecting the IO to keep the process working. 
 * 
 */
public class Activator implements BundleActivator {

   private static final String SYSTEM_OUT_FILE = "systemout.txt";
   private static final String LINE_SEPARATOR = "line.separator";
   private static final String USER_DIR = "user.dir";
   private static final String OTE_IO_REDIRECT_FILE = "ote.io.redirect.file";
   private static final String OTE_IO_REDIRECT = "ote.io.redirect";

   private static Activator instance;
   
   private PrintStream oldErr;
   private PrintStream oldOut;
   private InputStream oldIn;
   private SpecializedOut out;
   private BufferedOutputStream outputStream;
   private SpecializedInputStream in;
   private String newline;
   
   static Activator getDefault(){
      return instance;
   }
   
   @Override
   public void start(BundleContext context) throws Exception {
      oldErr = System.err;
      oldOut = System.out;
      oldIn = System.in;
      String ioRedirect = System.getProperty(OTE_IO_REDIRECT);
      if(ioRedirect != null){
         if(Boolean.parseBoolean(ioRedirect)){
            String ioRedirectFile = System.getProperty(OTE_IO_REDIRECT_FILE);
            if(ioRedirectFile != null){
               if(Boolean.parseBoolean(ioRedirectFile)){
                  String workingDirectory = System.getProperty(USER_DIR);
                  outputStream = new BufferedOutputStream(new FileOutputStream(new File(workingDirectory, SYSTEM_OUT_FILE)));
               }
            }
            out = new SpecializedOut(new SpecializedOutputStream(outputStream == null ? oldOut : outputStream));
            newline = System.getProperty(LINE_SEPARATOR);
            in = new SpecializedInputStream(oldIn);
            System.setIn(in);
            System.setOut(out);
            System.setErr(out);      
         }
      }
      instance = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if(out != null){
         out.flush();    
      }
   }
   
   public void addListener(SystemOutputListener listener){
      if(out != null){
         out.addListener(listener);
      }
   }
   
   public void removeListener(SystemOutputListener listener){
      if(out != null){
         out.removeListener(listener);
      }
   }
   
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
