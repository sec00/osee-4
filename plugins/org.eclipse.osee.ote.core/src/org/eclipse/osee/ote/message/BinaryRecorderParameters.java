package org.eclipse.osee.ote.message;

import java.io.Serializable;

public class BinaryRecorderParameters implements Serializable{

	private static final long serialVersionUID = 5636273416937795622L;

	private final String outputFileName;
	private final String[] messageClasses;
	
	public BinaryRecorderParameters(String outputFileName,
			String[] messageClasses) {
		super();
		this.outputFileName = outputFileName;
		this.messageClasses = messageClasses;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public String[] getMessageClasses() {
		return messageClasses;
	}
	
}
