package org.devocative.thallo.cdc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devocative.thallo.cdc.annotation.CdcTarget;
import org.devocative.thallo.cdc.message.CdcMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component("CdcMessageTargetListener")
public class CdcMessageTargetListener {
	private static final Logger log = LoggerFactory.getLogger(CdcMessageTargetListener.class);

	private final EntityManager entityManager;
	private final CdcConfiguration configuration;

	// ---------------

	private final ObjectMapper mapper;
	private final Map<String, Optional<Class<?>>> mappedClasses = new ConcurrentHashMap<>();
	private final Map<Class, CdcTargetHandler<Object>> mappedHandler = new ConcurrentHashMap<>();

	// ---------------

	@Value("${spring.application.name}")
	private String appName;

	// ------------------------------

	public CdcMessageTargetListener(EntityManager entityManager, CdcConfiguration configuration) {
		this.entityManager = entityManager;
		this.configuration = configuration;

		final CdcConfiguration.ReceiveEnd receiveEnd = configuration.getReceive();
		if (configuration.isEnabled()) {
			mapper = new ObjectMapper();
			mapper
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

			log.info("* Thallo CdcMessageTargetListener Initiated - Create=[{}] Update=[{}] Delete=[{}], entityManager=[{}], mappedClasses={}",
				receiveEnd.isCreate(), receiveEnd.isUpdate(), receiveEnd.isDelete(), entityManager, receiveEnd.getMappedClasses());
		} else {
			mapper = null;
			log.error("* Thallo CdcMessageTargetListener Disabled in Config!");
		}
	}

	// ------------------------------

	@PostConstruct
	public void init() {
		if (configuration.isEnabled()) {
			final Map<String, String> mappedClasses = configuration.getReceive().getMappedClasses();

			for (Map.Entry<String, String> entry : mappedClasses.entrySet()) {
				try {
					final Class<?> mappedClass = Class.forName(entry.getValue());
					if (!mappedClass.isAnnotationPresent(CdcTarget.class)) {
						throw new CdcException("Invalid mapped class for target without @CdcTarget: " + entry.getValue());
					}
					this.mappedClasses.put(entry.getKey(), Optional.of(mappedClass));
				} catch (ClassNotFoundException e) {
					throw new CdcException("Unknown mapped class for target in config: " + entry.getValue());
				}

			}
		}
	}

	@Transactional
	@KafkaListener(topics = "#{cdcConfig.topic}", autoStartup = "#{cdcConfig.enabled}")
	public void onCdcMessage(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key, @Payload String rawMessage) {

		if (!configuration.isEnabled()) {
			return;
		}

		final CdcMessage message;
		try {
			message = mapper.readValue(rawMessage, CdcMessage.class);
		} catch (IOException e) {
			log.error("CdcMessageTargetListener.onCdcMessage, Invalid CdcMessage for {}", rawMessage, e);
			throw new CdcException("Invalid CdcMessage: " + rawMessage);
		}

		final Optional<Class<?>> targetClassOptional = findTargetClass(key);
		targetClassOptional.ifPresent(targetClass -> {
			log.info("OnCdcMessage: key=[{}] message={}", key, message);

			final CdcTarget cdcTarget = targetClass.getAnnotation(CdcTarget.class);
			final Object targetObject;
			try {
				targetObject = mapper.readValue(message.getBody(), targetClass);
			} catch (IOException e) {
				log.error("CdcMessageTargetListener.onCdcMessage, Invalid target[{}] for message's body: {}", key, message.getBody(), e);
				throw new CdcException("Invalid message's body for target entity: " + key);
			}

			final CdcTargetHandler<Object> handler = findHandler(targetClass, cdcTarget);

			final CdcConfiguration.ReceiveEnd receiveEnd = configuration.getReceive();
			switch (message.getAction()) {
				case Create:
					if (receiveEnd.isCreate() && cdcTarget.create()) {
						final Object beforePersist = handler.beforePersist(targetObject);
						if (beforePersist != null) {
							//entityManager.persist(beforePersist);
							handleEntity(message, beforePersist);
						}
					}
					break;

				case Update:
					if (receiveEnd.isUpdate() && cdcTarget.update()) {
						final Object beforeUpdate = handler.beforeUpdate(targetObject);
						if (beforeUpdate != null) {
							//entityManager.merge(beforeUpdate);
							handleEntity(message, beforeUpdate);
						}
					}
					break;

				case Delete:
					if (receiveEnd.isDelete() && cdcTarget.delete() && handler.beforeDelete(targetObject)) {
						//entityManager.remove(targetObject);
						handleEntity(message, targetObject);
					}
					break;

				default:
					throw new CdcException("Invalid message's action: " + message.getAction());
			}
		});
	}

	// ------------------------------

	private Optional<Class<?>> findTargetClass(String key) {
		if (!mappedClasses.containsKey(key)) {
			if (configuration.getReceive().getMappedClasses().isEmpty()) {
				try {
					final Class<?> cls = Class.forName(key);
					if (cls.isAnnotationPresent(CdcTarget.class)) {
						final Optional<Class<?>> targetClass = Optional.of(cls);
						mappedClasses.put(key, targetClass);
					} else {
						log.error("Cdc Target Class with @CdcTarget: " + key);
						mappedClasses.put(key, Optional.empty());
					}
				} catch (Exception e) {
					log.info("CdcMessageTargetListener, Class ignored: {}", key);
					mappedClasses.put(key, Optional.empty());
				}
			} else {
				log.info("CdcMessageTargetListener, Class not in map: {}", key);
				mappedClasses.put(key, Optional.empty());
			}
		}

		return mappedClasses.get(key);
	}

	private CdcTargetHandler<Object> findHandler(Class targetClass, CdcTarget cdcTarget) {
		if (!mappedHandler.containsKey(targetClass)) {
			try {
				CdcTargetHandler<Object> handler = cdcTarget.handler().newInstance();
				mappedHandler.put(targetClass, handler);
			} catch (Exception e) {
				log.error("CdcMessageTargetListener.onCdcMessage, CdcTargetHandler Instantiation", e);
				throw new CdcException("CdcTargetHandler Instantiation Problem: " + e.getMessage());
			}
		}

		return mappedHandler.get(targetClass);
	}

	private void handleEntity(CdcMessage cdcMessage, Object finalObject) {
		switch (cdcMessage.getAction()) {
			case Create:
				entityManager.persist(finalObject);
				break;

			case Update:
				entityManager.merge(finalObject);
				break;

			case Delete:
				entityManager.remove(finalObject);
				break;
		}
	}
}
