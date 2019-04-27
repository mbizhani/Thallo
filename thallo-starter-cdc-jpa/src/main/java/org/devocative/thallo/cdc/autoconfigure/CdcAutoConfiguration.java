package org.devocative.thallo.cdc.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.devocative.thallo.cdc")
public class CdcAutoConfiguration {
	private static final Logger log = LoggerFactory.getLogger(CdcAutoConfiguration.class);

	// ------------------------------

	public CdcAutoConfiguration() {
		log.info("* Thallo CDC Starter");
	}
}
