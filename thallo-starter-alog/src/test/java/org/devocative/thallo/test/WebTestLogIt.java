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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebTestLogIt {

	@LocalServerPort
	private Integer port;

	private TestRestTemplate restTemplate = new TestRestTemplate();
	private HttpHeaders headers = new HttpHeaders();

	@Autowired
	private MethodLogProperties properties;

	@Test
	public void testRest() {
		assertTrue(properties.getService().getEnabled());

		ListAppender<ILoggingEvent> appender = new ListAppender<>();
		appender.start();

		final Logger root = (Logger) LoggerFactory.getLogger("org.devocative.thallo");
		root.addAppender(appender);

		HttpEntity entity = new HttpEntity(headers);
		final ResponseEntity<String> responseEntity = restTemplate.exchange(String.format("http://localhost:%s/ignore", port), HttpMethod.GET, entity, String.class);
		assertEquals("[]", responseEntity.getBody());

		final ILoggingEvent event1 = appender.list.get(0);
		assertEquals(Level.INFO, event1.getLevel());
		assertTrue(event1.getMessage().startsWith("}LogIt - {sig: \"TestController.ignore\", result: \"[]\", dur: "));
		assertTrue(event1.getMessage().endsWith("}"));
	}

	@Test
	public void testInThreads() throws Exception {
		final ExecutorService executorService = Executors.newFixedThreadPool(20);
		executorService.invokeAll(IntStream
			.range(1, 40)
			.mapToObj(i -> (Callable<Void>) () -> {
				testRest();
				return null;
			})
			.collect(Collectors.toList())
		);
	}
}
