package org.devocative.thallo.itest.iservice;

import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.ServiceInfo;
import org.devocative.thallo.itest.Util;
import org.devocative.thallo.itest.domain.Param;
import org.devocative.thallo.itest.domain.service.BootApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BootAppService implements IService<BootApp> {
	private static final Logger log = LoggerFactory.getLogger(BootAppService.class);

	private BootApp app;
	private BootAppProcess appProcess;

	@Override
	public void init(BootApp service) {
		app = service;
	}

	@Override
	public Optional<ServiceInfo> start() {
		ServiceInfo result = null;
		try {
			String $HOME = System.getProperty("user.home");
			String jarFile = String.format("%s/.m2/repository/%s/%s/%s/%s-%s.jar",
				$HOME,
				app.getGroupId().replace(".", "/"),
				app.getArtifactId(),
				app.getVersion(),
				app.getArtifactId(),
				app.getVersion()
			);
			log.info("BootApp JAR [{}]: {}", app.getName(), jarFile);

			if (!Files.exists(Paths.get(jarFile))) {
				throw new RuntimeException(String.format("Jar not found, app[%s]: %s", app.getName(), jarFile));
			}

			List<Param> params = new ArrayList<>();
			if (app.getProfile() != null) {
				params.add(new Param(PARAM_PROFILE, app.getProfile()));
			}
			if (app.getContext() != null || app.getPort() != null) { //TODO
				final Integer port = app.getPort() != null ? app.getPort() :
					Util.findRandomOpenPortOnAllLocalInterfaces();
				params.add(new Param(PARAM_PORT, port.toString()));
				params.add(new Param(PARAM_CONTEXT, app.getContext()));
				result = new ServiceInfo("localhost", port, app.getContext());
			}
			for (Map.Entry<String, String> entry : ENV.entrySet()) {
				params.add(new Param(entry.getKey(), entry.getValue()));
			}
			params.addAll(app.getParams());

			List<String> command = new ArrayList<>();
			command.add("java");
			params.forEach(param -> command.add(String.format("-D%s=%s", param.getName(), param.getValue())));
			command.add("-jar");
			command.add(jarFile);

			log.info("Starting BootApp [{}]:\n\t{}", app.getName(), command);
			appProcess = new BootAppProcess(app.getName(), command);
			appProcess.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return Optional.ofNullable(result);
	}

	@Override
	public void stop() {
		appProcess.stopApp();
	}

	// ------------------------------

	private static class BootAppProcess extends Thread {
		private static final Logger log = LoggerFactory.getLogger(BootAppProcess.class);

		private final String name;
		private final List<String> command;
		private Process process;

		public BootAppProcess(String name, List<String> command) {
			this.name = name;
			this.command = command;

			setName("BootApp-" + name);
		}

		@Override
		public void run() {
			try {
				ProcessBuilder builder = new ProcessBuilder(command);
				builder.redirectOutput(new File(name + "-out.txt"));
				builder.redirectError(new File(name + "-err.txt"));
				process = builder.start();
				process.waitFor();
				log.info("## BootApp EXITED [{}]: code=[{}]", name, process.exitValue());
			} catch (Exception e) {
				log.error("BootApp Error [{}]", name, e);
			}
		}

		public void stopApp() {
			process.destroy();
		}
	}
}
