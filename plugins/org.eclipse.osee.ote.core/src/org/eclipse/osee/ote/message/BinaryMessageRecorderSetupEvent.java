package org.eclipse.osee.ote.message;

import java.io.IOException;

import org.eclipse.osee.ote.message.event.SerializedClassMessage;



public final class BinaryMessageRecorderSetupEvent extends SerializedClassMessage<BinaryRecorderParameters>{

	public static final String TOPIC = "ote/server/binary_message_recorder/cmd/setup";

	public BinaryMessageRecorderSetupEvent() {
		super(TOPIC);
	}
	
	public BinaryMessageRecorderSetupEvent(BinaryRecorderParameters parameters) throws IOException {
		super(TOPIC, parameters);
	}

	public BinaryMessageRecorderSetupEvent(byte[] data) {
		super(data);
	}
}
