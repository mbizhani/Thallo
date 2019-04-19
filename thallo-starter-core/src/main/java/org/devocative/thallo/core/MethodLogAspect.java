package org.devocative.thallo.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.devocative.thallo.core.annotation.LogIt;
import org.devocative.thallo.core.annotation.StackTraceLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
public class MethodLogAspect {
	private static final Logger log = LoggerFactory.getLogger(MethodLogAspect.class);

	// ------------------------------

	public MethodLogAspect() {
		log.info("* Thallo MethodLogAspect InitiatedInitiated");
	}

	// ------------------------------

	@Around("@within(org.devocative.thallo.core.annotation.LogIt) || @annotation(org.devocative.thallo.core.annotation.LogIt) || false")
	public Object around(ProceedingJoinPoint jp) throws Throwable {
		Object[] args = jp.getArgs();

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

			// Help: https://stackoverflow.com/questions/5714411/getting-the-java-lang-reflect-method-from-a-proceedingjoinpoint
			final MethodSignature sig = (MethodSignature) jp.getSignature();
			final Method method = sig.getMethod();
			final Class<?> declaringType = sig.getDeclaringType();

			final LogIt logIt = method.isAnnotationPresent(LogIt.class) ?
				method.getAnnotation(LogIt.class) :
				declaringType.getAnnotation(LogIt.class);

			final LogItWrapper wrapper = new LogItWrapper(logIt);

			StringBuilder builder = new StringBuilder();
			builder.append(String.format("LogIt - {sig: \"%s.%s\"", declaringType.getSimpleName(), sig.getName()));

			if (args.length > 0) {
				builder.append(", args: ").append(wrapper.logParams() ? Arrays.toString(args) : "[***]");
			}

			if (error == null && !method.getReturnType().equals(Void.TYPE)) {
				builder.append(", result: \"").append(wrapper.logResult() ? result : "***").append("\"");
			}

			builder.append(", dur: ").append(dur);

			if (error != null && wrapper.logException()) {
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

		return result;
	}

	// ------------------------------

	private class LogItWrapper {
		private final LogIt logIt;

		LogItWrapper(LogIt logIt) {
			this.logIt = logIt;
		}

		boolean logParams() {
			return logIt == null || logIt.logParams();
		}

		boolean logResult() {
			return logIt == null || logIt.logResult();
		}

		boolean logException() {
			return logIt == null || logIt.logException();
		}

		StackTraceLogType stacktrace() {
			return logIt != null ? logIt.stacktrace() : StackTraceLogType.Filtered;
		}
	}
}
