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

package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSelectionDialog.Selection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ArtifactPromptChange {

   private final static String VALID_FLOAT_REG_EX = "^[0-9\\.]+$";
   private final static String VALID_INTEGER_REG_EX = "^[0-9]+$";
   private final static String VALID_PERCENT_REG_EX =
         "^(0*100{1,1}\\.?((?<=\\.)0*)?%?$)|(^0*\\d{0,2}\\.?((?<=\\.)\\d*)?%?)$";

   public static boolean promptChangeAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) {
      try {
         Class<? extends Attribute<?>> attributeBaseType =
               AttributeTypeManager.getType(attributeName).getBaseAttributeClass();
         if (attributeBaseType.equals(DateAttribute.class)) {
            return ArtifactPromptChange.promptChangeDate(attributeName, displayName, artifacts, persist);
         } else if (attributeBaseType.equals(FloatingPointAttribute.class)) {
            return ArtifactPromptChange.promptChangeFloatAttribute(attributeName, displayName, artifacts, persist);
         } else if (attributeBaseType.equals(IntegerAttribute.class)) {
            return ArtifactPromptChange.promptChangeIntegerAttribute(attributeName, displayName, artifacts, persist);
         } else if (attributeBaseType.equals(BooleanAttribute.class)) {
            return ArtifactPromptChange.promptChangeBoolean(attributeName, displayName, artifacts, null, persist);
         } else if (attributeBaseType.equals(EnumeratedAttribute.class)) {
            return ArtifactPromptChange.promptChangeEnumeratedAttribute(attributeName, displayName, artifacts, persist);
         } else if (attributeBaseType.equals(StringAttribute.class)) {
            return ArtifactPromptChange.promptChangeStringAttribute(attributeName, displayName, artifacts, persist,
                  true);
         } else
            AWorkbench.popup("ERROR", "Unhandled attribute type.  Can't edit through this view");
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeIntegerAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws Exception {
      return promptChangeStringAttribute(attributeName, displayName, VALID_INTEGER_REG_EX, artifacts, persist, false);
   }

   public static boolean promptChangeIntegerAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws Exception {
      return promptChangeStringAttribute(attributeName, displayName, VALID_INTEGER_REG_EX, Arrays.asList(artifact),
            persist, false);
   }

   public static boolean promptChangePercentAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws Exception {
      return promptChangeStringAttribute(attributeName, displayName, VALID_PERCENT_REG_EX, Arrays.asList(artifact),
            persist, false);
   }

   public static boolean promptChangePercentAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws Exception {
      return promptChangeStringAttribute(attributeName, displayName, VALID_PERCENT_REG_EX, artifacts, persist, false);
   }

   public static boolean promptChangeFloatAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws Exception {
      return promptChangeFloatAttribute(attributeName, displayName, Arrays.asList(artifact), persist);
   }

   public static boolean promptChangeFloatAttribute(String attributeName, String displayName, final Collection<? extends Artifact> smas, boolean persist) throws Exception {
      return promptChangeStringAttribute(attributeName, displayName, VALID_FLOAT_REG_EX, smas, persist, false);
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist, boolean multiLine) throws Exception {
      return promptChangeStringAttribute(attributeName, displayName, null, Arrays.asList(artifact), persist, multiLine);
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, final Collection<? extends Artifact> smas, boolean persist, boolean multiLine) throws Exception {
      return promptChangeStringAttribute(attributeName, displayName, null, smas, persist, multiLine);
   }

   public static boolean promptChangeDate(String attributeName, String displayName, Artifact artifact, boolean persist) throws Exception {
      return promptChangeDate(attributeName, displayName, Arrays.asList(artifact), persist);
   }

   public static boolean promptChangeDate(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws Exception {
      // prompt that current release is (get from attribute); want to change
      DateSelectionDialog diag =
            new DateSelectionDialog("Select " + displayName, "Select " + displayName,
                  artifacts.size() == 1 ? artifacts.iterator().next().getSoleAttributeValue(attributeName, null,
                        Date.class) : null);
      if (diag.open() == 0) {
         if (artifacts.size() > 0) {
            SkynetTransaction transaction =
                  !persist ? null : new SkynetTransaction(artifacts.iterator().next().getBranch());
            for (Artifact artifact : artifacts) {
               if (diag.isNoneSelected())
                  artifact.deleteSoleAttribute(attributeName);
               else
                  artifact.setSoleAttributeValue(attributeName, diag.getSelectedDate());
               if (persist) artifact.persistAttributes(transaction);
            }
            if (persist) transaction.execute();
         }
      }
      return true;
   }

   public static boolean promptChangeEnumeratedAttribute(String attributeName, String displayName, Artifact artifact, boolean persist) throws Exception {
      return promptChangeEnumeratedAttribute(attributeName, displayName, Arrays.asList(artifact), persist);
   }

   public static boolean promptChangeEnumeratedAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws Exception {
      String type = artifacts.iterator().next().getArtifactTypeName();
      for (Artifact art : artifacts) {
         if (!type.equals(art.getArtifactTypeName())) {
            AWorkbench.popup("ERROR", "Artifact types must all match to change enumerated type.");
            return false;
         }
      }
      EnumSelectionDialog diag = new EnumSelectionDialog(attributeName, artifacts);
      if (diag.open() == 0) {
         Set<String> selected = new HashSet<String>();
         for (Object obj : diag.getResult())
            selected.add((String) obj);
         if (artifacts.size() > 0) {
            SkynetTransaction transaction =
                  !persist ? null : new SkynetTransaction(artifacts.iterator().next().getBranch());
            for (Artifact artifact : artifacts) {
               List<String> current = artifact.getAttributesToStringList(attributeName);
               if (diag.getSelected() == Selection.AddSelection) {
                  current.addAll(selected);
                  artifact.setAttributeValues(attributeName, current);
               } else if (diag.getSelected() == Selection.DeleteSelected) {
                  current.removeAll(selected);
                  artifact.setAttributeValues(attributeName, current);
               } else if (diag.getSelected() == Selection.ReplaceAll) {
                  artifact.setAttributeValues(attributeName, selected);
               } else {
                  AWorkbench.popup("ERROR", "Unhandled selection type => " + diag.getSelected().name());
                  return false;
               }
               if (persist) artifact.persistAttributes(transaction);
            }
            if (persist) transaction.execute();
         }
      }
      return true;
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, String validationRegEx, final Collection<? extends Artifact> smas, boolean persist, boolean multiLine) throws OseeCoreException {
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Enter " + displayName, null,
                  "Enter " + displayName, MessageDialog.QUESTION, new String[] {"OK", "Clear", "Cancel"}, 0);
      ed.setFillVertically(multiLine);
      if (smas.size() == 1) {
         Object obj = smas.iterator().next().getSoleAttributeValueAsString(attributeName, null);
         if (obj != null) {
            ed.setEntry(String.valueOf(obj));
         }
      }
      if (validationRegEx != null) ed.setValidationRegularExpression(validationRegEx);
      int result = ed.open();
      if (result == 0 || result == 1) {
         SkynetTransaction transaction = !persist ? null : new SkynetTransaction(smas.iterator().next().getBranch());
         for (Artifact sma : smas) {
            String value = ed.getEntry();
            if (result == 0) {
               sma.setSoleAttributeFromString(attributeName, value);
            } else {
               if (attributeName.equals("Name")) {
                  throw new OseeArgumentException("Can not delete Name attribute");
               }
               sma.deleteSoleAttribute(attributeName);
            }
            if (persist) sma.persistAttributes(transaction);
         }
         if (persist) transaction.execute();
         return true;
      }
      return false;
   }

   public static boolean promptChangeBoolean(String attributeName, String displayName, final Artifact artifact, String toggleMessage, boolean persist) throws OseeCoreException {
      return promptChangeBoolean(attributeName, displayName, Arrays.asList(artifact), toggleMessage, persist);
   }

   public static boolean promptChangeBoolean(String attributeName, String displayName, final Collection<? extends Artifact> smas, String toggleMessage, boolean persist) throws OseeCoreException {
      boolean set = false;
      if (smas.size() == 1) set = smas.iterator().next().getSoleAttributeValue(attributeName, false);
      MessageDialogWithToggle md =
            new MessageDialogWithToggle(Display.getCurrent().getActiveShell(), displayName, null, displayName,
                  MessageDialog.QUESTION, new String[] {"Ok", "Cancel"}, MessageDialog.OK,
                  toggleMessage != null ? toggleMessage : displayName, set);

      int result = md.open();
      if (result == 256) {
         if (smas.size() > 0) {
            SkynetTransaction transaction = !persist ? null : new SkynetTransaction(smas.iterator().next().getBranch());
            for (Artifact sma : smas) {
               sma.setSoleAttributeValue(attributeName, md.getToggleState());
               if (persist) sma.persistAttributes();
            }
            if (persist) transaction.execute();
         }
         return true;
      }
      return false;
   }

}
