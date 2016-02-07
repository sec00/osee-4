/*
 * Created on Nov 7, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.elements;

import java.util.List;

import org.eclipse.osee.ote.message.elements.Element;

/**
 * @author Michael P. Masterson
 */
public interface ElementGroup<E extends Element> {

   public void setElementList(List<E> elements);
}
