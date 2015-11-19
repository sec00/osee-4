/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.search;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public final class LoadSearchItemAction extends Action {

   private final AtsSearchWorkflowSearchItem searchItem;
   private final AtsSearchData data;

   public LoadSearchItemAction(AtsSearchData data, AtsSearchWorkflowSearchItem searchItem) {
      this.data = data;
      this.searchItem = searchItem;
   }

   @Override
   public String getText() {
      return data.getSearchName();
   }

   @Override
   public void run() {
      searchItem.loadWidgets(data);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      AtsImage image = AtsImage.SEARCH;
      if (data.getNamespace().equals(AtsSearchTeamWorkflowSearchItem.NAMESPACE)) {
         image = AtsImage.TEAM_WORKFLOW;
      }
      return ImageManager.getImageDescriptor(image);
   }

};
