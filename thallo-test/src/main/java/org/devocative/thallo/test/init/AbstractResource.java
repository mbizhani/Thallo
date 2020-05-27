package org.devocative.thallo.test.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractResource {
	protected static final Logger log = LoggerFactory.getLogger(AbstractResource.class);

	private static Properties FIRST, SECOND;

	static {
		FIRST = new Properties();
		try {
			FIRST.load(AbstractResource.class.getResourceAsStream("/init.rule.properties"));
			log.info("init.rule.properties loaded successfully");
		} catch (Exception e) {
			throw new RuntimeException("Can't load 'init.rule.properties'!");
		}

		SECOND = new Properties();
		try {
			SECOND.load(AbstractResource.class.getResourceAsStream("/init.rule-custom.properties"));
			log.info("init.rule-custom.properties loaded successfully");
		} catch (Exception e) {
			log.info("'init.rule-custom.properties' not found!");
		}
	}

	public abstract Map<String, String> start();

	public void stop() {
	}

	// ------------------------------

	protected MapBuilder createMapBuilder() {
		return new MapBuilder();
	}

	protected static String getConfig(String key) {
		return SECOND.getProperty(key, FIRST.getProperty(key));
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
