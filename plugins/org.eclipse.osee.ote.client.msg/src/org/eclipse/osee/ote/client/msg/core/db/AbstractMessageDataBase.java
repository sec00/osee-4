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
package org.eclipse.osee.ote.client.msg.core.db;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.client.msg.core.internal.MessageReference;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.message.ClassLocator;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageController;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.ote.scheduler.Scheduler;

/**
 * @author Ken J. Aguilar
 */
public abstract class AbstractMessageDataBase {

	private final HashMap<MessageReference, MessageInstance> referenceToMsgMap =
			new HashMap<MessageReference, MessageInstance>();
	private final ConcurrentHashMap<Integer, MessageInstance> idToMsgMap =
			new ConcurrentHashMap<Integer, MessageInstance>();

	private IMsgToolServiceClient client;
	private volatile boolean connected = false;
	private MessageController messageController;
	
	ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	
	ITimerControl fakeTimerControl = new ITimerControl() {
      
      @Override
      public void step() {
      }
      
      @Override
      public ICancelTimer setTimerFor(ITimeout objToNotify, int milliseconds) {
         return null;
      }
      
      @Override
      public void setRunManager(IRunManager runManager) {
      }
      
      @Override
      public void setCycleCount(int cycle) {
      }
      
      @Override
      public void removeTask(EnvironmentTask task) {
      }
      
      @Override
      public boolean isRealtime() {
         return false;
      }
      
      @Override
      public void incrementCycleCount() {
      }
      
      @Override
      public long getTimeOfDay() {
         return 0;
      }
      
      @Override
      public IRunManager getRunManager() {
         return null;
      }
      
      @Override
      public long getEnvTime() {
         return 0;
      }
      
      @Override
      public int getCycleCount() {
         return 0;
      }
      
      @Override
      public void envWait(int milliseconds) throws InterruptedException {
      }
      
      @Override
      public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
      }
      
      @Override
      public void dispose() {
      }
      
      @Override
      public void cancelTimers() {
      }
      
      @Override
      public void cancelAllTasks() {
      }
      
      @Override
      public void addTask(EnvironmentTask task, TestEnvironment environment) {
      }

      @Override
      public Scheduler getScheduler() {
         // TODO Auto-generated method stub
         return null;
      }

   };
   private IMessageRequestor req;
   
	protected AbstractMessageDataBase(IMsgToolServiceClient service) {
	   client = service;
	   messageController = new MessageController(new ClassLocator() {
         ExportClassLoader loader = ExportClassLoader.getInstance();
         @Override
         public Class<?> findClass(String name) throws ClassNotFoundException {
            return loader.loadClass(name);
         }
      }, fakeTimerControl, null, null);
	   req = messageController.createRequestor("messageDb");
	   
	   timer.scheduleWithFixedDelay(new Runnable(){
         @Override
         public void run() {
            try{
            resubscriber();
            } catch (Throwable th){
               th.printStackTrace();
            }
         }
	   }, 30, 30, TimeUnit.SECONDS);
	}

	public synchronized MessageInstance findInstance(String name, MessageMode mode, DataType type) {
		MessageReference reference = new MessageReference(type, mode, name);
		return referenceToMsgMap.get(reference);
	}

	public MessageInstance acquireInstance(String name) throws Exception {
		return acquireInstance(name, MessageMode.READER, (DataType) null);
	}

	public synchronized MessageInstance acquireInstance(String name, MessageMode mode, DataType type) throws Exception {
		if (type == null) {
		   type = messageController.getMessageClass(name).newInstance().getDefaultMessageData().getType();
		}
		
		
		
		MessageReference reference = new MessageReference(type, mode, name);
		MessageInstance instance = referenceToMsgMap.get(reference);
		if (instance == null) {
		   Message msg;
		   if(mode == MessageMode.READER){
		      msg = req.getMessageReader(name);
		   } else {
		      msg = req.getMessageWriter(name);
		   }
			msg.setMemSource(type);
			instance = new MessageInstance(msg, mode, type);
			referenceToMsgMap.put(reference, instance);
		}
		instance.incrementReferenceCount();
		if (connected && !instance.isAttached()) {
			doInstanceAttach(instance);
		}
		return instance;
	}

	public MessageInstance acquireInstance(String name, MessageMode mode, String dataType) throws Exception {
	   Message msg = req.getMessageReader(name);
		Set<DataType> available = msg.getAssociatedMessages().keySet();
		DataType requestDataType = msg.getDefaultMessageData().getType();
		for (DataType type : available) {
			if (type.name().equals(dataType)) {
				requestDataType = type;
				break;
			}
		}
		MessageReference reference = new MessageReference(requestDataType, mode, name);
		MessageInstance instance = referenceToMsgMap.get(reference);
		if (instance == null) {
			msg.setMemSource(requestDataType);
			instance = new MessageInstance(msg, mode, requestDataType);
			referenceToMsgMap.put(reference, instance);
		}
		instance.incrementReferenceCount();
		if (connected && !instance.isAttached()) {
			doInstanceAttach(instance);
		}
		return instance;
	}


	public void releaseInstance(MessageInstance instance) throws Exception {
		instance.decrementReferenceCount();
		if (!instance.hasReferences()) {
			if (instance.isAttached()) {
				doInstanceDetach(instance);
			}
			MessageReference reference =
					new MessageReference(instance.getType(), instance.getMode(), instance.getMessage().getClass().getName());
			referenceToMsgMap.remove(reference);
			req.remove(instance.getMessage());
		}

	}

	public synchronized void attachToService(IMsgToolServiceClient client) {
	   connected = true;
		this.client = client;
		for (MessageInstance instance : referenceToMsgMap.values()) {
			try {
				doInstanceAttach(instance);
			} catch (Exception e) {
				OseeLog.log(AbstractMessageDataBase.class, Level.SEVERE,
						"could not attach instance for " + instance.toString(), e);
			}
		}
	}
	
	public synchronized void resubscriber(){
	   if(connected && client != null){
	      for (MessageInstance instance : referenceToMsgMap.values()) {
	         try {
	            if(!instance.isConnected() && instance.isSupported()){
	               doInstanceAttach(instance);
	            }
	         } catch (Exception e) {
	            OseeLog.log(AbstractMessageDataBase.class, Level.SEVERE,
	                  "could not attach instance for " + instance.toString(), e);
	         }
	      }
	   }
	}

	public synchronized void detachService() {
		for (MessageInstance instance : referenceToMsgMap.values()) {
			doInstanceDetach(instance);
		}
		connected = false;
		this.client = null;
	}

	public MessageInstance findById(int id) {
		return idToMsgMap.get(id);
	}

	private boolean doInstanceAttach(MessageInstance instance) throws Exception {
	   new Thread(new Runnable() {
         @Override
         public void run() {
            Integer id;
            try {
               id = instance.attachToService(client);
               if (id != null) {
                  idToMsgMap.put(id, instance);
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }).start();
		return true;
	}

	private void doInstanceDetach(MessageInstance instance) {
		try {
			Integer id = instance.getId();
			if (id != null) {
				idToMsgMap.remove(id);
			}
			instance.detachService(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
