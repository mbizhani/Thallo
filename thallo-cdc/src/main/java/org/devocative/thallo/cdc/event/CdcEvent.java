package org.devocative.thallo.cdc.event;

import java.util.Map;

public class CdcEvent {
	private final EEventType type;
	private final Object target;
	private final Map<String, Object> source;

	public CdcEvent(EEventType type, Object target, Map<String, Object> source) {
		this.type = type;
		this.target = target;
		this.source = source;
	}

	public EEventType getType() {
		return type;
	}

	public Object getTarget() {
		return target;
	}

	public Map<String, Object> getSource() {
		return source;
	}

	@Override
	public String toString() {
		return String.format("CdcEvent(%s, %s)", type, target);
	}
}
