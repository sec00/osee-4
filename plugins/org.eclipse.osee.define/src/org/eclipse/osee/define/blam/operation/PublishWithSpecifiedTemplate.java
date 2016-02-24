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
package org.eclipse.osee.define.blam.operation;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.MediaType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XDslEditorWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class PublishWithSpecifiedTemplate extends AbstractBlam {
   private List<Artifact> templates;
   private IOseeBranch branch;
   private boolean isSRSPublishing;
   private XBranchSelectWidget branchWidget;
   private XCombo slaveWidget;
   private XDslEditorWidget orcsQueryWidget;
   private XListDropViewer artifactsWidget;
   private final String USE_ARTIFACT_NAMES = "Use Artifact Names";
   private final String USE_PARAGRAPH_NUMBERS = "Use Paragraph Numbers";
   private final String UPDATE_PARAGRAPH_NUMBERS = "Update Paragraph Numbers (If authorized)";
   private final String EXCLUDE_ARTIFACT_TYPES = "Exclude Artifact Types";
   private final String MASTER_TEMPLATE = "Master Template";
   private final String SLAVE_TEMPLATE = "Slave Template";
   private final String IS_ARTIFACTS = "IS Artifacts";
   private final String PUBLISH_AS_DIFF = "Publish As Diff";
   private final String WAS_BRANCH = "WAS Branch";
   private final String INCLUDE_ARTIFACT_UUIDS = "Include Artifact UUIDs";
   private final String ORCS_QUERY = "Orcs Query";

   @Override
   public String getName() {
      return "Publish With Specified Template";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      populateTemplateList();

      boolean useArtifactNameInLinks = variableMap.getBoolean(USE_ARTIFACT_NAMES);
      boolean useParagraphNumbersInLinks = variableMap.getBoolean(USE_PARAGRAPH_NUMBERS);
      variableMap.getBoolean(INCLUDE_ARTIFACT_UUIDS);

      if (!useParagraphNumbersInLinks && !useArtifactNameInLinks) {
         throw new OseeArgumentException("Please select at least one Document Link Format");
      }
      LinkType linkType;
      if (useArtifactNameInLinks && useParagraphNumbersInLinks) {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
      } else if (useParagraphNumbersInLinks) {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER;
      } else {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      }

      Artifact master = getTemplate(variableMap.getString(MASTER_TEMPLATE));
      if (master == null) {
         throw new OseeArgumentException("Must select a Master Template");
      }
      Artifact slave = getTemplate(variableMap.getString(SLAVE_TEMPLATE));

      List<Artifact> artifacts = null;
      try {
         if (!isSRSPublishing) {
            if (orcsQueryWidget.getText().isEmpty()) {
               artifacts = variableMap.getArtifacts(IS_ARTIFACTS);
            } else {
               artifacts = getArtifactsFromOrcsQuery();
            }
         }
      } catch (NullPointerException e) {
         throw new OseeArgumentException(
            "Must provide an IS artifact or an Orcs Query.  Please add an IS artifact or an Orcs Query and rerun.");
      }
      if (artifacts != null && !artifacts.isEmpty()) {
         branch = artifacts.get(0).getBranch();
      } else if (!isSRSPublishing) {
         throw new OseeArgumentException("Must provide an artifact");
      }

      if (branch == null) {
         throw new OseeArgumentException("Cannot determine IS branch.");
      }

      WordTemplateRenderer renderer = new WordTemplateRenderer();
      SkynetTransaction transaction =
         TransactionManager.createTransaction(branch, "BLAM: Publish with specified template");

      Object[] options = new Object[] {
         "Branch",
         branch,
         "compareBranch",
         variableMap.getBranch(WAS_BRANCH),
         "Publish As Diff",
         variableMap.getValue(PUBLISH_AS_DIFF),
         "INCLUDE UUIDS",
         variableMap.getValue(INCLUDE_ARTIFACT_UUIDS),
         "linkType",
         linkType,
         WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION,
         variableMap.getBoolean(UPDATE_PARAGRAPH_NUMBERS),
         "EXCLUDE ARTIFACT TYPES",
         variableMap.getArtifactTypes(EXCLUDE_ARTIFACT_TYPES),
         ITemplateRenderer.TRANSACTION_OPTION,
         transaction,
         IRenderer.SKIP_ERRORS,
         true,
         "Exclude Folders",
         true,
         "Recurse On Load",
         true,
         "Maintain Order",
         true,
         "Progress Monitor",
         monitor,
         ITemplateRenderer.USE_TEMPLATE_ONCE,
         true,
         WordTemplateRenderer.FIRST_TIME,
         true};

      renderer.publish(master, slave, artifacts, options);

      transaction.execute();
   }

   private List<Artifact> getArtifactsFromOrcsQuery() {
      Writer writer = new StringWriter();
      OseeClient oseeClient = ServiceUtil.getOseeClient();

      String orcsQuery = orcsQueryWidget.getText();
      oseeClient.executeScript(orcsQuery, null, false, MediaType.APPLICATION_JSON_TYPE, writer);
      String result = writer.toString();
      parseQueryForBranchName(orcsQuery);

      //Parse JSON returned from query
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
      try {
         JSONObject jsonObject = new JSONObject(result);
         JSONArray results = jsonObject.getJSONArray("results");
         if (results.length() >= 1 && branch != null) {
            JSONArray artifactIds = results.getJSONObject(0).getJSONArray("artifacts");
            JSONObject id = null;
            for (int i = 0; i < artifactIds.length(); i++) {
               id = artifactIds.getJSONObject(i);
               Long artifactId = id.getLong("id");
               Artifact artifact = ArtifactQuery.getArtifactFromId(artifactId, branch, EXCLUDE_DELETED);
               artifacts.add(artifact);
            }
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return artifacts;
   }

   private void parseQueryForBranchName(String orcsQuery) {
      Pattern pattern = Pattern.compile("start from branch \"(.*?)\"");
      Matcher matcher = pattern.matcher(orcsQuery);
      if (matcher.find()) {
         String branchName = matcher.group(1);
         branch = BranchManager.getBranch(branchName);
      }
   }

   @Override
   public String getDescriptionUsage() {
      StringBuilder sb = new StringBuilder();
      sb.append("<form>Use a template to publish a document or diff the document against a different version.<br/>");
      sb.append("Select Parameters<br/>");
      sb.append("<li>Select Update Paragraph Numbers if authorized to update them</li>");
      sb.append("<li>Choose whether or not you want the UUIDs published</li>");
      sb.append("<li>Select the Document Link format(s)</li>");
      sb.append("<li>Choose artifact type(s) to exclude</li>");
      sb.append("<li>Select Master or Master/Slave (for SRS) template.  Only use non-recursive templates</li>");
      sb.append(
         "<li>Drag &amp; Drop the IS Artifacts into the box OR write an Orcs Query that returns a list of Artifact Ids</li>");
      sb.append("NOTE: Providing IS Artifacts or an Orcs Query is not necessary for SRS Master/Slave Publishing");
      sb.append("<li>Decide to Publish as Diff and select WAS branch as desired</li>");
      sb.append("<br/>Click the play button at the top right or in the Execute section.</form>");
      return sb.toString();
   }

   @Override
   public String getXWidgetsXml() {
      populateTemplateList();
      StringBuilder builder = new StringBuilder();
      builder.append(String.format(
         "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         UPDATE_PARAGRAPH_NUMBERS));
      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         INCLUDE_ARTIFACT_UUIDS));

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Document Link Format:\"/>");
      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" defaultValue=\"true\"/>",
         USE_ARTIFACT_NAMES));
      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         USE_PARAGRAPH_NUMBERS));
      builder.append(String.format(
         "<XWidget xwidgetType=\"XArtifactTypeMultiChoiceSelect\" displayName=\"Exclude Artifact Types\" />",
         EXCLUDE_ARTIFACT_TYPES));

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \" /><XWidget xwidgetType=\"XCombo(");
      for (Artifact art : templates) {
         builder.append(art.getSafeName());
         builder.append(",");
      }
      builder.append(String.format(")\" displayName=\"%s\" horizontalLabel=\"true\"/>", MASTER_TEMPLATE));
      builder.append("<XWidget xwidgetType=\"XCombo(");
      for (Artifact art : templates) {
         builder.append(art.getSafeName());
         builder.append(",");
      }

      builder.append(String.format(
         ")\" displayName=\"%s\" horizontalLabel=\"true\"/><XWidget xwidgetType=\"XLabel\" displayName=\" \" />",
         SLAVE_TEMPLATE));
      builder.append(String.format("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"%s\" />", IS_ARTIFACTS));
      builder.append(
         String.format("<XWidget xwidgetType=\"XDslEditorWidget\" displayName=\"%s\"  defaultValue=\"", ORCS_QUERY));
      builder.append("orcs");
      builder.append("\" fill=\"vertically\"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Generate Differences:\"/>");
      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         PUBLISH_AS_DIFF));
      builder.append(String.format("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"%s\"/>", WAS_BRANCH));
      builder.append(
         "<XWidget xwidgetType=\"XLabel\" displayName=\"Note: If a WAS branch is selected, diffs will be between selected IS artifacts and current version on WAS branch\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XLabel\" displayName=\"If a WAS branch is NOT selected, diffs will be between selected IS artifacts and baseline version on IS branch\"/>");
      builder.append("</xWidgets>");

      return builder.toString();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(WAS_BRANCH)) {
         branchWidget = (XBranchSelectWidget) xWidget;
         branchWidget.setEditable(false);
      } else if (xWidget.getLabel().equals(PUBLISH_AS_DIFF)) {
         final XCheckBox checkBox = (XCheckBox) xWidget;
         checkBox.addSelectionListener(new SelectionAdapter() {

            // Link the editable setting of the branch widget to the 'Publish As Diff' checkbox
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               branchWidget.setEditable(checkBox.isChecked());

               if (!checkBox.isChecked()) {
                  // reset branchwidget selection when checkbox is unchecked
                  branchWidget.setSelection(null);
               }
            }

         });
      } else if (xWidget.getLabel().equals(MASTER_TEMPLATE)) {
         final XCombo masterCombo = (XCombo) xWidget;
         masterCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
               // only enable slave template selection if Master is for SRS.
               if (masterCombo.get().contains("srsMaster")) {
                  slaveWidget.setEnabled(true);
                  orcsQueryWidget.setEditable(false);
                  orcsQueryWidget.set("");
                  artifactsWidget.setEditable(false);
                  isSRSPublishing = true;
               } else {
                  slaveWidget.setEnabled(false);
                  slaveWidget.set("");
                  orcsQueryWidget.setEditable(true);
                  artifactsWidget.setEditable(true);
                  isSRSPublishing = false;
               }
            }
         });
      } else if (xWidget.getLabel().equals(SLAVE_TEMPLATE)) {
         slaveWidget = (XCombo) xWidget;
         slaveWidget.setEnabled(false);
      } else if (xWidget.getLabel().equals(IS_ARTIFACTS)) {
         artifactsWidget = (XListDropViewer) xWidget;
         artifactsWidget.setEditable(true);
      } else if (xWidget.getLabel().equals(ORCS_QUERY)) {
         orcsQueryWidget = (XDslEditorWidget) xWidget;
         orcsQueryWidget.setEditable(true);
      }
   }

   private void populateTemplateList() {
      templates = TemplateManager.getAllTemplates();
      Collections.sort(templates);
   }

   private Artifact getTemplate(String templateName) {
      for (Artifact artifact : templates) {
         if (artifact.getSafeName().equals(templateName)) {
            return artifact;
         }
      }
      return null;
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish");
   }
}
