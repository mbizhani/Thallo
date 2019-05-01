package org.devocative.thallo.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.devocative.thallo.alog.MethodLogProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import x.y.z.ITestService;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLogIt {

	@Autowired
	private ITestService testService;

	@Autowired
	private MethodLogProperties properties;

	// ------------------------------

	@Test
	public void testAll() {
		assertTrue(properties.getService().getEnabled());

		ListAppender<ILoggingEvent> appender = new ListAppender<>();
		appender.start();

		final Logger root = (Logger) LoggerFactory.getLogger("org.devocative.thallo");
		root.addAppender(appender);


		assertEquals("no = 1", testService.simpleMethod(1));
		final ILoggingEvent event1 = appender.list.get(0);
		assertEquals(Level.INFO, event1.getLevel());
		assertTrue(event1.getMessage().startsWith("}LogIt - {sig: \"TestService.simpleMethod\", args: [1], result: \"no = 1\", dur:"));
		assertTrue(event1.getMessage().endsWith("}"));


		testService.noResult();
		final ILoggingEvent event2 = appender.list.get(1);
		assertEquals(Level.INFO, event2.getLevel());
		assertTrue(event2.getMessage().startsWith("}LogIt - {sig: \"TestService.noResult\", dur:"));
		assertTrue(event2.getMessage().endsWith("}"));


		try {
			testService.throwsError(1);
			fail();
		} catch (Exception e) {
			final ILoggingEvent event3 = appender.list.get(2);
			assertEquals(Level.ERROR, event3.getLevel());
			assertTrue(event3.getMessage().startsWith("}LogIt - {sig: \"TestService.throwsError\", args: [1], dur:"));
			assertTrue(event3.getMessage().contains(", err: \"java.lang.ArithmeticException: / by zero\"}"));
			assertFalse(event3.getMessage().endsWith("}"));
		}


		assertEquals("[1, 100]", testService.ignored(Arrays.asList(1, 100)));


		assertTrue(testService.auth("password"));
		final ILoggingEvent event4 = appender.list.get(3);
		assertEquals(Level.INFO, event4.getLevel());
		assertTrue(event4.getMessage().startsWith("}LogIt - {sig: \"TestService.auth\", args: [***], result: \"***\", dur:"));
		assertTrue(event4.getMessage().endsWith("}"));


		try {
			testService.ignoreError(13);
			fail();
		} catch (Exception e) {
			/*
			Note: ignoreError() has annotated with "place = ELogPlace.Both", however config file overrides it!

			final ILoggingEvent event5 = appender.list.get(4);
			assertEquals(Level.INFO, event5.getLevel());
			assertEquals("{LogIt - {sig: \"TestService.ignoreError\", args: [13]}", event5.getMessage());
			*/

			final ILoggingEvent event5 = appender.list.get(4);
			assertEquals(Level.INFO, event5.getLevel());
			assertTrue(event5.getMessage().startsWith("}LogIt - {sig: \"TestService.ignoreError\", args: [13], dur:"));
			assertTrue(event5.getMessage().endsWith("}"));
		}


		assertEquals(5, appender.list.size());
	}

	@Test
	public void testInThreads() throws Exception {
		final ExecutorService executorService = Executors.newFixedThreadPool(20);
		executorService.invokeAll(IntStream
			.range(1, 40)
			.mapToObj(i -> (Callable<Void>) () -> {
				testAll();
				return null;
			})
			.collect(Collectors.toList())
		);
	}
}
