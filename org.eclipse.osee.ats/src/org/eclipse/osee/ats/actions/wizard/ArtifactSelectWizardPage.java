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
package org.eclipse.osee.ats.actions.wizard;

import java.sql.SQLException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class ArtifactSelectWizardPage extends WizardPage {

   private ListViewer artTypeList;
   private ListViewer artList;
   private Artifact selectedArtifact;
   private boolean showArtData = false;

   public ArtifactSelectWizardPage() {
      super("Select an Artifact");
   }

   public void createControl(Composite parent) {
      setTitle("Select an Artifact");

      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gl = new GridLayout();
      gl.numColumns = 2;
      composite.setLayout(gl);
      GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
      composite.setLayoutData(gd);

      Composite leftComp = new Composite(composite, SWT.NONE);

      leftComp.setLayout(new GridLayout());
      gd = new GridData(GridData.FILL, GridData.FILL, true, true);
      leftComp.setLayoutData(gd);

      (new Label(leftComp, SWT.NONE)).setText("Artifact Type");

      try {
         artTypeList = new ListViewer(leftComp, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
         artTypeList.setContentProvider(new ArrayContentProvider());
         artTypeList.setLabelProvider(new ArtTypeLabelProvider());

         gd = new GridData(GridData.FILL, GridData.FILL, true, true);
         gd.heightHint = 300;
         gd.widthHint = 200;
         artTypeList.getControl().setLayoutData(gd);
         artTypeList.setInput(ConfigurationPersistenceManager.getInstance().getValidArtifactTypes(
               BranchPersistenceManager.getInstance().getAtsBranch()));
         artTypeList.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
               IStructuredSelection selection = (IStructuredSelection) event.getSelection();
               ArtifactSubtypeDescriptor desc = (ArtifactSubtypeDescriptor) selection.getFirstElement();
               try {
                  artList.setInput(ArtifactPersistenceManager.getInstance().getArtifactsFromSubtypeName(desc.getName(),
                        BranchPersistenceManager.getInstance().getAtsBranch()));
               } catch (SQLException ex) {
                  OSEELog.logException(AtsPlugin.class, ex, false);
               }
            }
         });
         artTypeList.setSorter(new ViewerSorter() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
               return getComparator().compare(((ArtifactSubtypeDescriptor) e1).getName(),
                     ((ArtifactSubtypeDescriptor) e2).getName());
            }
         });

         Composite rightComp = new Composite(composite, SWT.NONE);

         rightComp.setLayout(new GridLayout());
         gd = new GridData(GridData.FILL, GridData.FILL, true, true);
         rightComp.setLayoutData(gd);

         Label lab = new Label(rightComp, SWT.NONE);
         lab.setText("Artifact (click here for artifact data)");
         lab.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
         lab.addListener(SWT.MouseUp, new Listener() {
            public void handleEvent(Event event) {
               showArtData = !showArtData;
               artList.refresh();
            }
         });

         artList = new ListViewer(rightComp, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
         artList.setContentProvider(new ArrayContentProvider());
         artList.setLabelProvider(new ArtifactDescriptiveLabelProvider());

         artList.setSorter(new ViewerSorter() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
               return getComparator().compare(((Artifact) e1).getDescriptiveName(),
                     ((Artifact) e2).getDescriptiveName());
            }
         });
         gd = new GridData(GridData.FILL, GridData.FILL, true, true);
         gd.heightHint = 300;
         gd.widthHint = 200;
         artList.getControl().setLayoutData(gd);
         artList.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
               IStructuredSelection selection = (IStructuredSelection) event.getSelection();
               selectedArtifact = (Artifact) selection.getFirstElement();
            }
         });
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }

      setControl(composite);
   }

   public Artifact getSelectedArtifact() {
      return selectedArtifact;
   }

   public class ArtifactDescriptiveLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         Artifact art = (Artifact) arg0;
         if (showArtData)
            return String.format("%s - (%s  %s  %s)", art.getDescriptiveName(), art.getArtId(),
                  art.getHumanReadableId(), art.getGuid());
         else
            return art.getDescriptiveName();
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }
   }

   public class ArtTypeLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         return ((ArtifactSubtypeDescriptor) arg0).getName();
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }
   }

}
