package org.devocative.thallo.core.autoconfigure;

import org.devocative.thallo.core.aspect.MethodLogAspect;
import org.devocative.thallo.core.aspect.MethodLogConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MethodLogConfiguration.class)
public class CoreAutoConfiguration {
	private static final Logger log = LoggerFactory.getLogger(CoreAutoConfiguration.class);

	// ------------------------------

	public CoreAutoConfiguration() {
		log.info("* Thallo CoreAutoConfiguration Initiated");
	}

	// ------------------------------

	@Bean
	public MethodLogAspect logAspect(MethodLogConfiguration configuration) {
		return new MethodLogAspect(configuration);
	}
}
