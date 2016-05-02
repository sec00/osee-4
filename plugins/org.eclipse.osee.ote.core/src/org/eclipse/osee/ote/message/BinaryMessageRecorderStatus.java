package org.eclipse.osee.ote.message;

import java.io.Serializable;

public final class BinaryMessageRecorderStatus implements Serializable{
	private static final long serialVersionUID = 2981630297586741464L;

	private final String filePath;
	private final int transmissionsRecorder;
	private final long currentOutputFileSize;
	
	public BinaryMessageRecorderStatus(String filePath,
			int transmissionsRecorder, long currentOutputFileSize) {
		super();
		this.filePath = filePath;
		this.transmissionsRecorder = transmissionsRecorder;
		this.currentOutputFileSize = currentOutputFileSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public int getTransmissionsRecorder() {
		return transmissionsRecorder;
	}

	public long getCurrentOutputFileSize() {
		return currentOutputFileSize;
	}
	
	
}
