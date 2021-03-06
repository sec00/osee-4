/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
�* All rights reserved. This program and the accompanying materials
�* are made available under the terms of the Eclipse Public License v1.0
�* which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.workdef.viewer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Abstract prototype of a model element.
 * <p>
 * This class provides features necessary for all model elements, like:
 * </p>
 * <ul>
 * <li>property-change support (used to notify edit parts of model changes),</li>
 * <li>property-source support (used to display property values in the Property View) and</li>
 * <li>serialization support (the model hierarchy must be serializable, so that the editor can save and restore a binary
 * representation. You might not need this, if you store the model a non-binary form like XML).</li>
 * </ul>
 * 
 * @author Donald G. Dunne
 */
public abstract class ModelElement implements IPropertySource {
   /** An empty property descriptor. */
   private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];

   /** Delegate used to implement property-change-support. */
   private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);
   protected Map<Object, Object> propertyValues;

   protected void initializePropertyValues() {
      if (propertyValues == null) {
         propertyValues = new HashMap<>();
      }
   }

   public abstract Result doSave(SkynetTransaction transaction) throws OseeCoreException;

   public abstract Result validForSave() throws OseeCoreException;

   /**
    * Attach a non-null PropertyChangeListener to this object.
    * 
    * @param l a non-null PropertyChangeListener instance
    * @throws IllegalArgumentException if the parameter is null
    */
   public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
      if (l == null) {
         throw new IllegalArgumentException();
      }
      pcsDelegate.addPropertyChangeListener(l);
   }

   /**
    * Report a property change to registered listeners (for example edit parts).
    * 
    * @param property the programmatic name of the property that changed
    * @param oldValue the old value of this property
    * @param newValue the new value of this property
    */
   protected void firePropertyChange(String property, Object oldValue, Object newValue) {
      if (pcsDelegate.hasListeners(property)) {
         pcsDelegate.firePropertyChange(property, oldValue, newValue);
      }
   }

   /**
    * Returns a value for this property source that can be edited in a property sheet.
    * <p>
    * My personal rule of thumb:
    * </p>
    * <ul>
    * <li>model elements should return themselves and</li>
    * <li>custom IPropertySource implementations (like DimensionPropertySource in the GEF-logic example) should return
    * an editable value.</li>
    * </ul>
    * <p>
    * Override only if necessary.
    * </p>
    * 
    * @return this instance
    */
   @Override
   public Object getEditableValue() {
      return this;
   }

   /**
    * Children should override this. The default implementation returns an empty array.
    */
   @Override
   public IPropertyDescriptor[] getPropertyDescriptors() {
      return EMPTY_ARRAY;
   }

   /**
    * Children should override this. The default implementation returns null.
    */
   @Override
   public Object getPropertyValue(Object id) {
      return propertyValues.get(id);
   }

   /**
    * Children should override this. The default implementation returns false.
    */
   @Override
   public boolean isPropertySet(Object id) {
      return false;
   }

   /**
    * Deserialization constructor. Initializes transient fields.
    * 
    * @see java.io.Serializable
    */
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      pcsDelegate = new PropertyChangeSupport(this);
   }

   /**
    * Remove a PropertyChangeListener from this component.
    * 
    * @param l a PropertyChangeListener instance
    */
   public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
      if (l != null) {
         pcsDelegate.removePropertyChangeListener(l);
      }
   }

   /**
    * Children should override this. The default implementation does nothing.
    */
   @Override
   public void resetPropertyValue(Object id) {
      // do nothing
   }

   /**
    * Children should override this. The default implementation does nothing.
    */
   @Override
   public void setPropertyValue(Object id, Object value) {
      initializePropertyValues();
      propertyValues.put(id, value);
   }
}
