package org.eclipse.osee.ote.message;

import java.nio.ByteBuffer;

public interface BinaryDecoderWorker {

   void scan(long time, String messageName, ByteBuffer buffer, int length);

   void play(long time, String messageName, ByteBuffer buffer, int length);

}
