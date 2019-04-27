package org.devocative.thallo.cdc;

public class CdcException extends RuntimeException {
	public CdcException(String message) {
		super(message);
	}

	public CdcException(Throwable cause) {
		super(cause);
	}
}
