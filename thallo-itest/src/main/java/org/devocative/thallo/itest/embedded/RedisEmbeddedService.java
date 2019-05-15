package org.devocative.thallo.itest.embedded;

import org.devocative.thallo.itest.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.embedded.RedisServer;

public class RedisEmbeddedService implements IEmbeddedService {
	private static final Logger log = LoggerFactory.getLogger(RedisEmbeddedService.class);

	private static final String PORT_PROP = "spring.redis.port";
	private static final String PASSWORD_PROP = "spring.redis.password";

	private RedisServer redisServer = null;

	@Override
	public void start() {
		final Integer redisPort = Util.findRandomOpenPortOnAllLocalInterfaces();
		redisServer = new RedisServer(redisPort);
		System.setProperty(PORT_PROP, redisPort.toString());
		System.setProperty(PASSWORD_PROP, "");
		redisServer.start();
		log.info("ITestFactory Startup\n###\nRedis Port: {}\n###\n", redisPort);

	}

	@Override
	public void stop() {
		if (redisServer != null) {
			redisServer.stop();
		}

		System.clearProperty(PASSWORD_PROP);
		System.clearProperty(PORT_PROP);
	}
}
