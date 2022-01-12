package org.devocative.thallo.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.devocative.thallo.alog.MethodLogProperties;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import x.y.z.ITestService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("no-service")
public class NoServiceTestLogIt {

	@Autowired
	private ITestService testService;

	@Autowired
	private MethodLogProperties properties;

	@Test
	@Profile("no-service")
	public void testServiceDisabled() {
		assertFalse(properties.getService().getEnabled());

		ListAppender<ILoggingEvent> appender = new ListAppender<>();
		appender.start();

		final Logger root = (Logger) LoggerFactory.getLogger("org.devocative.thallo");
		root.addAppender(appender);

		assertEquals("no = 1", testService.simpleMethod(1));
		assertTrue(appender.list.isEmpty());

		testService.noResult();
		assertTrue(appender.list.isEmpty());

		try {
			testService.throwsError(1);
			fail();
		} catch (Exception e) {
			assertTrue(appender.list.isEmpty());
		}

		assertTrue(testService.auth("password"));
		final ILoggingEvent event1 = appender.list.get(0);
		assertEquals(Level.INFO, event1.getLevel());
		assertTrue(event1.getMessage().startsWith("}LogIt - {sig: \"TestService.auth\", args: [***], result: \"***\", dur:"));
		assertTrue(event1.getMessage().endsWith("}"));
	}
}
