package org.eclipse.osee.ote.properties;

public enum OtePropertiesCore implements OteProperties {

   batchFolderDays("ote.batchfolder.days"),
   brokerUriPort("ote.server.broker.uri.port"),
   httpPort("org.osgi.service.http.port"),
   ioRedirectFile("ote.io.redirect.file"),
   ioRedirect("ote.io.redirect"),
   ioRedirectPath("ote.io.redirect.path"),
   javaIoTmpdir("java.io.tmpdir"),
   lineSeparator("line.separator"),
   masterURI("ote.master.uri"),
   noStacktraceFilter("org.eclipse.osee.ote.core.noStacktraceFilter"),
   outfilesLocation("osee.ote.outfiles"),
   serverFactoryClass("osee.ote.server.factory.class"),
   serverKeepalive("osee.ote.server.keepAlive"),
   serverTitle("osee.ote.server.title"),
   userHome("user.home"),
   userName("user.name"),
   useLookup("osee.ote.use.lookup");

   private String key;
   
   OtePropertiesCore(String key){
      this.key = key;
   }
   
   @Override
   public String getKey() {
      return key;
   }

   @Override
   public void setValue(String value) {
      System.setProperty(key, value);
   }

   @Override
   public String getValue() {
      return System.getProperty(key);
   }
   
   @Override
   public String getValue(String defaultValue) {
      return System.getProperty(key, defaultValue);
   }
}
