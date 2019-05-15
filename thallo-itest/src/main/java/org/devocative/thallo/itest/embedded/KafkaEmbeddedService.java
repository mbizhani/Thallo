package org.devocative.thallo.itest.embedded;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

public class KafkaEmbeddedService implements IEmbeddedService {
	private static final Logger log = LoggerFactory.getLogger(KafkaEmbeddedService.class);
	private static final String BOOTSTRAP_SERVERS_PROP = "spring.kafka.bootstrap-servers";

	private EmbeddedKafkaBroker embeddedKafka = null;

	@Override
	public void start() {
		embeddedKafka = new EmbeddedKafkaBroker(1, true, 2);
		embeddedKafka.afterPropertiesSet();
		System.setProperty(BOOTSTRAP_SERVERS_PROP, embeddedKafka.getBrokersAsString());
		log.info("ITestFactory Startup\n###\nKafka: {}\n###\n", embeddedKafka.getBrokersAsString());
	}

	@Override
	public void stop() {
		if (embeddedKafka != null) {
			embeddedKafka.destroy();
		}
		System.clearProperty(BOOTSTRAP_SERVERS_PROP);
	}
}
