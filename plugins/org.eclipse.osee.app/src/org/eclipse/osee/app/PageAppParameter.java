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
public final class PageAppParameter {
   private final PageAppParameterType type;
   private final String name;
   private final boolean visible;
   private String label;
   private String value;
   private String description;
   private PageAppParameter dependency;

   public PageAppParameter(PageAppParameterType type, String name, boolean visible) {
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

   public PageAppParameterType getType() {
      return type;
   }

   public String getName() {
      return name;
   }

   public boolean isVisible() {
      return visible;
   }

   public PageAppParameter getDependency() {
      return dependency;
   }

   public void setDependency(PageAppParameter dependency) {
      this.dependency = dependency;
   }
}