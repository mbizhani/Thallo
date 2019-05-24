package org.devocative.thallo.itest.iservice;

import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.ServiceInfo;
import org.devocative.thallo.itest.Util;
import org.devocative.thallo.itest.domain.service.BootClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BootClassService implements IService<BootClass> {
	private static final Logger log = LoggerFactory.getLogger(BootClassService.class);

	private BootClass app;
	private ConfigurableApplicationContext context;

	@Override
	public void init(BootClass service) {
		app = service;
	}

	@Override
	public Optional<ServiceInfo> start() {
		/*
		TODO
		if (CONTEXT.containsKey(app.getName())) {
			log.warn("Service already defined: {}", app.getName());
			return;
		}*/
		ServiceInfo result = null;
		try {
			Map<String, Object> p1 = new HashMap<>();
			if (app.getProfile() != null) {
				p1.put(PARAM_PROFILE, app.getProfile());
			}

			if (app.getContext() != null) {
				final int port = Util.findRandomOpenPortOnAllLocalInterfaces();
				p1.put(PARAM_PORT, port);
				p1.put(PARAM_CONTEXT, app.getContext());
				result = new ServiceInfo("localhost", port, app.getContext());
			}

			Class<?> appClass = Class.forName(app.getFqn());
			context = new SpringApplicationBuilder(appClass)
				.properties(p1)
				.build()
				.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return Optional.ofNullable(result);
	}

	@Override
	public void stop() {
		context.close();
	}
}
