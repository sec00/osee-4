/*
 * Created on Oct 25, 2006
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;

/**
 * @author Andrew M. Finkbeiner
 */
public class PeriodicPublishTask extends EnvironmentTask implements IMessageDisposeListener {

   private final CopyOnWriteNoIteratorList<Message> periodicMessages = new CopyOnWriteNoIteratorList<Message>(Message.class);
   private IMessageManager messageManager;

   public PeriodicPublishTask(IMessageManager messageManager, double hzRate, int phase) {
      super(hzRate, phase);
      this.messageManager = messageManager;
   }

   @Override
   public void runOneCycle() throws MessageSystemException {
      try {
         Message[] msgs = periodicMessages.get();
         int size = msgs.length;
         for (int i = 0; i < size; i++){
            Message msg = msgs[i];
            if (msg != null && !msg.isDestroyed()) {
               try{
                  messageManager.publish(msg);
               } catch (Throwable th){
                  System.out.println(msg.getName());
                  th.printStackTrace();
               }
            }
         }
      } catch (Throwable th) {
         th.printStackTrace();
      }

   }

   public void put(Message msg) {
      if (msg == null) {
         return;
      }
      if (!periodicMessages.contains(msg)) {
         msg.addPreMessageDisposeListener(this);
         periodicMessages.add(msg);
      }
   }

   public void remove(Message msg) {
      msg.removePreMessageDisposeListener(this);
      periodicMessages.remove(msg);
   }

   public void clear() {
      Message[] msgs = periodicMessages.get();
      for (int i = 0; i < msgs.length; i++){
         msgs[i].removePreMessageDisposeListener(this);
      }
      periodicMessages.clear();
   }

   @Override
   public void onPostDispose(Message message) {
   }

   @Override
   public void onPreDispose(Message message) {
      periodicMessages.remove(message);
   }

}
