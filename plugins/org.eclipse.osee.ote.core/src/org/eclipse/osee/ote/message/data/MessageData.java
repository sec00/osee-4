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
package org.eclipse.osee.ote.message.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.IMessageSendListener;
import org.eclipse.osee.ote.message.IOType;
import org.eclipse.osee.ote.message.LegacyMessageMapper;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.properties.OtePropertiesCore;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageData {

   private LegacyMessageMapper mapper;
   
   private static long debugTimeout = OtePropertiesCore.timeDebugTimeout.getLongValue();
   private static boolean debugTime = OtePropertiesCore.timeDebug.getBooleanValue();

   private final CopyOnWriteNoIteratorList<IMessageSendListener> messageSendListeners = new CopyOnWriteNoIteratorList<IMessageSendListener>(IMessageSendListener.class);

   private final MemoryResource mem;
   private final String typeName;
   private final String name;
   private final int defaultDataByteSize;
   private final DataType memType;
   private long activityCount = 0;
   private long sentCount;
   private int currentLength;
   private boolean isScheduled = false;
   private long time = -1;

   private boolean isWriter = false;
   private Map<Class<?>, Pair<Message, MemoryResource>>overrideMessages = new HashMap<Class<?>, Pair<Message, MemoryResource>>();

   private boolean isWrapbackEnabled = true;

   private IOType ioType;
   
   public MessageData(String typeName, String name, int dataByteSize, int offset, DataType memType, IOType ioType) {
      mem = new MemoryResource(new byte[dataByteSize], offset, dataByteSize - offset);
      this.typeName = typeName;
      this.name = name;
      this.defaultDataByteSize = dataByteSize;
      this.currentLength = dataByteSize;
      this.memType = memType;
      this.ioType = ioType;
   }

   public MessageData(String typeName, String name, MemoryResource mem, DataType memType, IOType ioType) {
      this.mem = mem;
      this.typeName = typeName;
      this.name = name;
      this.defaultDataByteSize = mem.getLength();
      this.currentLength = mem.getLength();
      this.memType = memType;
      this.ioType = ioType;
      GCHelper.getGCHelper().addRefWatch(this);
   }

   public MessageData(String name, int dataByteSize, int offset, DataType memType, IOType ioType) {
      this(name, name, dataByteSize, offset, memType, ioType);
   }

   public MessageData(byte[] data, int dataByteSize, int offset) {
      this.mem = new MemoryResource(data, offset, dataByteSize - offset);
      this.typeName = "";
      this.name = "";
      this.defaultDataByteSize = dataByteSize;
      this.currentLength = dataByteSize;
      this.memType = null;
      this.ioType = null;
      GCHelper.getGCHelper().addRefWatch(this);
   }

   public MessageData(MemoryResource memoryResource) {
      this("", memoryResource);
   }

   public MessageData(String name, MemoryResource memoryResource) {
      this.mem = memoryResource;
      this.typeName = "";
      this.name = name;
      this.defaultDataByteSize = memoryResource.getLength();
      this.currentLength = memoryResource.getLength();
      this.memType = null;
      this.ioType = null;
      GCHelper.getGCHelper().addRefWatch(this);
   }
   
   //TODO move this to the message package and make it package private
   public void setMapper(LegacyMessageMapper mapper){
      this.mapper = mapper;
   }

   public IMessageHeader getMsgHeader(){
      return null;
   }

   public DataType getType() {
      return memType;
   }
   
   public IOType getIOType() {
      return ioType;
   }

   /**
    * Returns the number of byte words in the payload of this message.
    * 
    * @return the number of bytes in the message payload
    */
   public int getPayloadSize() {
      return currentLength;
   }

   public String getName() {
      return name;
   }

   /**
    * returns a list of the message that this data is a source for. <BR>
    * 
    * @return a collection of messages
    */
   public Collection<Message> getMessages() {
      checkMapper();
      return mapper.getMessages(this).fillCollection(new ArrayList<Message>());
   }

   /**
    * @return Returns the activityCount.
    */
   public long getActivityCount() {
      return activityCount;
   }

   /**
    * @param activityCount The activityCount to set.
    */
   public void setActivityCount(long activityCount) {
      this.activityCount = activityCount;
   }

   public void incrementActivityCount() {
      activityCount++;
   }

   public void incrementSentCount() {
      sentCount++;
   }

   public long getSentCount() {
      return sentCount;
   }

   public boolean isEnabled() {
      return true;
   }

   public void visit(IMessageDataVisitor visitor){
   }

   public void dispose() {
      if(mapper != null){
         mapper.cleanup(this);
      }
   }

   public void copyData(int destOffset, byte[] data, int srcOffset, int length) {
      setCurrentLength(length + destOffset);
      mem.copyData(destOffset, data, srcOffset, length);
   }

   public void copyData(int destOffset, ByteBuffer data, int length) throws MessageSystemException {
      try {
         setCurrentLength(destOffset + length);
         mem.copyData(destOffset, data, length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.INFO, ex,
               "increasing backing store for %s to %d. prev length: %d, recv cnt: %d", getName(), destOffset + length,
               mem.getData().length, this.activityCount);
         setNewBackingBuffer(data, destOffset, length);
      }
   }

   public void copyData(ByteBuffer data) {
      copyData(0, data, data.remaining());
   }

   /**
    * Notifies all {@link Message}s that have this registered as a data source of the update
    */
   public void notifyListeners() throws MessageSystemException {
      checkMapper();
      final DataType memType = getType();
      Message[] ref = mapper.getMessages(this).get();
      for (int i = 0; i < ref.length; i++) {
         Message message = ref[i];
         try {
            if (!message.isDestroyed()) {
               message.notifyListeners(this, memType);
            }
         } catch (Throwable t) {
            final String msg =
                  String.format("Problem during listener notification for message %s. Data=%s, MemType=%s",
                        message.getName(), this.getName(), this.getType());
            OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, msg, t);
         }
      }
   }

   /**
    * @return the currentLength
    */
   public int getCurrentLength() {
      return currentLength;
   }

   /**
    * @param currentLength the currentLength to set
    */
   public void setCurrentLength(int currentLength) {
      this.currentLength = currentLength;
   }

   /**
    * Override this method if you need to set some default data in the backing buffer.
    */
   public void setNewBackingBuffer(byte[] data) {
      setCurrentLength(data.length);
      this.mem.setData(data);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }

   }

   public void setNewBackingBuffer(ByteBuffer buffer) {
      byte[] data = new byte[buffer.remaining()];
      buffer.get(data);
      this.mem.setData(data);
      setCurrentLength(data.length);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }
   }

   public void setNewBackingBuffer(ByteBuffer buffer, int offset, int length) {
      byte[] data = new byte[offset + length];
      buffer.get(data, offset, length);
      this.mem.setData(data);
      setCurrentLength(data.length);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }

   }

   public void initializeDefaultHeaderValues(){
      
   }

   /**
    * @return the mem
    */
   public MemoryResource getMem() {
      return mem;
   }

   public int getDefaultDataByteSize() {
      return defaultDataByteSize;
   }

   public void setFromByteArray(byte[] input) {
      try {
         copyData(0, input, 0, input.length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.WARNING,

               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, input.length);
         setNewBackingBuffer(input);
      }
   }

   public void setFromByteBuffer(ByteBuffer buffer) {
      try {
         copyData(buffer);
      } catch (Exception e) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, buffer.limit());
         setNewBackingBuffer(buffer);
      }
   }

   public ByteBuffer toByteBuffer() {
      return mem.getAsBuffer();
   }

   public void setFromByteArray(byte[] input, int length) {
      try {
         copyData(0, input, 0, length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, length);
         setNewBackingBuffer(input);
      }
   }

   public void setFromByteArray(int destOffset, byte[] input, int srcOffset, int length) {
      try {
         copyData(destOffset, input, srcOffset, length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, length);
         setNewBackingBuffer(input);
      }
   }

   public void setFromByteArray(ByteBuffer input, int length) {
      try {
         copyData(0, input, length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, length);
         setNewBackingBuffer(input);
      }
   }

   public byte[] toByteArray() {
      return mem.getData();
   }

   /**
    * Override this method if you want to specialize the send criteria in a data source. For example, if you only want
    * to send data to the MUX driver if the data has changed.
    */
   public boolean shouldSendData() {
      return true;
   }

   public String getTopicName() {
      return getName();
   }

   public String getTypeName() {
      return typeName;
   }

   public Namespace getNamespace() {
      return new Namespace(getType().name());
   }

   /*
    * each type that extends DDSData needs to have it's own namespace.... we need to go through each DDSData child and
    * determine all of it's possible namespaces
    */
   public boolean isWriter() {
      return isWriter;
   }
   
   //TODO move class and make this package private  
   public void setWriter(boolean isWriter){
      this.isWriter = isWriter;
   }

   @Override
   public String toString() {
      return getClass().getName() + ": name=" + getName();
   }

   public int getOffset() {
      return 0;
   }

   /**
    * @return the isScheduled
    */
   public boolean isScheduled() {
      return isScheduled;
   }

   /**
    * @param isScheduled the isScheduled to set
    */
   public void setScheduled(boolean isScheduled) {
      this.isScheduled = isScheduled;
   }

   public void notifyPostSendListeners() {
      try {
         long start = 0, elapsed;
         IMessageSendListener[] listeners = messageSendListeners.get();
         for (int i = 0; i < listeners.length; i++) {
            IMessageSendListener listener = listeners[i];
            if(debugTime){
               start = System.nanoTime();
            }
            listener.onPostSend(this);
            if(debugTime){
               elapsed = System.nanoTime() - start;
               if(elapsed > debugTimeout){
                  Locale.setDefault(Locale.US);
                  System.out.printf("%s %s SLOW POST SEND %,d\n", getName(), listener.getClass().getName(), elapsed);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Message.class, Level.SEVERE, ex);
      }
   }

   public void notifyPreSendListeners() {
      try {
         long start = 0, elapsed;
         IMessageSendListener[] listeners = messageSendListeners.get();
         for (int i = 0; i < listeners.length; i++) {
            IMessageSendListener listener = listeners[i];
            if(debugTime){
               start = System.nanoTime();
            }
            listener.onPreSend(this);
            if(debugTime){
               elapsed = System.nanoTime() - start;
               if(elapsed > debugTimeout){
                  Locale.setDefault(Locale.US);
                  System.out.printf("%s %s SLOW PRE SEND %,d\n", getName(), listener.getClass().getName(), elapsed);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Message.class, Level.SEVERE, ex);
      }
   }

   public void addSendListener(IMessageSendListener listener) {
      messageSendListeners.add(listener);
   }

   public void removeSendListener(IMessageSendListener listener) {
      messageSendListeners.remove(listener);
   }

   public boolean containsSendListener(IMessageSendListener listener) {
      return messageSendListeners.contains(listener);
   }

   public boolean isMessageCollectionNotEmpty() {
      checkMapper();
      return mapper.getMessages(this).get().length > 0;
   }
   
   private void checkMapper(){
      if(mapper == null){
         throw new IllegalStateException("Unable to call method when mapper has not been set");
      }
   }

   public void zeroize() {
      final byte[] data = toByteArray();
      Arrays.fill(data, getMsgHeader().getHeaderSize(), data.length, (byte) 0);
   }

   /**
    * A time value associated with this message.
    * The time value will have different meanings or may not be used depending on the context and usage.
    */
   public long getTime() {
      return time;
   }

   public void setTime(long time) {
      this.time = time;
   }
   
   public void performOverride() {
      if (getMem().isDataChanged()) {
         synchronized(overrideMessages) {
            if(!overrideMessages.isEmpty()){
               for (Pair<Message, MemoryResource> override : overrideMessages.values()) {
                  byte[] overrideMsgData = override.getFirst().getData();
                  byte[] overrideMask = override.getSecond().getData();
                  if (null != overrideMsgData && null != overrideMask) {
                     byte[] targetMsgData = getMem().getData();
                     int targetMsgHeaderSize = getMem().getOffset();
                     int minLength = Math.min(targetMsgData.length - targetMsgHeaderSize, overrideMsgData.length - targetMsgHeaderSize);
                     minLength = Math.min(minLength, overrideMask.length);
                     int targetIndex;
                     int overrideIndex;
                     int overrideMsgHeaderSize = override.getFirst().getHeaderSize();
                     for (int byteIndex=0; byteIndex < minLength; byteIndex++) {
                        if (overrideMask[byteIndex] != 0x0) {
                           targetIndex = byteIndex+targetMsgHeaderSize;
                           overrideIndex = byteIndex + overrideMsgHeaderSize;
                           overrideMsgData[overrideIndex] &= overrideMask[byteIndex]; // zeroize non override regions
                           targetMsgData[targetIndex] &= ~overrideMask[byteIndex]; // zeroize regions to override
                           targetMsgData[targetIndex] = (byte) (targetMsgData[targetIndex] | overrideMsgData[overrideIndex]);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public MemoryResource getOverrideResource(Class<? extends Message> clazz) {
      MemoryResource memResource = null;
      Pair<Message, MemoryResource> override = getOverride(clazz);
      if (override != null) {
         memResource = override.getSecond();
      }
      return memResource;
   }

   public Message getOverrideMessage(Class<? extends Message> clazz) {
      Message message = null;
      Pair<Message, MemoryResource> override = getOverride(clazz);
      if (override != null) {
         message = override.getFirst();
      }
      return message;

   }

   private Pair<Message, MemoryResource> getOverride(Class<? extends Message> clazz) {
      Pair<Message, MemoryResource> override = overrideMessages.get(clazz);
      if (override == null) {
         synchronized(overrideMessages) {
            try {
               Message msg = clazz.newInstance();
               byte[] mask = new byte[msg.getMaxDataSize()];
               MemoryResource memoryResource = new MemoryResource(mask, 0, mask.length);
               override = new Pair<Message, MemoryResource>(msg, memoryResource);
               overrideMessages.put(clazz, override);
            } catch (Throwable th) {
               th.printStackTrace();
            }
         }
      }
      return override;
   }
   

   /**
    * Remove any override messages for which there are no more overridden elements 
    */
   public void cleanupOverrides() {
      synchronized(overrideMessages) {
         Set<Class<?>> keySet = overrideMessages.keySet();
         for (Class<?> clazz : keySet) {
            byte[] overrideMask = overrideMessages.get(clazz).getSecond().getData();
            boolean overrides = false;
            for (byte checkByte : overrideMask) {
               if (checkByte != (byte) 0x0) {
                  overrides = true;
                  break;
               }
            }
            if (!overrides) {
               overrideMessages.remove(clazz);
            }
         }
      }
   }
   
   /**
    * Remove all overrides
    */
   public void clearOverrides() {
      synchronized(overrideMessages) {
         overrideMessages.clear();
      }
   }

   public boolean isWrapbackEnabled() {
      return isWrapbackEnabled;
   }
   
   public void setWrapbackEnabled(boolean isWrapbackEnabled) {
      this.isWrapbackEnabled = isWrapbackEnabled;
   }
}
