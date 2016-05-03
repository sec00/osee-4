package org.eclipse.osee.ote.message;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

public class BinaryMessageRecorder implements Closeable{

	static final long FILE_TYPE_MARKER = 0xBADF00DFECEFACE0L;
	static final int VERSION_SECTION_ID = 0xFEEDF00D;
	static final int MESSAGE_MAP_SECTION_ID = 0xFEEDBEEF;
	static final int MESSAGE_DATA_SECTION_ID = 0xCAFEF00D;
	
	
	public static BinaryMessageRecorder create(File file, ITimerControl timerControl) throws IOException{
		return new BinaryMessageRecorder(file, timerControl, new BinaryMessageRecorderDataCache(100, 65536));
	}
	
	public static BinaryMessageRecorder create(File file, ITimerControl timerControl, BinaryMessageRecorderDataCache cache) throws IOException{
      return new BinaryMessageRecorder(file, timerControl, cache);
   }
	
	private final class MessageListener implements IOSEEMessageListener {

		private final Message message;
		private final DataType type;
		private final int id;

		MessageListener(Message message, int id) {
			this.message = message;
			this.id = id;
			this.type = message.getDefaultMessageData().getType(); 
		}
		@Override
		public void onDataAvailable(MessageData data, DataType type)
				throws MessageSystemException {
			if (!this.type.equals(type)) {
				return;
			}
			ByteBuffer buffer = cache.takeBufferForCopy(data.getMem().getData().length + 16);
			buffer.clear();
			buffer.putLong(timerControl.getEnvTime());
			buffer.putInt(id);
			buffer.putInt(data.getCurrentLength());
			buffer.put(data.toByteArray(), 0, data.getCurrentLength());
			buffer.flip();
			cache.giveBufferForProcessing(buffer);
			messageCounter.incrementAndGet();
		}

		@Override
		public void onInitListener() throws MessageSystemException {
			
		}
		
		void start() {
			message.addListener(this);
		}
		
		void stop() {
			message.removeListener(this);
		}
		
	}
	
	private final FileChannel channel;

	private final ITimerControl timerControl;
	
	private final LinkedList<MessageListener> listeners = new LinkedList<BinaryMessageRecorder.MessageListener>();

	private int idCounter = 1000;
	
	private volatile boolean isStarted;
	
	private final AtomicInteger messageCounter = new AtomicInteger(0);
	private final File destinationFile;
   private long finalSize;
   private BinaryMessageRecorderDataCache cache;
   private Thread thread;
	
	@SuppressWarnings("resource")
   public BinaryMessageRecorder(File destinationFile, ITimerControl timeControl, BinaryMessageRecorderDataCache cache) throws FileNotFoundException {
	   this.destinationFile = destinationFile;
      this.channel = new FileOutputStream(destinationFile).getChannel();
      this.timerControl = timeControl;
      this.cache = cache;
      
   }

   public synchronized void addMessage(Message message) {
		if (isStarted) {
			throw new IllegalStateException("Recording In Progress - Can't Messages");
		}
		MessageListener listener = new MessageListener(message, idCounter++);
		listeners.add(listener);
	}

	public File getDestinationFile() {
		return destinationFile;
	}
	
	public synchronized void addMessages(Collection<Message> messages) {
		if (isStarted) {
			throw new IllegalStateException("Recording In Progress - Can't Messages");
		}
		for (Message message : messages) {
	 		MessageListener listener = new MessageListener(message, idCounter++);
			listeners.add(listener);
		}
	}

	public synchronized void start(String versionId) throws IOException{
		if (isStarted) {
			throw new IllegalStateException("Recording already started");
		}
		isStarted = true;
		thread = new Thread(new Runnable(){
		   @Override
		   public void run() {
		      ArrayList<ByteBuffer> dataToWrite = new ArrayList<ByteBuffer>();
		      while(isStarted){
		         cache.drainDataToProcess(dataToWrite);
		         if(dataToWrite.size() > 0){
		            for(int i = 0; i < dataToWrite.size(); i++){
		               ByteBuffer buff = dataToWrite.get(i);
		               try {
                        channel.write(buff);
                     } catch (IOException e) {
                        e.printStackTrace();
                     }
		               cache.giveBufferBack(buff);
		            }	
		            dataToWrite.clear();
		         } else {
		            try {
                     Thread.sleep(5);
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
		         }
		      }
		   }});
		thread.setName("BinaryMessageRecorderOutput-" +versionId);
		thread.start();
		writeSections(versionId);
		for (MessageListener listener : listeners) {
			listener.start();
		}		
	}


	private void stop() {
		for (MessageListener listener : listeners) {
			listener.stop();
		}		
		isStarted = false;
		try {
         thread.join(1000);
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
		ArrayList<ByteBuffer> dataToWrite = new ArrayList<ByteBuffer>();
		cache.drainDataToProcess(dataToWrite);
		for(int i = 0; i < dataToWrite.size(); i++){
		   ByteBuffer buff = dataToWrite.get(i);
		   try {
		      channel.write(buff);
		   } catch (IOException e) {
		      e.printStackTrace();
		   }
		   cache.giveBufferBack(buff);
		}
	}

	@Override
	public synchronized void close() {
		stop();
		try {
		   finalSize = channel.size();
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeSections(String versionId) throws IOException{
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024*1024);
		buffer.putLong(FILE_TYPE_MARKER);
		
		writeVersionIdSection(buffer, versionId);
		writeMessageMapSection(buffer);
		writeMessageDataSection(buffer);

		buffer.flip();
		channel.write(buffer);
	}
	
	private void writeVersionIdSection(ByteBuffer buffer, String versionId) {
		buffer.putInt(VERSION_SECTION_ID);
		byte[] versionInBytes = versionId.getBytes();
		buffer.putInt(versionInBytes.length);
		buffer.put(versionInBytes);
		
	}
	
	private void writeMessageMapSection(ByteBuffer buffer) {
		buffer.putInt(MESSAGE_MAP_SECTION_ID);
		int sizePosition = buffer.position();
		// dummy size for now
		buffer.putInt(0);
		
		for (MessageListener listener : listeners) {
			buffer.putInt(listener.id);
			byte[] classNameInBytes = listener.message.getClass().getName().getBytes();
			buffer.putInt(classNameInBytes.length);
			buffer.put(classNameInBytes);
		}
		int sectionSize = buffer.position() - sizePosition - 4;
		buffer.putInt(sizePosition, sectionSize);				
	}
	
	private void writeMessageDataSection(ByteBuffer buffer) {
		buffer.putInt(MESSAGE_DATA_SECTION_ID);
		buffer.putInt(-1);
	}
	
	public int getRecordedTransmissionsCount() {
		return messageCounter.get();
	}
	
	public long getCurrentFileSize() throws IOException{
	   if(channel.isOpen()){
	      return channel.size();
	   } else {
	      return finalSize;
	   }
	}
	
}
