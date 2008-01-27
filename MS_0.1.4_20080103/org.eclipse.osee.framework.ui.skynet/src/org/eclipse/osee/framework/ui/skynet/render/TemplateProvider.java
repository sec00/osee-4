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
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.JavaObjectAttribute;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class TemplateProvider {
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

   private final DoubleKeyHashMap<Branch, PresentationType, Artifact> documentMap;
   private HashMap<String, String> defaultTemplateMap;

   protected TemplateProvider() {
      this.defaultTemplateMap = null;
      this.documentMap = new DoubleKeyHashMap<Branch, PresentationType, Artifact>();
   }

   public void setDefaultTemplates(String rendererId, Artifact document, PresentationType presentationType, Branch branch) throws Exception {
      if (document == null) {
         document = getDocumentArtifact(rendererId, presentationType, branch);
      } else {
         document.setDescriptiveName("org.eclipse.osee.framework.ui.skynet.word " + presentationType);
      }

      JavaObjectAttribute javaAttribute =
            (JavaObjectAttribute) document.getAttributeManager("Template Map").getSoleAttribute();

      HashMap<String, String> defaultMap = getDefaultTemplateMap();
      javaAttribute.setObject(defaultMap);
      document.persistAttributes();
   }

   @SuppressWarnings("unchecked")
   public void addTemplate(String rendererId, Branch branch, PresentationType presentationType, TemplateLocation locationData) throws SQLException, IOException, ClassNotFoundException {
      Artifact document = getDocumentArtifact(rendererId, presentationType, branch);
      JavaObjectAttribute javaAttribute =
            (JavaObjectAttribute) document.getAttributeManager("Template Map").getSoleAttribute();
      HashMap<String, String> templateMap = (HashMap<String, String>) javaAttribute.getObject();
      if (templateMap == null) {
         templateMap = new HashMap<String, String>();
      }

      addTemplateToMap(templateMap, locationData);

      javaAttribute.setObject(templateMap);
      document.persistAttributes();
   }

   @SuppressWarnings("unchecked")
   public String getTemplate(String rendererId, Branch branch, Artifact artifact, PresentationType presentationType, String option) throws Exception {
      String template = null;
      try {
         Artifact document = getDocumentArtifact(rendererId, presentationType, branch);
         JavaObjectAttribute javaAttribute =
               (JavaObjectAttribute) document.getAttributeManager("Template Map").getSoleAttribute();
         HashMap<String, String> templateMap = (HashMap<String, String>) javaAttribute.getObject();

         if (option != null) {
            template = templateMap.get(option);
         }
         if (template == null && artifact != null) {
            template = templateMap.get(artifact.getArtifactTypeName());
         }

         if (template == null) {
            template = templateMap.get("default");
            if (template == null) {
               throw new IllegalArgumentException("No default template found for the artifact: " + document);
            }
         }
      } catch (Exception ex) {
         String message = "Error obtaining template from database. Using default templates.";
         template = getDefaultTemplateMap().get("default");
         OSEELog.logException(TemplateProvider.class, new IllegalStateException(message, ex), true);
      }
      return template;
   }

   private Artifact getDocumentArtifact(String rendererId, PresentationType presentationType, Branch branch) throws SQLException {
      Artifact document = documentMap.get(branch, presentationType);
      if (document == null) {
         try {
            document =
                  artifactManager.getArtifactFromTypeName("Document", rendererId + " " + presentationType.name(),
                        branch);
         } catch (IllegalStateException ex) {
            if (branch == branchManager.getCommonBranch()) {
               document = null;
            } else if (branch.getParentBranch() == null) {
               document = getDocumentArtifact(rendererId, presentationType, branchManager.getCommonBranch());
            } else {
               document = getDocumentArtifact(rendererId, presentationType, branch.getParentBranch());
            }
         }
         documentMap.put(branch, presentationType, document);
      }
      return document;
   }

   private void addTemplateToMap(HashMap<String, String> templateMap, TemplateLocation locationData) throws IOException {
      Bundle bundle = Platform.getBundle(locationData.getBundleName());
      InputStream inputStream = bundle.getEntry(locationData.getTemplatePath()).openStream();
      String template = Lib.inputStreamToString(inputStream);
      templateMap.put(locationData.getTemplateName(), template);
   }

   private HashMap<String, String> getDefaultTemplateMap() throws Exception {
      if (defaultTemplateMap == null) {
         defaultTemplateMap = new HashMap<String, String>();
         try {
            List<TemplateLocation> defaultTemplates = getExtensionDefinedTemplates();
            for (TemplateLocation templateLocationData : defaultTemplates) {
               addTemplateToMap(defaultTemplateMap, templateLocationData);
            }
         } catch (Exception ex) {
            throw new Exception("Unable to load extension defined templates.", ex);
         }
      }
      return defaultTemplateMap;
   }

   private List<TemplateLocation> getExtensionDefinedTemplates() {
      List<TemplateLocation> extensionTemplates = new ArrayList<TemplateLocation>();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements("org.eclipse.osee.framework.ui.skynet.ArtifactRendererTemplate",
                  "Template");

      for (IConfigurationElement element : elements) {
         String bundleName = element.getContributor().getName();
         String templateName = element.getAttribute("templateName");
         String templatePath = element.getAttribute("templateFile");
         extensionTemplates.add(new TemplateLocation(bundleName, templateName, templatePath));
      }
      return extensionTemplates;
   }

   protected TemplateLocation createLocation(String bundleName, String templateName, String templatePath) {
      return new TemplateLocation(bundleName, templateName, templatePath);
   }

   protected final class TemplateLocation {
      String bundleName;
      String templateName;
      String templatePath;

      private TemplateLocation(String bundleName, String templateName, String templatePath) {
         this.bundleName = bundleName;
         this.templateName = templateName;
         this.templatePath = templatePath;
      }

      public String getBundleName() {
         return bundleName;
      }

      public String getTemplateName() {
         return templateName;
      }

      public String getTemplatePath() {
         return templatePath;
      }
   }

}
