package org.devocative.thallo.cdc;

import org.devocative.thallo.cdc.message.CdcEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CdcEventService {
	private final Logger log = LoggerFactory.getLogger(CdcEventService.class);

	private final ApplicationEventPublisher appEventPublisher;

	// ------------------------------

	public CdcEventService(ApplicationEventPublisher appEventPublisher) {
		this.appEventPublisher = appEventPublisher;
	}

	// ------------------------------

	@Async
	public void publishEvent(CdcEvent event) {
		log.info("CdcEvent Published: {}", event);

		appEventPublisher.publishEvent(event);
	}
}
