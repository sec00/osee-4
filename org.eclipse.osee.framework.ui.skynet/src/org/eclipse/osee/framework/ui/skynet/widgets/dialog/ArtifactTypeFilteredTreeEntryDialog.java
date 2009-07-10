/*
 * Created on Jul 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeFilteredTreeEntryDialog extends ArtifactTypeFilteredTreeDialog {

   private String entryValue = null;
   private final String entryName;
   private XText xText = null;

   /**
    * @param title
    * @param message
    * @param artifactTypes
    */
   public ArtifactTypeFilteredTreeEntryDialog(String title, String message, String entryName, Collection<ArtifactType> artifactTypes) {
      super(title, message, artifactTypes);
      this.entryName = entryName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEEFilteredTreeDialog#createPreCustomArea(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected void createPreCustomArea(Composite parent) {
      super.createPreCustomArea(parent);
      xText = new XText(entryName);
      if (entryValue != null) {
         xText.setText(entryValue);
      }
      xText.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            entryValue = xText.get();
            updateStatusLabel();
         }
      });
      xText.createWidgets(parent, 2);
   }

   /**
    * @return the entryValue
    */
   public String getEntryValue() {
      return entryValue;
   }

   /**
    * @param entryValue the entryValue to set
    */
   public void setEntryValue(String entryValue) {
      this.entryValue = entryValue;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTypeFilteredTreeDialog#isComplete()
    */
   @Override
   protected Result isComplete() {
      if (entryValue == null || entryValue.equals("")) {
         return new Result("Must enter Artifact name.");
      }
      return super.isComplete();
   }

}
