/*
 * Created on Oct 25, 2006
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;

/**
 * This is a helper class that stores the periodic publish tasks in a map based on the rate and phase of a message.
 * Currently we're ignoring the phase, by setting it to zero, because we determined it was not important, this may
 * change in the future.
 *
 * @author Andrew M. Finkbeiner
 */
public class PeriodicPublishMap {
   private final CompositeKeyHashMap<Double, Integer, PeriodicPublishTask> ratePhaseMap =
         new CompositeKeyHashMap<Double, Integer, PeriodicPublishTask>(32, false);
   private IMessageManager messageManager;
   private ITimerControl timerControl;

   public PeriodicPublishMap(IMessageManager messageManager, ITimerControl timerControl){
      this.messageManager = messageManager;
      this.timerControl = timerControl;
   }
   
   public void clear() {
      for (PeriodicPublishTask task : ratePhaseMap.values()) {
         task.clear();
      }
      ratePhaseMap.clear();
   }

   public PeriodicPublishTask get(double rate, int phase) {
      phase = 0;// TODO ignoring phase, delete to stop ignoring
      PeriodicPublishTask task = ratePhaseMap.get(rate, phase);
      if(task == null){
         task = new PeriodicPublishTask(messageManager, rate, phase);
         timerControl.addTask(task, null);
         ratePhaseMap.put(rate, phase, task);
      }
      return task;
   }

   public PeriodicPublishTask put(double rate, int phase, PeriodicPublishTask task) {
      phase = 0;// TODO ignoring phase, delete to stop ignoring
      ratePhaseMap.put(rate, phase, task);
      return task;
   }

   public boolean containsKey(double rate, int phase) {
      phase = 0;// TODO ignoring phase, delete to stop ignoring
      return ratePhaseMap.containsKey(rate, phase);
   }

   public PeriodicPublishTask[] getTasks() {
      return ratePhaseMap.values().toArray(new PeriodicPublishTask[ratePhaseMap.size()]);
   }

//   /* (non-Javadoc)
//    * @see ote.lba.message.manager.IMpStatusEvent#onMpOn()
//    */
//   public void onMpOn() {
//      OseeLog.log(RuntimePlugin.class, Level.FINE, "periodic publish on");
//      for (PeriodicPublishTask task : ratePhaseMap.values()) {
//         task.setPublish(true);
//      }
//   }
//
//   /* (non-Javadoc)
//    * @see ote.lba.message.manager.IMpStatusEvent#onMpOff()
//    */
//   public void onAllMpOff() {
//      OseeLog.log(RuntimePlugin.class, Level.FINE, "periodic publish off");
//      for (PeriodicPublishTask task : ratePhaseMap.values()) {
//         task.setPublish(false);
//      }
//   }
//
//   public void onAnyMpOff() {
//      // Included because of IMpStatusListener interface; empty because there is no use for it here.
//   }

   public void onMessageSubscriptionComplete() {
      // Included because of IMpStatusListener interface; empty because there is no use for it here.
   }

   /**
    * @return the ratePhaseMap
    */
   public CompositeKeyHashMap<Double, Integer, PeriodicPublishTask> getRatePhaseMap() {
      return ratePhaseMap;
   }

}
