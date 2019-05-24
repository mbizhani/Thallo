package org.devocative.thallo.itest.iservice;

import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.ServiceInfo;
import org.devocative.thallo.itest.domain.service.Kafka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.Optional;

public class KafkaService implements IService<Kafka> {
	private static final Logger log = LoggerFactory.getLogger(KafkaService.class);

	private Kafka kafka;
	private EmbeddedKafkaBroker embeddedKafka = null;

	// ------------------------------


	@Override
	public void init(Kafka service) {
		kafka = service;
	}

	@Override
	public Optional<ServiceInfo> start() {
		embeddedKafka = new EmbeddedKafkaBroker(1, true, 2);
		if (kafka.getDefaultPort() == null || kafka.getDefaultPort()) {
			embeddedKafka.kafkaPorts(KAFKA_DEFAULT_PORT);
		}
		embeddedKafka.afterPropertiesSet();
		System.setProperty(PARAM_KAFKA, embeddedKafka.getBrokersAsString());
		log.info("ITestFactory Startup\n###\nKafka: {}\n###\n", embeddedKafka.getBrokersAsString());

		ENV.put(PARAM_KAFKA, embeddedKafka.getBrokersAsString());
		return Optional.empty();
	}

	@Override
	public void stop() {
		if (embeddedKafka != null) {
			embeddedKafka.destroy();
		}
		System.clearProperty(PARAM_KAFKA);
	}
}
