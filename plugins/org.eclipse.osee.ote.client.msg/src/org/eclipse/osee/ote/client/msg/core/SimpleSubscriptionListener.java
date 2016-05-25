package org.eclipse.osee.ote.client.msg.core;

import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

/**
 * Simple implemention of a {@link ISubscriptionListener} that handles the adding and removing of the passed in
 * {@link IOSEEMessageListener}.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class SimpleSubscriptionListener implements ISubscriptionListener{

   private IOSEEMessageListener listener;
   
   public SimpleSubscriptionListener(IOSEEMessageListener listener){
      this.listener = listener;
   }
   
   @Override
   public void subscriptionInvalidated(IMessageSubscription subscription) {
      
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {
      subscription.getMessage().addListener(listener);
   }

   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
      subscription.getMessage().removeListener(listener);
   }

   @Override
   public void subscriptionActivated(IMessageSubscription subscription) {
      
   }

   @Override
   public void subscriptionCanceled(IMessageSubscription subscription) {
      subscription.getMessage().removeListener(listener);
   }

   @Override
   public void subscriptionNotSupported(IMessageSubscription subscription) {
      
   }

}
