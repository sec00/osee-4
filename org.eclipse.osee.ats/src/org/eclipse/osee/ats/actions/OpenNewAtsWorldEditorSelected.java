/*
 * Created on Sep 4, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import java.util.ArrayList;
import org.eclipse.jface.action.Action;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorSelected extends Action {

   private final IOpenNewAtsWorldEditorSelectedHandler openNewAtsWorldEditorSelectedHandler;

   public OpenNewAtsWorldEditorSelected(IOpenNewAtsWorldEditorSelectedHandler openNewAtsWorldEditorSelectedHandler) {
      this.openNewAtsWorldEditorSelectedHandler = openNewAtsWorldEditorSelectedHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GLOBE_SELECT));
      setToolTipText("Open Selected in ATS World Editor");
   }

   public interface IOpenNewAtsWorldEditorSelectedHandler {
      public CustomizeData getCustomizeDataCopy() throws OseeCoreException;

      public ArrayList<Artifact> getSelectedArtifacts() throws OseeCoreException;

   }

   @Override
   public void run() {
      try {
         if (openNewAtsWorldEditorSelectedHandler.getSelectedArtifacts().size() == 0) {
            AWorkbench.popup("ERROR", "Select items to open");
            return;
         }
         WorldEditor.open(new WorldEditorSimpleProvider("ATS World",
               openNewAtsWorldEditorSelectedHandler.getSelectedArtifacts(),
               openNewAtsWorldEditorSelectedHandler.getCustomizeDataCopy(), (TableLoadOption[]) null));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
