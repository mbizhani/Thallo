package org.devocative.thallo.itest.iservice;

import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.ServiceInfo;
import org.devocative.thallo.itest.Util;
import org.devocative.thallo.itest.domain.service.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.embedded.RedisServer;

import java.util.Optional;

public class RedisService implements IService<Redis> {
	private static final Logger log = LoggerFactory.getLogger(RedisService.class);

	private Redis redis;
	private RedisServer redisServer = null;

	// ------------------------------

	@Override
	public void init(Redis service) {
		redis = service;
	}

	@Override
	public Optional<ServiceInfo> start() {
		boolean defaultPort = redis.getDefaultPort() == null || redis.getDefaultPort();
		final int port = defaultPort ? REDIS_DEFAULT_PORT : Util.findRandomOpenPortOnAllLocalInterfaces();

		redisServer = new RedisServer(port);
		System.setProperty(PARAM_REDIS_PORT, String.valueOf(port));
		System.setProperty(PARAM_REDIS_PASSWORD, "");
		redisServer.start();
		log.info("ITestFactory Startup\n###\nRedis Port: {}\n###\n", port);

		ENV.put(PARAM_REDIS_PORT, String.valueOf(port));
		ENV.put(PARAM_REDIS_PASSWORD, "");

		return Optional.empty();
	}

	@Override
	public void stop() {
		if (redisServer != null) {
			redisServer.stop();
		}

		System.clearProperty(PARAM_REDIS_PORT);
		System.clearProperty(PARAM_REDIS_PASSWORD);
	}
}
