package org.devocative.thallo.itest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.devocative.thallo.itest.domain.*;
import org.devocative.thallo.itest.embedded.IEmbeddedService;
import org.devocative.thallo.itest.embedded.KafkaEmbeddedService;
import org.devocative.thallo.itest.embedded.RedisEmbeddedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ITestFactory {
	private static final Logger log = LoggerFactory.getLogger(ITestFactory.class);
	private static final String CLASSPATH_PREFIX = "classpath:";

	private final Map<String, Object> CONTEXT = new HashMap<>();
	private final Binding binding = new Binding(CONTEXT);
	private final GroovyShell shell = new GroovyShell();
	private final RestTemplate template = new RestTemplate();
	private final ObjectMapper mapper = new ObjectMapper();

	private final List<ConfigurableApplicationContext> applicationContexts = new ArrayList<>();
	private final List<IEmbeddedService> embeddedServices = new ArrayList<>();
	private final String xmlFile;

	private ITest iTest;

	public ITestFactory(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public void init() {
		try {
			InputStream stream;
			if (xmlFile.startsWith(CLASSPATH_PREFIX)) {
				stream = getClass().getResourceAsStream(xmlFile.substring(CLASSPATH_PREFIX.length()));
				if (stream == null) {
					throw new FileNotFoundException(xmlFile);
				}
			} else {
				stream = new FileInputStream(xmlFile);
			}

			XStream xStream = new XStream();
			xStream.processAnnotations(ITest.class);
			xStream.processAnnotations(BootApp.class);
			xStream.processAnnotations(RemoteService.class);
			iTest = (ITest) xStream.fromXML(stream);

			if (iTest.getKafka() != null) {
				embeddedServices.add(new KafkaEmbeddedService());
			}
			if (iTest.getRedis() != null) {
				embeddedServices.add(new RedisEmbeddedService());
			}
			embeddedServices.forEach(IEmbeddedService::start);

			for (AbstractService service : iTest.getServices()) {
				if (service instanceof BootApp) {
					startApp((BootApp) service);
				} else if (service instanceof RemoteService) {
					processService((RemoteService) service);
				} else {
					throw new RuntimeException("Invalid service type");
				}
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

			final String banner = IOUtils.toString(getClass().getResourceAsStream("/itest-banner.txt"), "utf8");
			log.info(banner);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		iTest.getRests().forEach(this::execute);

		log.info("\n\n" +
			"#######################################################\n" +
			"## I T E S T   P A S S E D   S U C C E S S F U L L Y ##\n" +
			"#######################################################\n");
	}

	public void destroy() {
		applicationContexts.forEach(ConfigurableApplicationContext::close);
		embeddedServices.forEach(IEmbeddedService::stop);
	}

	// ------------------------------

	private void execute(Rest rest) {
		final String url;
		if (rest.getUri() != null) {
			final AppInfo info = (AppInfo) CONTEXT.get(rest.getService());
			if (info == null) {
				throw new TestFailException(String.format("Invalid 'service' for rest [%s]: %s", rest.getUri(), rest.getService()));
			}
			url = "http://" + info.host + ":" + info.port + info.context + processStringTemplate(rest.getUri());
		} else if (rest.getUrl() != null) {
			String pUrl = processStringTemplate(rest.getUrl());
			url = pUrl.startsWith("http://") || pUrl.startsWith("https://") ? pUrl : "http://" + pUrl;
		} else {
			throw new TestFailException("Invalid rest without uri or url");
		}

		if (rest.getResponse() != null) {
			processRq(url, rest, null);
		} else if (!rest.getRequests().isEmpty()) {
			rest.getRequests().forEach(request -> processRq(url, rest, request));
		} else {
			throw new TestFailException("Invalid XML: No <response/> nor <request/>");
		}
	}

	private void processRq(String url, Rest rest, RestRequest request) {
		final String rqBody;
		if (request == null) {
			rqBody = "";
		} else {
			rqBody = request.getBody() != null ? processStringTemplate(request.getBody()).trim() : "";
		}
		log.info("\n\n## R E S T   R E Q U E S T ##\n{} {}\n{}\n\n", rest.getMethod(), url, rqBody);

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (rest.getBearerToken() != null) {
			headers.setBearerAuth(processStringTemplate(rest.getBearerToken()));
		}

		final HttpEntity<?> entity = new HttpEntity<>(rqBody, headers);
		ResponseEntity<String> exchange = template.exchange(url, rest.getMethod(), entity, String.class);
		final HttpStatus rsStatus = exchange.getStatusCode();
		final String rsBody = exchange.getBody();
		log.info("\n\n## R E S T   R E S P O N S E ##\nSTATUS: [{}]\n{}\n\n", rsStatus, prettyJson(rsBody));

		if (request == null && rest.getResponse() != null) {
			processRs(rsStatus, rsBody, rest.getResponse());
		} else if (request != null && request.getResponse() != null) {
			processRs(rsStatus, rsBody, request.getResponse());
		} else {
			throw new TestFailException("Invalid XML of <response/> or <request/>");
		}
	}

	private void processRs(HttpStatus rsStatus, String rsBody, RestResponse response) {
		if (rsStatus == response.getStatus()) {
			Map rs;
			if (rsBody.startsWith("{")) {
				rs = jsonAsMap(rsBody);
			} else if (rsBody.startsWith("[")) {
				List list = jsonAsList(rsBody);
				rs = new HashMap();
				rs.put("list", list);
			} else {
				throw new RuntimeException("Invalid JSON response");
			}

			response.getAsserts().forEach(restAssert -> evalAssert(rs, restAssert));
		} else if (!response.getIgnoreOthers()) {
			throw new TestFailException("Invalid status code", response.getStatus(), rsStatus);
		}
	}

	private void evalAssert(Map response, RestAssert anAssert) {
		Object rsValue = evaluate(response, anAssert.getPath());
		if (rsValue != null) {
			if (!anAssert.getType().getJavaType().isAssignableFrom(rsValue.getClass())) {
				throw new TestFailException(String.format("Invalid assert type for [%s]", anAssert.getPath()), anAssert.getType().getJavaType(), rsValue.getClass());
			}

			// TODO check value
			if (anAssert.getValue() != null) {
				Object expectedValue = null;
				switch (anAssert.getType()) {
					case NONE:
					case LIST:
						break;

					case STRING:
						expectedValue = anAssert.getValue();
						break;

					case INTEGER:
						rsValue = Long.valueOf(rsValue.toString());
						expectedValue = Long.valueOf(anAssert.getValue());
						break;

					case REAL:
						rsValue = new BigDecimal(rsValue.toString());
						expectedValue = new BigDecimal(anAssert.getValue());
						break;

					case BOOLEAN:
						expectedValue = Boolean.valueOf(anAssert.getValue());
						break;

					default:
						throw new TestFailException("Invalid type: " + anAssert.getType());
				}

				if (expectedValue != null) {
					if (!expectedValue.equals(rsValue)) {
						throw new TestFailException("Invalid Value for path: " + anAssert.getPath(), expectedValue, rsValue);
					}
				}
			}

			if (anAssert.getStoreAsParam() != null) {
				CONTEXT.put(anAssert.getStoreAsParam(), rsValue);
			}
		} else {
			if (anAssert.isHasValue() || anAssert.getStoreAsParam() != null) {
				throw new TestFailException(String.format("Null value for assert [%s]", anAssert.getPath()));
			}
		}
	}

	private void startApp(BootApp app) {
		if (CONTEXT.containsKey(app.getName())) {
			log.warn("BootApp/RemoteService already defined: {}", app.getName());
			return;
		}

		try {
			Map<String, Object> p1 = new HashMap<>();
			if (app.getProfile() != null) {
				p1.put("spring.profiles.active", app.getProfile());
			}

			if (app.getContext() != null) {
				final int port = Util.findRandomOpenPortOnAllLocalInterfaces();
				p1.put("server.port", port);
				p1.put("server.servlet.context-path", app.getContext());
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

	private void processService(RemoteService service) {
		AppInfo info = new AppInfo(service.getHost(), service.getPort(), service.getContext());
		if (!CONTEXT.containsKey(service.getName())) {
			CONTEXT.put(service.getName(), info);
		} else {
			log.warn("BootApp/RemoteService already defined: {}", service.getName());
		}
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

	@RequiredArgsConstructor
	private static class AppInfo {
		private final String host;
		private final int port;
		private final String context;
	}
}
