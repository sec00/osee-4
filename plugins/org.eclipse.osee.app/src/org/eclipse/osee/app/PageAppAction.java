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
package org.eclipse.osee.app;

/**
 * @author Ryan D. Brooks
 */
public final class PageAppAction {
   private String label;
   private final PageAppActionType type;
   private final String name;
   private final boolean visible;
   private String value;
   private String description;
   private PageAppAction dependency;

   //   action                  : 'action' STRING? action_method ;
   // action_method           : 'osee-script' | ('url' http_verb? | 'java-script') (STRING | param_ref)+ ;   // the tokens after the action_method are method specific, for 'url' they are concatenated together.  the param_ref are used as a key into the OseeAppParams prior to concatenation
   // http_verb               : 'get' | 'put' | 'post' | 'delete' | 'head' ;   // if the http-verb is omitted, 'get' is used

   public PageAppAction(PageAppActionType type, String name, boolean visible) {
      this.type = type;
      this.name = name;
      this.visible = visible;
   }

   String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public PageAppActionType getType() {
      return type;
   }

   public String getName() {
      return name;
   }

   public boolean isVisible() {
      return visible;
   }

   public PageAppAction getDependency() {
      return dependency;
   }

   public void setDependency(PageAppAction dependency) {
      this.dependency = dependency;
   }
}