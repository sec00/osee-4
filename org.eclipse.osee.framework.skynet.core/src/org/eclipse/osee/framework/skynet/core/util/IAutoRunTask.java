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

   public void startTasks(StringBuffer results) throws Exception;

   public int getHourStartTime();

   public int getMinuteStartTime();

   public String[] getNotificationEmailAddresses();

}
