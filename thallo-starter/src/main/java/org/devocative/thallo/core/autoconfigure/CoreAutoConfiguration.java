package org.devocative.thallo.core.autoconfigure;

import org.devocative.thallo.core.MethodLogAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreAutoConfiguration {
	private static final Logger log = LoggerFactory.getLogger(CoreAutoConfiguration.class);

	// ------------------------------

	public CoreAutoConfiguration() {
		log.info("* Thallo CoreAutoConfiguration Initiated");
	}

	// ------------------------------

	@Bean
	public MethodLogAspect logAspect() {
		return new MethodLogAspect();
	}
}
