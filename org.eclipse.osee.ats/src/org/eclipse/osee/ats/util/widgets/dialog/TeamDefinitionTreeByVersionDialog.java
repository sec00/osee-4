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

package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.XListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionTreeByVersionDialog extends TeamDefinitionTreeDialog {

   XListViewer versionList = new XListViewer("Version");
   VersionArtifact selectedVersion = null;
   private Button okButton;

   /**
    * @param active
    */
   public TeamDefinitionTreeByVersionDialog(Active active) throws OseeCoreException {
      super(active);
      setInput(TeamDefinitionArtifact.getTeamReleaseableDefinitions(active));
   }

   @Override
   protected Control createDialogArea(Composite container) {

      super.createDialogArea(container);
      getTreeViewer().addCheckStateListener(new ICheckStateListener() {
         /* (non-Javadoc)
          * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent)
          */
         public void checkStateChanged(CheckStateChangedEvent event) {
            try {
               Collection<Object> objs = new HashSet<Object>();
               Object objects[] = getTreeViewer().getCheckedElements();
               for (Object object : objects) {
                  for (Artifact art : ((TeamDefinitionArtifact) object).getVersionsFromTeamDefHoldingVersions(VersionReleaseType.Both))
                     objs.add(art);
               }
               versionList.setInput(objs);
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(1, false));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      versionList.setLabelProvider(new VersionArtifactLabelProvider());
      versionList.setContentProvider(new ArrayContentProvider());
      versionList.setSorter(new ArtifactViewerSorter(true));
      versionList.setGrabHorizontal(true);
      versionList.setMultiSelect(false);
      versionList.createWidgets(comp, 2);
      versionList.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            if (versionList.getSelected().size() == 0)
               selectedVersion = null;
            else
               selectedVersion = (VersionArtifact) versionList.getSelected().iterator().next();

            updateButtons();
         };
      });
      return container;
   }

   /**
    * @return the selectedVersion
    */
   public VersionArtifact getSelectedVersion() {
      return selectedVersion;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      okButton.setEnabled(false);
      return c;
   }

   protected boolean isComplete() {
      return selectedVersion != null;
   }

   private void updateButtons() {
      if (okButton != null) {
         okButton.setEnabled(isComplete());
      }
   }

   @Override
   protected void updateOKStatus() {
      updateButtons();
   }

}
