package org.devocative.thallo.cdc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.devocative.thallo.cdc.annotation.CdcSource;
import org.devocative.thallo.cdc.message.CdcMessage;
import org.devocative.thallo.cdc.message.EAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

@Configuration
public class CdcSourceEntityListener {
	private static final Logger log = LoggerFactory.getLogger(CdcSourceEntityListener.class);

	private final CdcConfiguration configuration;
	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper entityMapper;
	private final ObjectMapper messageMapper;

	// ---------------

	@Value("${spring.application.name}")
	private String appName;

	// ------------------------------

	public CdcSourceEntityListener(CdcConfiguration configuration, KafkaTemplate<String, String> kafkaTemplate) {
		this.configuration = configuration;
		this.kafkaTemplate = kafkaTemplate;

		final CdcConfiguration.SendEnd send = configuration.getSend();
		if (configuration.isEnabled()) {
			log.info("* Thallo CdcSourceEntityListener Initiated - Create=[{}] Update=[{}] Delete=[{}]",
				send.isCreate(), send.isUpdate(), send.isDelete());

			entityMapper = new ObjectMapper();
			entityMapper.setAnnotationIntrospector(new CdcAnnotationIntrospector());

			messageMapper = new ObjectMapper();
			messageMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
		} else {
			entityMapper = null;
			messageMapper = null;
			log.error("* Thallo CdcSourceEntityListener Disabled in Config!");
		}
	}

	// ------------------------------

	@PostPersist
	public void afterPersist(Object target) {
		final Class<?> cls = target.getClass();
		if (configuration.isEnabled() && configuration.getSend().isCreate() && cls.isAnnotationPresent(CdcSource.class)) {
			final CdcSource cdcSource = cls.getAnnotation(CdcSource.class);
			if (cdcSource.create()) {
				log.info("CdcSource.Persist: {} - [{}]", cls.getName(), target);

				sendMessage(target, EAction.Create);
			}
		}
	}

	@PostUpdate
	public void afterUpdate(Object target) {
		final Class<?> cls = target.getClass();
		if (configuration.isEnabled() && configuration.getSend().isUpdate() && cls.isAnnotationPresent(CdcSource.class)) {
			final CdcSource cdcSource = cls.getAnnotation(CdcSource.class);
			if (cdcSource.update()) {
				log.info("CdcSource.Update: {} - [{}]", cls.getName(), target);

				sendMessage(target, EAction.Update);
			}
		}
	}

	@PostRemove
	public void afterRemove(Object target) {
		final Class<?> cls = target.getClass();
		if (configuration.isEnabled() && configuration.getSend().isDelete() && cls.isAnnotationPresent(CdcSource.class)) {
			final CdcSource cdcSource = cls.getAnnotation(CdcSource.class);
			if (cdcSource.delete()) {
				log.info("CdcSource.Delete: {} - [{}]", cls.getName(), target);

				sendMessage(target, EAction.Delete);
			}
		}
	}

	// ------------------------------

	private void sendMessage(Object target, EAction action) {
		Class<?> cls = target.getClass();
		try {
			final String body = entityMapper.writeValueAsString(target);
			final String message = messageMapper.writeValueAsString(new CdcMessage(action, appName, body));
			kafkaTemplate.send(new ProducerRecord<>(configuration.getTopic(), cls.getName(), message));
		} catch (Exception e) {
			log.error("CdcSourceEntityListener, action=[{}]", action, e);
			throw new CdcException(e);
		}
	}

}
