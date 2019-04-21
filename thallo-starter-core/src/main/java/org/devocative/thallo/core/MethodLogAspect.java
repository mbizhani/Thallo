package org.devocative.thallo.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.devocative.thallo.core.annotation.ELogMode;
import org.devocative.thallo.core.annotation.ELogPlace;
import org.devocative.thallo.core.annotation.EStackTraceLogType;
import org.devocative.thallo.core.annotation.LogIt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
public class MethodLogAspect {
	private static final Logger log = LoggerFactory.getLogger(MethodLogAspect.class);

	// ------------------------------

	public MethodLogAspect() {
		log.info("* Thallo MethodLogAspect Initiated");
	}

	// ------------------------------

	@Around("@within(org.devocative.thallo.core.annotation.LogIt) || @annotation(org.devocative.thallo.core.annotation.LogIt)")
	public Object around(ProceedingJoinPoint jp) throws Throwable {
		// Help: https://stackoverflow.com/questions/5714411/getting-the-java-lang-reflect-method-from-a-proceedingjoinpoint
		final MethodSignature sig = (MethodSignature) jp.getSignature();
		final Method method = sig.getMethod();
		final Class<?> declaringType = sig.getDeclaringType();

		final LogItWrapper wrapper = new LogItWrapper(
			method.isAnnotationPresent(LogIt.class) ?
				method.getAnnotation(LogIt.class) :
				declaringType.getAnnotation(LogIt.class));

		final Object[] args = jp.getArgs();

		if (wrapper.value() != ELogMode.Disabled && wrapper.place() != ELogPlace.End) {
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

			if (wrapper.value() != ELogMode.Disabled && wrapper.place() != ELogPlace.Start) {
				StringBuilder builder = new StringBuilder();
				builder.append(String.format("}LogIt - {sig: \"%s.%s\"", declaringType.getSimpleName(), sig.getName()));

				if (args.length > 0) {
					builder.append(", args: ").append(wrapper.logParams() ? Arrays.toString(args) : "[***]");
				}

				if (error == null && !method.getReturnType().equals(Void.TYPE)) {
					builder.append(", result: \"").append(wrapper.logResult() ? result : "***").append("\"");
				}

				builder.append(", dur: ").append(dur);

				if (error != null && wrapper.value() == ELogMode.All) {
					builder.append(", err: \"").append(error).append("\"}");

					switch (wrapper.stacktrace()) {
						case None:
							log.error(builder.toString());
							break;

						case Filtered:
							builder
								.append("\n")
								.append(new StackTraceProcessor(error, declaringType).process());
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

		ELogMode value() {
			return logIt != null ? logIt.value() : ELogMode.All;
		}

		boolean logParams() {
			return logIt == null || logIt.logParams();
		}

		boolean logResult() {
			return logIt == null || logIt.logResult();
		}

		EStackTraceLogType stacktrace() {
			return logIt != null ? logIt.stacktrace() : EStackTraceLogType.Filtered;
		}

		ELogPlace place() {
			return logIt != null ? logIt.place() : ELogPlace.End;
		}
	}
}
