/*
 * Created on Jan 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.util;

/**
 * @author Donald G. Dunne
 */
public interface IAutoRunTask {

   public enum RunDb {
      Production_Db, Test_Db, Both
   };

   public enum TaskType {
      Regression, Db_Health_Check, Data_Exchange, Backup
   }

   public void startTasks(StringBuffer results) throws Exception;

   public int getHourStartTime();

   public int getMinuteStartTime();

   public String[] getNotificationEmailAddresses();

   public void setAutoRunUniqueId(String autoRunUniqueId);

   public String getAutoRunUniqueId();

   public TaskType getTaskType();

   public String getDescription();

   public RunDb getRunDb();

   public String getCategory();
}
