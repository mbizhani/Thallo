package org.devocative.thallo.test.init;

import org.devocative.thallo.test.TestUtil;
import redis.embedded.RedisServer;

import java.util.Map;

class RedisResource extends AbstractResource {
	private RedisServer redisServer;

	@Override
	public Map<String, String> start() {
		final Integer port = TestUtil.findRandomOpenPortOnAllLocalInterfaces();
		redisServer = new RedisServer(port);
		redisServer.start();

		return createMapBuilder()
			.put("spring.redis.host", "localhost")
			.put("spring.redis.port", port.toString())
			.get();
	}

	@Override
	public void stop() {
		redisServer.stop();
	}
}
