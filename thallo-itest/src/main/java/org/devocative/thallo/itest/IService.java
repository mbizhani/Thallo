package org.devocative.thallo.itest;

import org.devocative.thallo.itest.domain.AbstractService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface IService<S extends AbstractService> {
	Map<String, Object> CONTEXT = new HashMap<>();
	Map<String, String> ENV = new HashMap<>();

	String PARAM_PROFILE = "spring.profiles.active";
	String PARAM_PORT = "server.port";
	String PARAM_CONTEXT = "server.servlet.context-path";
	String PARAM_KAFKA = "spring.kafka.bootstrap-servers";
	String PARAM_REDIS_PORT = "spring.redis.port";
	String PARAM_REDIS_PASSWORD = "spring.redis.password";

	Integer KAFKA_DEFAULT_PORT = 9092;
	Integer REDIS_DEFAULT_PORT = 6379;

	default void init(S service) {
	}

	Optional<ServiceInfo> start();

	void stop();
}
