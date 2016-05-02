package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;


public final class BinaryMessageRecorderStopEvent extends OteEventMessage{

	public static final String TOPIC = "ote/server/binary_message_recorder/cmd/stop";

	public static final int SIZE = 256 + 4 + 4;
	
	public final StringElement FILE_NAME;

	public final IntegerElement iP_ADDRESS;

	public final IntegerElement PORT;
	
	public BinaryMessageRecorderStopEvent() {
	   super(BinaryMessageRecorderStopEvent.class.getSimpleName(), TOPIC, SIZE);
	   FILE_NAME = new StringElement(this, "FILE_NAME", getDefaultMessageData(), 0, 0, 8*256 - 1);
	   iP_ADDRESS = new IntegerElement(this, "IP_ADDRESS", getDefaultMessageData(), 256, 0, 31);
	   PORT = new IntegerElement(this, "PORT", getDefaultMessageData(), 260, 0, 31);
	   
	   addElements(FILE_NAME, iP_ADDRESS, PORT);
	}
}
