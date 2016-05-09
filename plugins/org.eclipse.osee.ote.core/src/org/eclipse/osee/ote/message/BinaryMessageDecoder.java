package org.eclipse.osee.ote.message;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinaryMessageDecoder {
   
   public static final long MAX_SECTION_SIZE = 26214400;//25mb
   private static final long FRAME_LENGTH_MS = 0;
   private MappedByteBuffer buffer;
   private String versionId = "";
   private Map<Integer, String> idLookup;
   private FileChannel channel;
   private long startOfDataSection;
   private long totalFileSize;
   private int numberOfDataSections;
   private long nextDataSection;
   private Map<Integer, SectionData> sectionData;
   private long sectionSize;
   private SectionData currentSectionData;
   
   public BinaryMessageDecoder() {
      idLookup = new HashMap<Integer, String>();
      sectionData = new HashMap<Integer, SectionData>();
      sectionSize = MAX_SECTION_SIZE;
   }
   
   BinaryMessageDecoder(long sectionSize) {
      this();
      this.sectionSize = sectionSize;
   }

   private long getSize(long offset){
      return sectionSize > (totalFileSize - offset) ? (totalFileSize - offset): sectionSize;
   }
   
   public void setInput(FileChannel channel) throws IOException {
      this.channel = channel;
      totalFileSize = channel.size();
      allocateBuffer(0, getSize(0));
      if(!readFileMarker()){
         throw new IOException("Failed to read the file marker");
      }
      
      if(!readVersionInformation()){
         throw new IOException("Failed to read file marker data");
      }
      if(!readMapSection()){
         throw new IOException("Failed to read the mapping section");
      }
      if(!readDataSectionStart()){
         throw new IOException("Failed to read start of data section");
      }
      calculateDataSectionInfo();
   }
   
   private boolean readDataSectionStart(){
      if(buffer.getInt() == BinaryMessageRecorder.MESSAGE_DATA_SECTION_ID){
         int spare = buffer.getInt();   
         if(spare == -1){
            nextDataSection += 8;
            return true;
         }
      }
      return false;
   }
   
   private void calculateDataSectionInfo() throws IOException{
      calculateDataSections();
      numberOfDataSections = sectionData.size();
   }
   
   private void allocateBuffer(long offset, long size) throws IOException{
      buffer = channel.map(MapMode.READ_ONLY, offset, size);
   }
   
   /**
    * @return false if we did not read a valid file marker
    */
   private boolean readFileMarker(){
      if(buffer.remaining() >= 8){
         long fileTypeMarker = buffer.getLong();
         if(BinaryMessageRecorder.FILE_TYPE_MARKER == fileTypeMarker){
            return true;
         }
      } 
      return false;
   }

   private boolean readVersionInformation() {
      if(buffer.remaining() >= 8){
         if(buffer.getInt() == BinaryMessageRecorder.VERSION_SECTION_ID){
            int versionSize = buffer.getInt();
            byte[] versionInBytes = new byte[versionSize];
            if(buffer.remaining() >= versionSize){
               buffer.get(versionInBytes);
               versionId  = new String(versionInBytes);
            }
            return true;
         }
      }
      return false;
   }

   public boolean readMapSection() {
      if(buffer.remaining() >= 8){
         if(buffer.getInt() == BinaryMessageRecorder.MESSAGE_MAP_SECTION_ID){
            int sectionSize = buffer.getInt();
            int endOfMapping = buffer.position() + sectionSize;
            while(buffer.position() < endOfMapping){
               int id = buffer.getInt();
               int stringSize = buffer.getInt();
               byte[] nameInBytes = new byte[stringSize];
               buffer.get(nameInBytes);
               String clazz = new String(nameInBytes);
               idLookup.put(id, clazz);
            }
            startOfDataSection = endOfMapping;
            nextDataSection = startOfDataSection;
            return true;
         }
      }
      return false;
   }

   public long getDataSectionSize(int trackNumber) {
      SectionData data = sectionData.get(trackNumber);
      if(data != null){
         return data.length;
      }
      return 0;
   }

   public int transitionToSection(int destinationSection) throws IOException {
      currentSectionData = sectionData.get(destinationSection);
      if(currentSectionData == null){
         return 0;
      } else {
         allocateBuffer(currentSectionData.offset, currentSectionData.length);
         return currentSectionData.indices.size();
      }
   }
   
   private void calculateDataSections() throws IOException {
      for(int i = 0; nextDataSection < totalFileSize; i++){
         boolean notLastMessage = true;
         long nextTime = -1;
         SectionData data = new SectionData();
         sectionData.put(i, data);
         allocateBuffer(nextDataSection, getSize(nextDataSection));
         data.offset = nextDataSection;
         data.indices.add(0);
         while(buffer.remaining() > 16 && notLastMessage){
            int safePosition = buffer.position();
            long time = buffer.getLong();
            if(nextTime == -1){
               nextTime = time + FRAME_LENGTH_MS;
            } else if (time >= nextTime){
               data.indices.add(safePosition);
               nextTime = time + FRAME_LENGTH_MS;
            }
            @SuppressWarnings("unused")
            int id = buffer.getInt();
            int length = buffer.getInt();
            if(buffer.remaining() >= length){
               buffer.position(buffer.position() + length);
               data.messagesInSection++;
            } else {
               buffer.position(safePosition);
               notLastMessage = false;
            }
         }
         nextDataSection += buffer.position();
         data.length = buffer.position();
      }
   }
   
   public void playFrame(int index, BinaryDecoderWorker worker) {
      commonPlay(index, false, worker);
   }
   
   public void scanFrame(int index, BinaryDecoderWorker worker) {
      commonPlay(index, true, worker);
   }
   
   private void commonPlay(int index, boolean scan, BinaryDecoderWorker worker){
      if(currentSectionData != null){
         if(index < currentSectionData.indices.size()){
            int endPosition = 0;
            buffer.position(currentSectionData.indices.get(index));
            if((index+1) < currentSectionData.indices.size()){
               endPosition = currentSectionData.indices.get(index+1)-1;
            } else {
               endPosition = buffer.limit();
            }
            
            while(buffer.position() < endPosition){
               long time = buffer.getLong();
               int id = buffer.getInt();
               int length = buffer.getInt();
               if(scan){
                  worker.scan(time, idLookup.get(id), buffer, length);
               } else {
                  worker.play(time, idLookup.get(id), buffer, length);
               }
               buffer.position(buffer.position() + length);
            }
         }
      }
   }

   public String getVersionId(){
      return versionId;
   }
   
   long getStartOfDataSection(){
      return startOfDataSection;
   }
   
   int getMappedMessageCount(){
      return idLookup.size();
   }

   public int getNumberOfDataSections() {
      return numberOfDataSections;
   }

   static class SectionData {
      public int length;
      long offset;
      List<Integer> indices = new ArrayList<Integer>();
      int messagesInSection;
      
   }

   SectionData getSectionData(int i) {
      return sectionData.get(i);
   }

   public Collection<String> getMessages() {
      return idLookup.values();
   }

}
