/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.api.components;

import org.eclipse.osee.display.api.data.WebArtifact;

public interface AttributeComponent {

   void clearAll();

   void setArtifact(WebArtifact artifact);
}
