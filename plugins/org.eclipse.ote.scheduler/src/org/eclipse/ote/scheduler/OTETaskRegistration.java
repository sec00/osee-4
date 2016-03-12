package org.eclipse.ote.scheduler;

public class OTETaskRegistration {
   
   private OTETask task;
   private SchedulerImpl scheduler;

   public OTETaskRegistration(SchedulerImpl scheduler, OTETask task) {
      this.task = task;
      this.scheduler = scheduler;
   }

   public void unregister(){
      task.setCanceled();
      scheduler.removeTask(task);
   }

   public boolean isComplete() {
      return task.isComplete();
   }
}
