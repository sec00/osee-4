/*
 * Created on Sep 5, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.report.api;

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class RendererClassRegistry {

   // TODO: Look at better option instead of CopyOnWriteArray
   private final List<Renderer> list = new CopyOnWriteArrayList<Renderer>();

   public void start() {
      //
      
      System.out.println("test");
   }

   public void stop() {
      //
   }

   public void addProvider(Renderer provider) {
      list.add(provider);
   }

   public void removeProvider(Renderer provider) {
      list.remove(provider);
   }

   // add get best renderer method
   public Renderer getBestRenderer(PresentationType presentationType, ArtifactReadable artifact) {
      // TODO: how to do on server - UserManager.getBooleanSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_ART_EDIT)
      if (presentationType == DEFAULT_OPEN) {
         presentationType = GENERAL_REQUESTED;
      }

      Renderer bestRendererPrototype = null;
      int bestRating = Renderer.NO_MATCH;

      for (Renderer renderer : list) {

         int rating = renderer.getApplicabilityRating(presentationType, artifact);
         if (rating > bestRating) {
            bestRendererPrototype = renderer;
            bestRating = rating;
         }
      }
      if (bestRendererPrototype == null) {
         throw new OseeStateException("No renderer configured for %s of %s", presentationType, artifact);
      }

      return bestRendererPrototype;
   }
}
