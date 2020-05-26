package org.devocative.thallo.test.init;

import org.devocative.thallo.test.init.db.DatabaseSelectorResource;
import org.devocative.thallo.test.init.db.EDbType;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InitRule extends ExternalResource {
	private static final Logger log = LoggerFactory.getLogger(InitRule.class);

	private List<AbstractResource> resources = new ArrayList<>();
	private Set<String> allKeys = new HashSet<>();

	// ------------------------------

	public InitRule enableRedis() {
		resources.add(new RedisResource());
		return this;
	}

	public InitRule enableInMemoryOrRDBMS(EDbType target) {
		resources.add(new DatabaseSelectorResource(target));
		return this;
	}

	public InitRule enableRDBMS(EDbType type) {
		return this;
	}

	// ---------------

	@Override
	protected void before() {
		for (AbstractResource resource : resources) {
			final Map<String, String> start = resource.start();
			for (Map.Entry<String, String> entry : start.entrySet()) {
				System.setProperty(entry.getKey(), entry.getValue());
			}

			allKeys.addAll(start.keySet());

			log.info("Resource Started: {}", resource.getClass().getSimpleName());
		}
	}

	@Override
	protected void after() {
		for (AbstractResource resource : resources) {
			resource.stop();
		}

		for (String key : allKeys) {
			System.clearProperty(key);
		}
	}
}
