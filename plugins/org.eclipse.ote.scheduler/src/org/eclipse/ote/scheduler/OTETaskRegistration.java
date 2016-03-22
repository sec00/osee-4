package org.eclipse.ote.scheduler;

public class OTETaskRegistration {
   
   private OTETask task;
   private SchedulerImpl scheduler;

   public OTETaskRegistration(SchedulerImpl scheduler, OTETask task) {
      this.task = task;
      this.scheduler = scheduler;
   }

   public void unregister(){
//      System.out.println("unregister " + scheduler.getTime() + " " + task.toString());
      task.setCanceled();
      scheduler.removeTask(task);
   }

   public boolean isComplete() {
      return task.isComplete();
   }
}
