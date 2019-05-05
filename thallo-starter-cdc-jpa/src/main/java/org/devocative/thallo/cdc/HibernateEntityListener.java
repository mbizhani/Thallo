package org.devocative.thallo.cdc;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@Configuration
public class HibernateEntityListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {
	private static final Logger log = LoggerFactory.getLogger(HibernateEntityListener.class);

	private final EntityManagerFactory factory;
	private final CdcSourceEntityListener cdcSourceEntityListener;

	// ------------------------------

	public HibernateEntityListener(EntityManagerFactory factory, CdcSourceEntityListener cdcSourceEntityListener) {
		this.factory = factory;
		this.cdcSourceEntityListener = cdcSourceEntityListener;
	}

	// ------------------------------

	@PostConstruct
	public void init() {
		SessionFactoryImplementor implementor = factory.unwrap(SessionFactoryImplementor.class);
		EventListenerRegistry service = implementor
			.getServiceRegistry()
			.getService(EventListenerRegistry.class);

		service.prependListeners(EventType.POST_INSERT, this);
		service.prependListeners(EventType.POST_UPDATE, this);
		service.prependListeners(EventType.POST_DELETE, this);

		log.info("* Thallo HibernateEntityListener: Listeners are added");
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		cdcSourceEntityListener.afterPersist(event.getEntity());
	}

	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		return false;
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		cdcSourceEntityListener.afterUpdate(event.getEntity());
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		cdcSourceEntityListener.afterRemove(event.getEntity());
	}
}
