package org.devocative.thallo.alog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.devocative.thallo.alog.annotation.ELogMode;
import org.devocative.thallo.alog.annotation.ELogPlace;
import org.devocative.thallo.alog.annotation.EStackTraceLogType;
import org.devocative.thallo.alog.annotation.LogIt;
import org.devocative.thallo.common.StackTraceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
public class MethodLogAspect {
	private static final Logger log = LoggerFactory.getLogger(MethodLogAspect.class);

	// ------------------------------

	private final MethodLogProperties properties;

	public MethodLogAspect(MethodLogProperties properties) {
		this.properties = properties;

		log.info("* Thallo MethodLogAspect Initiated");
	}

	// ------------------------------

	@Around("@within(org.springframework.stereotype.Service) || @within(org.devocative.thallo.alog.annotation.LogIt) || @annotation(org.devocative.thallo.alog.annotation.LogIt)")
	public Object around(ProceedingJoinPoint jp) throws Throwable {
		// Help: https://stackoverflow.com/questions/5714411/getting-the-java-lang-reflect-method-from-a-proceedingjoinpoint
		final MethodSignature sig = (MethodSignature) jp.getSignature();
		final Method method = sig.getMethod();
		final Class<?> declaringType = sig.getDeclaringType();

		if (!properties.getService().getEnabled() && declaringType.isAnnotationPresent(Service.class) &&
			!declaringType.isAnnotationPresent(LogIt.class) && !method.isAnnotationPresent(LogIt.class)) {
			return jp.proceed();
		}

		final LogItWrapper wrapper = new LogItWrapper(
			method.isAnnotationPresent(LogIt.class) ?
				method.getAnnotation(LogIt.class) :
				declaringType.getAnnotation(LogIt.class));

		final Object[] args = jp.getArgs();

		if (wrapper.mode() != ELogMode.Disabled && wrapper.place() != ELogPlace.End) {
			StringBuilder builder = new StringBuilder();
			builder.append(String.format("{LogIt - {sig: \"%s.%s\"", declaringType.getSimpleName(), sig.getName()));
			if (args.length > 0) {
				builder.append(", args: ").append(wrapper.logParams() ? Arrays.toString(args) : "[***]");
			}
			builder.append("}");

			log.info(builder.toString());
		}

		Object result = null;
		Throwable error = null;
		final long start = System.currentTimeMillis();

		try {

			result = jp.proceed();

		} catch (Throwable throwable) {
			error = throwable;
			throw throwable;

		} finally {
			final long dur = System.currentTimeMillis() - start;

			if (wrapper.mode() != ELogMode.Disabled && wrapper.place() != ELogPlace.Start) {
				StringBuilder builder = new StringBuilder();
				builder.append(String.format("}LogIt - {sig: \"%s.%s\"", declaringType.getSimpleName(), sig.getName()));

				if (args.length > 0) {
					builder.append(", args: ").append(wrapper.logParams() ? Arrays.toString(args) : "[***]");
				}

				if (error == null && !method.getReturnType().equals(Void.TYPE)) {
					builder.append(", result: \"").append(wrapper.logResult() ? result : "***").append("\"");
				}

				builder.append(", dur: ").append(dur);

				if (error != null && wrapper.mode() == ELogMode.All) {
					builder.append(", err: \"").append(error).append("\"}");

					switch (wrapper.stacktrace()) {
						case None:
							log.error(builder.toString());
							break;

						case Filtered:
							builder
								.append("\n")
								.append(StackTraceProcessor.filter(error, declaringType));
							log.error(builder.toString());
							break;

						case All:
							log.error(builder.toString(), error);
							break;
					}
				} else {
					builder.append("}");
					log.info(builder.toString());
				}
			}
		}

		return result;
	}

	// ------------------------------

	private class LogItWrapper {
		private final LogIt logIt;

		// ---------------

		LogItWrapper(LogIt logIt) {
			this.logIt = logIt;
		}

		// ---------------

		ELogMode mode() {
			return properties.getMode() != null ? properties.getMode() :
				logIt != null ? logIt.mode() : ELogMode.All;
		}

		boolean logParams() {
			return properties.getLogParams() != null ? properties.getLogParams() :
				logIt == null || logIt.logParams();
		}

		boolean logResult() {
			return properties.getLogResult() != null ? properties.getLogResult() :
				logIt == null || logIt.logResult();
		}

		EStackTraceLogType stacktrace() {
			return properties.getStacktrace() != null ? properties.getStacktrace() :
				logIt != null ? logIt.stacktrace() : EStackTraceLogType.Filtered;
		}

		ELogPlace place() {
			return properties.getPlace() != null ? properties.getPlace() :
				logIt != null ? logIt.place() : ELogPlace.End;
		}
	}
}
