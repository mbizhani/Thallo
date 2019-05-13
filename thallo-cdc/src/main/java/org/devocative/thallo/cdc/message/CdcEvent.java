package org.devocative.thallo.cdc.message;

import java.util.Map;

public class CdcEvent {
	private final EAction action;
	private final Object target;
	private final Map<String, Object> source;

	public CdcEvent(EAction action, Object target, Map<String, Object> source) {
		this.action = action;
		this.target = target;
		this.source = source;
	}

	public EAction getAction() {
		return action;
	}

	public Object getTarget() {
		return target;
	}

	public Map<String, Object> getSource() {
		return source;
	}

	@Override
	public String toString() {
		return String.format("CdcEvent(%s, %s)", action, target);
	}
}
