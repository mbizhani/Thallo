package org.devocative.thallo.itest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;
import org.devocative.thallo.itest.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ITestFactory {
	private static final Logger log = LoggerFactory.getLogger(ITestFactory.class);

	private final Map<String, Object> CONTEXT = new HashMap<>();
	private final Binding binding = new Binding(CONTEXT);
	private final GroovyShell shell = new GroovyShell();
	private final List<ConfigurableApplicationContext> applicationContexts = new ArrayList<>();
	private final RestTemplate template = new RestTemplate();
	private final ObjectMapper mapper = new ObjectMapper();


	public void run() {
		EmbeddedKafkaBroker embeddedKafka = null;
		RedisServer redisServer = null;

		try {
			XStream xStream = new XStream();
			xStream.processAnnotations(ITest.class);
			ITest iTest = (ITest) xStream.fromXML(ITestFactory.class.getResourceAsStream("/itest.xml"));

			if (!iTest.getBootApps().isEmpty()) {
				embeddedKafka = new EmbeddedKafkaBroker(1, true);
				embeddedKafka.afterPropertiesSet();
				System.setProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());
				log.info("ITestFactory Startup\n###\nKafka: {}\n###\n", embeddedKafka.getBrokersAsString());

				final Integer redisPort = findRandomOpenPortOnAllLocalInterfaces();
				redisServer = new RedisServer(redisPort);
				System.setProperty("spring.redis.port", redisPort.toString());
				System.setProperty("spring.redis.password", "");
				redisServer.start();
				log.info("ITestFactory Startup\n###\nRedis Port: {}\n###\n", redisPort);
			}

			if (!iTest.getBootApps().isEmpty()) {
				iTest.getBootApps().forEach(this::startApp);
			} else if (!iTest.getServices().isEmpty()) {
				iTest.getServices().forEach(this::processService);
			} else {
				throw new RuntimeException("Invalid XML: no <bootApp/> or <service/>");
			}

			iTest.getParams().forEach(param ->
				CONTEXT.put(param.getName(),
					processStringTemplate(param.getValue())
				)
			);

			template.setErrorHandler(new ResponseErrorHandler() {
				@Override
				public boolean hasError(ClientHttpResponse clientHttpResponse) {
					return false;
				}

				@Override
				public void handleError(ClientHttpResponse clientHttpResponse) {
				}
			});
			System.setProperty("spring.jackson.serialization.INDENT_OUTPUT", "true");

			log.info("\n#########################\nI T E S T   F A C T O R Y\n#########################\n");

			iTest.getRests().forEach(this::execute);
		} finally {
			applicationContexts.forEach(ConfigurableApplicationContext::close);
			if (embeddedKafka != null) {
				embeddedKafka.destroy();
			}
			if (redisServer != null) {
				redisServer.stop();
			}

		}
	}

	// ------------------------------

	private void execute(Rest rest) {
		final AppInfo info = (AppInfo) CONTEXT.get(rest.getApp());
		final String url = "http://" + info.host + ":" + info.port + info.context + processStringTemplate(rest.getUri());
		final String rqBody = rest.getBody() != null ? processStringTemplate(rest.getBody()).trim() : "";
		log.info("# R E S T #\n{} {}\n{}", rest.getMethod(), url, rqBody);

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final HttpEntity<?> entity = new HttpEntity<>(rqBody, headers);
		ResponseEntity<String> exchange = template.exchange(url, rest.getMethod(), entity, String.class);
		final HttpStatus rsStatus = exchange.getStatusCode();
		final String rsBody = exchange.getBody();
		log.info("# R E S T   R E S U L T#\nSTATUS: [{}]\n{}", rsStatus, prettyJson(rsBody));

		final RestResponse response = rest.getResponse();
		if (rsStatus == response.getStatus()) {
			Map rs;
			if (rsBody.startsWith("{")) {
				rs = jsonAsMap(rsBody);
				//System.out.println("###### map = " + rs);
			} else if (rsBody.startsWith("[")) {
				List list = jsonAsList(rsBody);
				//System.out.println("###### list = " + list);
				rs = new HashMap();
				rs.put("list", list);
			} else {
				throw new RuntimeException("Invalid JSON response");
			}

			response.getAsserts().forEach(restAssert -> evalAssert(rs, restAssert));
		} else {
			throw new TestFailException("Invalid status code", response.getStatus(), rsStatus);
		}
	}

	private void evalAssert(Map response, RestAssert anAssert) {
		final Object val = evaluate(response, anAssert.getPath());
		if (val != null) {
			if (!anAssert.getType().getJavaType().isAssignableFrom(val.getClass())) {
				throw new TestFailException(String.format("Invalid assert type for [%s]", anAssert.getPath()), anAssert.getType().getJavaType(), val.getClass());
			}

			// TODO check value

			if (anAssert.getStoreAsParam() != null) {
				CONTEXT.put(anAssert.getStoreAsParam(), val);
			}
		} else {
			if (anAssert.isHasValue() || anAssert.getStoreAsParam() != null) {
				throw new TestFailException(String.format("Null value for assert [%s]", anAssert.getPath()));
			}
		}
	}

	private void startApp(BootApp app) {
		try {
			Map<String, Object> p1 = new HashMap<>();
			if (app.getProfile() != null) {
				p1.put("spring.profiles.active", app.getProfile());
			}

			if (app.getContext() != null) {
				final int port = findRandomOpenPortOnAllLocalInterfaces();
				p1.put("server.port", port);
				CONTEXT.put(app.getName(), new AppInfo("localhost", port, app.getContext()));
			}

			Class<?> appClass = Class.forName(app.getFqn());
			ConfigurableApplicationContext context = new SpringApplicationBuilder(appClass)
				.properties(p1)
				.build()
				.run();
			applicationContexts.add(context);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void processService(Service service) {
		AppInfo info = new AppInfo(service.getHost(), service.getPort(), service.getContext());
		CONTEXT.put(service.getName(), info);
	}

	// --------------- helper

	private Object evaluate(Map map, String expr) {
		Script script = shell.parse(expr);
		script.setBinding(new Binding(map));
		return script.run();
	}

	private String processStringTemplate(String tmpl) {
		Script script = shell.parse("\"\"\"" + tmpl + "\"\"\"");
		script.setBinding(binding);
		Object run = script.run();
		return run != null ? run.toString() : null;
	}

	private String prettyJson(String json) {
		if (json != null) {
			try {
				Object obj = mapper.readValue(json, Object.class);
				return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return "";
	}

	private Map jsonAsMap(String json) {
		try {
			return mapper.readValue(json, Map.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private List jsonAsList(String json) {
		try {
			return mapper.readValue(json, List.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Integer findRandomOpenPortOnAllLocalInterfaces() {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@RequiredArgsConstructor
	private static class AppInfo {
		private final String host;
		private final int port;
		private final String context;
	}
}
