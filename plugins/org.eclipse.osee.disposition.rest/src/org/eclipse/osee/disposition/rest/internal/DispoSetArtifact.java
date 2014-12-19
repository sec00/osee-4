/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.rest.DispoConstants;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Angel Avila
 */
public class DispoSetArtifact extends BaseIdentity<String> implements DispoSet, Comparable<Named> {

   private final ArtifactReadable artifact;

   public DispoSetArtifact(ArtifactReadable artifact) {
      super(artifact.getGuid());
      this.artifact = artifact;
   }

   @Override
   public String getName() {
      return artifact.getName();
   }

   @Override
   public String getImportPath() {
      return artifact.getSoleAttributeAsString(DispoConstants.ImportPath);
   }

   @Override
   public List<Note> getNotesList() {
      List<Note> toReturn = new ArrayList<Note>();
      String notesJson = artifact.getSoleAttributeAsString(DispoConstants.DispoNotesJson, "[]");
      try {
         JSONArray jArray = new JSONArray(notesJson);
         for (int i = 0; i < jArray.length(); i++) {
            toReturn.add(DispoUtil.jsonObjToNote(jArray.getJSONObject(i)));
         }
         return toReturn;
      } catch (JSONException ex) {
         throw new OseeCoreException("Could not parse Notes Json", ex);
      }
   }

   @Override
   public int compareTo(Named other) {
      if (other != null && other.getName() != null && getName() != null) {
         return getName().compareTo(other.getName());
      }
      return -1;
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getImportState() {
      return artifact.getSoleAttributeAsString(DispoConstants.ImportState, "NOT_SET");
   }

   @Override
   public String getDispoType() {
      return artifact.getSoleAttributeAsString(DispoConstants.DispoType, "");
   }
}
