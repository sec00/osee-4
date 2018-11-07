/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.eclipse.osee.ats.dsl.services.AtsDslGrammarAccess;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

/**
 * This class contains custom formatting description. see :
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting on how and when to use it Also see
 * {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 *
 * @author Donald G. Dunne
 */
public class AtsDslFormatter extends AbstractDeclarativeFormatter implements IIndentationInformation {

   private final List<String> KEYWORDS = Arrays.asList(new String[] {
      "attributeName",
      "version",
      "staticId",
      "baselineBranchId",
      "workDefinition",
      "teamDefinition",
      "privileged",
      "userDefinition",
      "actionableItem",
      "allowCreateBranch",
      "allowCommitBranch",
      "isAdmin",
      "userId",
      "email",
      "children",
      "released",
      "next",
      "team",
      "member",
      "lead",
      "xWidgetName",
      "defaultValue",
      "height",
      "option",
      "id",
      "description",
      "layoutCopyFrom",
      "attributeWidget",
      "to",
      "ordinal",
      "layout",
      "composite",
      "startState",
      "rule",
      "widget",
      "relatedToState",
      "blockingType",
      "decisionReview",
      "onEvent",
      "peerReview",
      "autoTransitionToDecision",
      "numColumns"});

   private boolean isKeywordEntry(String current) {
      return KEYWORDS.contains(current);
   }

   @Override
   protected void configureFormatting(FormattingConfig c) {
      AtsDslGrammarAccess access = (AtsDslGrammarAccess) getGrammarAccess();

      c.setAutoLinewrap(120);

      Iterable<Keyword> keywords = GrammarUtil.containedKeywords(access.getGrammar());
      Stack<Keyword> openBraceStack = new Stack<>();

      for (Keyword currentKeyword : keywords) {
         String current = currentKeyword.getValue();
         if ("{".equals(current)) {
            openBraceStack.add(currentKeyword);
            c.setLinewrap().after(currentKeyword);
         } else if ("}".equals(current)) {
            c.setLinewrap().before(currentKeyword);
            c.setLinewrap().after(currentKeyword);
            if (!openBraceStack.isEmpty()) {
               c.setIndentation(openBraceStack.pop(), currentKeyword);
            }
         } else if (";".equals(current)) {
            c.setSpace("").before(currentKeyword);
            c.setLinewrap(1).after(currentKeyword);
         } else if ("state".equals(current)) {
            c.setLinewrap(2).before(currentKeyword);
         } else if ("widgetDefinition".equals(current)) {
            c.setLinewrap(2).before(currentKeyword);
         } else if ("teamDefinition".equals(current)) {
            c.setLinewrap(2).before(currentKeyword);
         } else if (isKeywordEntry(current)) {
            c.setLinewrap().before(currentKeyword);
         }
      }
      c.setLinewrap(0, 1, 2).before(access.getSL_COMMENTRule());
      c.setLinewrap(0, 1, 2).before(access.getML_COMMENTRule());
      c.setLinewrap(0, 1, 1).after(access.getML_COMMENTRule());
   }

   @Override
   public String getIndentString() {
      return "   ";
   }
}
