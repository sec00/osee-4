java -Dorg.osgi.service.http.port=8015 \
-Dosgi.compatibility.bootdelegation=true \
-Xmx512m \
-Dosee.db.connection.id=oracle8Client \
-Dequinox.ds.debug=true \
-Dosee.application.server.data=/lba_oseex/osee_backup/datastore_lba8_fs \
-Dosee.check.tag.queue.on.startup=true \
-jar org.eclipse.osgi_3.4.0.v20080326.jar -console 