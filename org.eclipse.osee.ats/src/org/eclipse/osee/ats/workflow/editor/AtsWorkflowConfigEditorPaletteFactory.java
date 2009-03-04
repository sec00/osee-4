/*******************************************************************************
 * Copyright (c) 2004, 2008 Donald G. Dunne and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Donald G. Dunne - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.editor;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.editor.actions.ValidateDiagramToolEntry;
import org.eclipse.osee.ats.workflow.editor.model.CancelledWorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.CompletedWorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.DefaultTransitionConnection;
import org.eclipse.osee.ats.workflow.editor.model.ReturnTransitionConnection;
import org.eclipse.osee.ats.workflow.editor.model.TransitionConnection;
import org.eclipse.osee.ats.workflow.editor.model.WorkPageShape;

/**
 * Utility class that can create a GEF Palette.
 * 
 * @see #createPalette()
 * @author Donald G. Dunne
 */
final class AtsWorkflowConfigEditorPaletteFactory {

   /** Preference ID used to persist the palette location. */
   private static final String PALETTE_DOCK_LOCATION = "ShapesEditorPaletteFactory.Location";
   /** Preference ID used to persist the palette size. */
   private static final String PALETTE_SIZE = "ShapesEditorPaletteFactory.Size";
   /** Preference ID used to persist the flyout palette's state. */
   private static final String PALETTE_STATE = "ShapesEditorPaletteFactory.State";

   /** Create the "States" drawer. */
   private static PaletteContainer createStatesDrawer() {
      PaletteDrawer componentsDrawer = new PaletteDrawer("States");

      CombinedTemplateCreationEntry component =
            new CombinedTemplateCreationEntry("State", "Create a new Workflow State", WorkPageShape.class,
                  new SimpleFactory(WorkPageShape.class),
                  AtsPlugin.getInstance().getImageDescriptor("rectangle16.gif"),
                  AtsPlugin.getInstance().getImageDescriptor("rectangle24.gif"));
      componentsDrawer.add(component);

      component =
            new CombinedTemplateCreationEntry("Completed State", "Create a Completed State",
                  CompletedWorkPageShape.class, new SimpleFactory(CompletedWorkPageShape.class),
                  AtsPlugin.getInstance().getImageDescriptor("rectangle16.gif"),
                  AtsPlugin.getInstance().getImageDescriptor("rectangle24.gif"));
      componentsDrawer.add(component);

      component =
            new CombinedTemplateCreationEntry("Cancelled State", "Create a Cancelled State",
                  CancelledWorkPageShape.class, new SimpleFactory(CancelledWorkPageShape.class),
                  AtsPlugin.getInstance().getImageDescriptor("rectangle16.gif"),
                  AtsPlugin.getInstance().getImageDescriptor("rectangle24.gif"));
      componentsDrawer.add(component);

      return componentsDrawer;
   }

   /** Create the "Transitions" drawer. */
   private static PaletteContainer createTransitionsDrawer() {
      PaletteDrawer componentsDrawer = new PaletteDrawer("Transitions");

      ToolEntry tool =
            new ConnectionCreationToolEntry("Default Transition", "Create a Default Transition", new CreationFactory() {
               public Object getNewObject() {
                  return null;
               }

               public Object getObjectType() {
                  return DefaultTransitionConnection.class;
               }
            }, AtsPlugin.getInstance().getImageDescriptor("connection_s16.gif"),
                  AtsPlugin.getInstance().getImageDescriptor("connection_s24.gif"));
      componentsDrawer.add(tool);

      tool =
            new ConnectionCreationToolEntry("Transition", "Create a Transition", new CreationFactory() {
               public Object getNewObject() {
                  return null;
               }

               public Object getObjectType() {
                  return TransitionConnection.class;
               }
            }, AtsPlugin.getInstance().getImageDescriptor("connection_s16.gif"),
                  AtsPlugin.getInstance().getImageDescriptor("connection_s24.gif"));
      componentsDrawer.add(tool);

      tool =
            new ConnectionCreationToolEntry("Return Transition", "Create a Return Transition", new CreationFactory() {
               public Object getNewObject() {
                  return null;
               }

               public Object getObjectType() {
                  return ReturnTransitionConnection.class;
               }
            }, AtsPlugin.getInstance().getImageDescriptor("connection_d16.gif"),
                  AtsPlugin.getInstance().getImageDescriptor("connection_d24.gif"));
      componentsDrawer.add(tool);

      return componentsDrawer;
   }

   /**
    * Creates the PaletteRoot and adds all palette elements. Use this factory method to create a new palette for your
    * graphical editor.
    * 
    * @return a new PaletteRoot
    */
   static PaletteRoot createPalette() {
      PaletteRoot palette = new PaletteRoot();
      palette.add(createToolsGroup(palette));
      palette.add(createStatesDrawer());
      palette.add(createTransitionsDrawer());
      return palette;
   }

   /** Create the "Tools" group. */
   private static PaletteContainer createToolsGroup(PaletteRoot palette) {
      PaletteToolbar toolbar = new PaletteToolbar("Tools");

      // Add a selection tool to the group
      ToolEntry tool = new PanningSelectionToolEntry();
      toolbar.add(tool);
      palette.setDefaultEntry(tool);

      // Add a marquee tool to the group
      toolbar.add(new MarqueeToolEntry());

      toolbar.add(new ValidateDiagramToolEntry());

      return toolbar;
   }

   /** Utility class. */
   private AtsWorkflowConfigEditorPaletteFactory() {
      // Utility class
   }

}