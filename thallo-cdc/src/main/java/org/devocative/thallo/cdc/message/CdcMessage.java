package org.devocative.thallo.cdc.message;

public class CdcMessage {
	private EAction action;
	private String sender;
	private String receiver;
	private String body;
	private String error;

	// ------------------------------

	public CdcMessage() {
	}

	public CdcMessage(EAction action, String sender, String body) {
		this.action = action;
		this.sender = sender;
		this.body = body;
	}

	// ------------------------------

	public EAction getAction() {
		return action;
	}

	public void setAction(EAction action) {
		this.action = action;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	// ---------------

	@Override
	public String toString() {
		return String.format("CdcMessage(action=[%s], sender=[%s])", action, sender);
	}
}
