/*
 *
 * Created on Mar 23, 2007
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.Namespace;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IMessageCreationListener {
   <CLASSTYPE extends Message> void onPreCreate(Class<CLASSTYPE> messageClass, IMessageRequestor requestor, boolean writer);

   <CLASSTYPE extends Message> void onPostCreate(Class<CLASSTYPE> messageClass, IMessageRequestor requestor, boolean writer, CLASSTYPE message, Namespace namespace);

   <CLASSTYPE extends Message> void onInstanceRequest(Class<CLASSTYPE> messageClass, CLASSTYPE message, IMessageRequestor requestor, boolean writer);
}
