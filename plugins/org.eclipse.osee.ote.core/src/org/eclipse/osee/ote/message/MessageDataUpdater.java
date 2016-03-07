package org.eclipse.osee.ote.message;

import java.nio.ByteBuffer;

public interface MessageDataUpdater {
   void update(MessageId id, ByteBuffer data);
}
