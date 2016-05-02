package org.eclipse.osee.ote.message;

import java.io.IOException;

import org.eclipse.osee.ote.message.event.SerializedClassMessage;



public class BinaryMessageRecorderStatusEvent extends SerializedClassMessage<BinaryMessageRecorderStatus>{

	public static final String TOPIC = "ote/server/binary_message_recorder/status";

	public BinaryMessageRecorderStatusEvent() {
		super(TOPIC);
	}
	
	public BinaryMessageRecorderStatusEvent(BinaryMessageRecorderStatus status) throws IOException {
		super(TOPIC, status);
	}

	public BinaryMessageRecorderStatusEvent(byte[] data) {
		super(data);
	}
}
