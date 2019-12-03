/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

/**
 * @author Donald G. Dunne
 */
public enum ChangeReportTaskMatchType {

   Manual("User Manually added task.- No Change Needed."),
   Match("Computed task needed matches existing task. - No Change Needed."),
   //
   ChangedReportTaskComputedAsNeeded("Change Report task computed as needed. - Awaiting determination if task exists or Create Task"),
   StaticTaskComputedAsNeeded("Statically defined task from CreateTaskDefinition computed as needed. - Awaiting determination if task exists or Create Task"),
   //
   TaskRefAttrMissing("Task Referenced Attr was not found. - Delete Task."),
   TaskRefAttrValidButRefChgArtMissing("Task Referenced Attr found but no matching changed art. - Delete Task");

   private final String description;

   private ChangeReportTaskMatchType(String description) {
      this.description = description;
   }

   public String getDesc() {
      return description;
   }
}
