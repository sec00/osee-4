/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.app.internal;

import static org.eclipse.osee.framework.core.enums.BranchState.CREATED;
import static org.eclipse.osee.framework.core.enums.BranchState.MODIFIED;
import static org.eclipse.osee.framework.core.enums.BranchType.BASELINE;
import static org.eclipse.osee.framework.core.enums.BranchType.WORKING;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.ws.rs.WebApplicationException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.template.engine.AppendableRule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ryan D. Brooks
 */
public final class SinglePageAppRule extends AppendableRule<CharSequence> {
   private final OrcsApi orcsApi;
   private final JSONObject json;

   public SinglePageAppRule(OrcsApi orcsApi, JSONObject json) {
      super("SinglePageApp");
      this.orcsApi = orcsApi;
      this.json = json;
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      try {
         createApp(appendable);
      } catch (JSONException ex) {
         appendable.append("<pre>");
         appendable.append(ex.toString());
         appendable.append("</pre>");
      }
   }

   private void createApp(Appendable appendable) throws JSONException, IOException {
      appendable.append("<form class=\"form-horizontal\" role=\"form\" action=\"#\" onsubmit=\"return performActions(this);\">\n");

      JSONArray parameters = json.getJSONArray("parameters");
      int paramCount = parameters.length();
      for (int i = 0; i < paramCount; i++) {
         JSONObject parameter = parameters.getJSONObject(i);
         addParameter(appendable, parameter);
      }

      JSONArray actions = json.getJSONArray("actions");
      int actionCount = actions.length();
      for (int i = 0; i < actionCount; i++) {
         JSONObject action = actions.getJSONObject(i);
         addActionButton(appendable, action);
      }

   }

   private void addParameter(Appendable appendable, JSONObject parameter) throws JSONException, IOException {
      String name = parameter.getString("name");
      String label = parameter.optString("label", name);

      appendable.append("        <div class=\"form-group\">\n");
      appendable.append("          <label for=\"");
      appendable.append(name);
      appendable.append("\" class=\"col-sm-2 control-label\">");
      appendable.append(label);
      appendable.append("</label>\n");
      appendable.append("          <div class=\"col-sm-10\">\n");
      addContorl(appendable, parameter, name, label);
      appendable.append("          </div>\n");
      appendable.append("        </div>\n");
   }

   private void addActionButton(Appendable appendable, JSONObject action) throws JSONException, IOException {
      String name = action.getString("name");
      String url = action.optString("url", name);

      appendable.append("        <div class=\"form-group\">\n");
      appendable.append("          <div class=\"col-sm-offset-2 col-sm-1\">\n");
      appendable.append("            <a class=\"btn btn-primary\" role=\"button\" href=\"");
      appendable.append(url);
      appendable.append("\">");
      appendable.append(name);
      appendable.append("</a>\n");
      appendable.append("          </div>\n");
      appendable.append("        </div>\n");
   }

   private void addContorl(Appendable appendable, JSONObject parameter, String name, String label) throws JSONException, IOException {
      String type = parameter.getString("type");
      boolean multiple = parameter.optBoolean("multiple");
      String value = parameter.optString("value");
      String description = parameter.optString("description");
      String options = parameter.optString("options");

      if (type.equals("text")) {
         if (multiple) {
            appendable.append("          <textarea class=\"form-control\" rows=\"3");
            addToolTip(appendable, description);
            appendable.append(value);
            appendable.append("</textarea>");
         } else {
            startInputTag(appendable, name);
            appendable.append("text\" class=\"form-control\" placeholder=\"\" required value=\"");
            appendable.append(value);
            addToolTip(appendable, description);
         }
      } else if (type.equals("list")) {
         addListControl(appendable, name, description, options, label);
      } else if (type.equals("boolean")) {
         appendable.append("          <div class=\"checkbox\">\n");
         appendable.append("  <input type=\"checkbox\" id=\"includeLinks\">");
         appendable.append("          </div>");
      } else if (type.equals("branch")) {
         if (options.equals("")) {
            options =
               "start from branch where type = system-root follow branch to children depth max collect branch-id as value, name as text";
         }
         addListControl(appendable, name, description, options, label);
      } else {
         startInputTag(appendable, name);
         appendable.append("text\" class=\"form-control\" readonly value=\"");
         appendable.append("the type [");
         appendable.append(type);
         appendable.append("] is not supported");
         addToolTip(appendable, description);
      }
   }

   private void addListControl(Appendable appendable, String name, String description, String options, String label) throws IOException, JSONException {
      appendable.append("          <input id=\"");
      appendable.append(name);
      appendable.append("Filter\" class=\"form-control\" type=\"search\" placeholder=\"");
      appendable.append(label);
      appendable.append(" Filter...\" />\n");
      appendable.append("          <select class=\"form-control\" id=\"");
      appendable.append(name);
      addToolTip(appendable, description);
      addOptions(appendable, options);
      appendable.append("          </select>\n");
   }

   private void startInputTag(Appendable appendable, String name) throws IOException {
      appendable.append("          <input id=\"");
      appendable.append(name);
      appendable.append("\" type=\"");
   }

   private void addToolTip(Appendable appendable, String description) throws IOException {
      appendable.append("\" title=\"");
      appendable.append(description);
      appendable.append("\">\n");
   }

   private void addOptions(Appendable appendable, String options) throws IOException, JSONException {
      if (options.startsWith("start from branch where")) {
         orcsApi.getBranchOps(null);
         BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();

         query.andIsOfType(BASELINE, WORKING);
         query.andStateIs(CREATED, MODIFIED);
         Iterable<BranchReadable> branches = query.getResults();
         for (BranchReadable branch : branches) {
            addOption(appendable, branch.getUuid().toString(), branch.getName());
         }

         executeOseeScript(appendable,
            "start from branch where type = [baseline,working] collect branches {id as \"value\", name as \"text\"};");
      } else {
         // execute options query
         for (int i = 0; i < 15; i++) {
            addOption(appendable, String.valueOf(i), String.valueOf(i));
         }
      }
   }

   private void executeOseeScript(Appendable appendable, String oseeScript) throws JSONException, IOException {
      ScriptEngine engine = orcsApi.getScriptEngine();
      try {
         Writer writer = new StringWriter(1000);
         ScriptContext context = new SimpleScriptContext();
         context.setWriter(writer);
         context.setErrorWriter(writer);
         context.setAttribute("output.debug", false, ScriptContext.ENGINE_SCOPE);

         engine.eval(oseeScript, context);

         JSONObject json = new JSONObject(writer.toString());
         JSONArray results = json.getJSONArray("results");

         int paramCount = results.length();
         for (int i = 0; i < paramCount; i++) {
            JSONObject branch = results.getJSONObject(i);
            addOption(appendable, branch.getString("value"), "text");
         }

         //         "value"
         //         "text"
      } catch (ScriptException ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void addOption(Appendable appendable, String value, String text) throws IOException {
      appendable.append("            <option value=\"");
      appendable.append(value);
      appendable.append("\">");
      appendable.append(text);
      appendable.append("</option>\n");
   }
}