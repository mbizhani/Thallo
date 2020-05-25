package org.devocative.thallo.test.init;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractResource {

	public abstract Map<String, String> start();

	public void stop() {
	}

	// ------------------------------

	protected MapBuilder createMapBuilder() {
		return new MapBuilder();
	}

	// ------------------------------

	protected static class MapBuilder {
		private Map<String, String> params = new HashMap<>();

		public MapBuilder put(String key, String value) {
			params.put(key, value);
			return this;
		}

		public Map<String, String> get() {
			return params;
		}
	}
}
