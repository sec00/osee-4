package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.filetransfer.TcpFileTransfer;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


public class BinaryMessageRecorderComponent implements EventHandler{

	private final HashMap<String, BinaryMessageRecorder> activeRecorders = new HashMap<String, BinaryMessageRecorder>();
	
	private ITimerControl timerControl;
	
	private IMessageRequestor requestor;
	
	private Timer timer;
	
	private ExecutorService service;
	private final TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			reportStatus();
		}
	};
	
	private void doStartRecording(BinaryRecorderParameters parameters) throws IOException{
		String filePath = parameters.getOutputFileName();
		
		if (activeRecorders.containsKey(filePath)) {
			// recording in progress
			return;
		}
		File tempFile = File.createTempFile(createTempFileName(filePath), null);
		
		BinaryMessageRecorder recorder = BinaryMessageRecorder.create(tempFile, timerControl);
		activeRecorders.put(filePath, recorder);
		String version = null;
		for (String messageClass : parameters.getMessageClasses()) {
			Message message = requestor.getMessageReader(messageClass);
			if (message == null) {
				reportCannotRecordMessage(messageClass);
				continue;
			}
			recorder.addMessage(message);
			if (version == null) {
				version = FrameworkUtil.getBundle(message.getClass()).getHeaders().get("Implementation-Version");	
			}
		}
		recorder.start(version == null ? "VERSION-NOT-SET" : version);
	}
	
	private void reportCannotRecordMessage(String className) {
		OseeLog.log(getClass(), Level.SEVERE, "Cannot get instance of " + className);
	}
	
	
	private void stopAll() {
		for (BinaryMessageRecorder recorder : activeRecorders.values()) {
			recorder.close();
		}
		activeRecorders.clear();
	}
	private void doStopRecording(String filePath, InetSocketAddress transferDestination) {
		BinaryMessageRecorder recorder = activeRecorders.remove(filePath);
		if (recorder != null) {
			recorder.close();
			transferFile(recorder.getDestinationFile(), transferDestination);
		}
	}
	
	private static String createTempFileName(String destinationFile) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		destinationFile = destinationFile.substring(0, destinationFile.lastIndexOf('.'));
		int index = destinationFile.lastIndexOf('\\');
		if (index < 0) {
			index = destinationFile.lastIndexOf('/');
		}
		if (index >= 0) {
			destinationFile = destinationFile.substring(index + 1);
		}
		return String.format("%s_%s", destinationFile, format.format(new Date()));
		
	}
	
	private synchronized void reportStatus() {
	   if(activeRecorders.size() > 0){
	      BinaryMessageRecorderStatusEvent event = new BinaryMessageRecorderStatusEvent();
	      for (Entry<String, BinaryMessageRecorder> entry : activeRecorders.entrySet()) {
	         BinaryMessageRecorder recorder = entry.getValue();
	         long size;
	         try {
	            size = recorder.getCurrentFileSize();
	         } catch (IOException e) {
	            OseeLog.log(getClass(), Level.SEVERE, "Cant get output file size", e);
	            size = -1;
	         }

	         BinaryMessageRecorderStatus status = new BinaryMessageRecorderStatus(entry.getKey(), recorder.getRecordedTransmissionsCount(), size);
	         try {
	            event.setObject(status);
	            OteEventMessageUtil.sendEvent(event);
	         } catch (IOException e) {
	            OseeLog.log(getClass(), Level.SEVERE, "Cant serialize status", e);
	            continue;
	         }			
	      }
	   }
	}
	
	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(BinaryMessageRecorderSetupEvent.TOPIC)) {
			BinaryMessageRecorderSetupEvent message = new BinaryMessageRecorderSetupEvent(OteEventMessageUtil.getBytes(event));	
			try {
				synchronized (this) {
					doStartRecording(message.getObject());					
				}
			} catch (Exception e) {
				OseeLog.log(getClass(), Level.SEVERE, "could not start recording", e);
			} 
		} else if (event.getTopic().equals(BinaryMessageRecorderStopEvent.TOPIC)) {
			BinaryMessageRecorderStopEvent message = new BinaryMessageRecorderStopEvent();	
			message.getDefaultMessageData().setNewBackingBuffer(OteEventMessageUtil.getBytes(event));
			try {
				synchronized (this) {
					doStopRecording(message.FILE_NAME.getValue(), getAddress(message));
				}
			} catch (Exception e) {
				OseeLog.log(getClass(), Level.SEVERE, "could not start recording", e);
			} 
			
		}
	}
	
	private InetSocketAddress getAddress(BinaryMessageRecorderStopEvent message) throws UnknownHostException{
		int ipAddress = message.iP_ADDRESS.getValue();
		int port = message.PORT.getValue();
		byte[] data = new byte[]{(byte)(ipAddress >>> 24), (byte) (ipAddress >>> 16), (byte) (ipAddress >>> 8), (byte) (ipAddress & 0xFF)};
		return new InetSocketAddress(InetAddress.getByAddress(data), port);
	}
	
	public synchronized void start() {
		service = Executors.newCachedThreadPool(new ThreadFactory() {
	      private int count = 0;
	      @Override
	      public Thread newThread(Runnable r) {
	         Thread th = new Thread(r);
	         th.setName(String.format("OTE binary message [%d]", count++));
	         return th;
	      }
	   });
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 1000, 1000);
	}
	
	public synchronized void stop() {
		if (service != null) {
			service.shutdown();
		}
		if (timer != null) {
			timer.cancel();
		}
	}
	
	private void transferFile(File file, InetSocketAddress address) {
		System.out.println("Preparing to transfer " + file.getAbsolutePath() + " to " + address);
		try {
			TcpFileTransfer.sendFile(service, file, address);
		} catch (IOException e) {
			OseeLog.log(getClass(), Level.SEVERE, "Failed to transfer file", e);
		}
		
	}
	
	public void setMessageManager(IMessageManager manager) {
		timerControl = ServiceUtility.getService(ITestEnvironmentAccessor.class).getTimerCtrl();
		requestor = manager.createMessageRequestor("Binary Message Recorder");
	}
	
	public void unsetMessageManager(IMessageManager manager) {
		stopAll();
		if (requestor != null) {
			requestor.dispose();
		}

	}

	public void setTestEnv(ITestEnvironmentAccessor env) {
      timerControl = env.getTimerCtrl();
   }
	
	public void unsetTestEnv(ITestEnvironmentAccessor env) {
   }
	
}
