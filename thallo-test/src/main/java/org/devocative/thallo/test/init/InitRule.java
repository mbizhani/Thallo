package org.devocative.thallo.test.init;

import org.devocative.thallo.test.init.db.DatabaseSelectorResource;
import org.devocative.thallo.test.init.db.IDbType;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InitRule extends ExternalResource {
	private static final Logger log = LoggerFactory.getLogger(InitRule.class);

	private final String name;
	private final List<AbstractResource> resources = new ArrayList<>();
	private final Set<String> allKeys = new HashSet<>();

	// ------------------------------

	public InitRule() {
		this("init_rule");
	}

	public InitRule(String name) {
		this.name = name;
	}

	// ------------------------------

	public InitRule enableRedis() {
		resources.add(new RedisResource());
		return this;
	}

	public InitRule enableInMemoryOrRDBMS(IDbType target) {
		resources.add(new DatabaseSelectorResource(target));
		return this;
	}

	// ---------------

	@Override
	protected void before() throws Exception {
		for (AbstractResource resource : resources) {
			resource.setName(name);

			final Map<String, String> envParams = resource.start();
			for (Map.Entry<String, String> entry : envParams.entrySet()) {
				System.setProperty(entry.getKey(), entry.getValue());
			}

			allKeys.addAll(envParams.keySet());

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
