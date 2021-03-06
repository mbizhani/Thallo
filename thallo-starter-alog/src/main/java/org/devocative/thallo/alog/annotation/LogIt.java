package org.devocative.thallo.alog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogIt {
	ELogMode mode() default ELogMode.All;

	boolean logParams() default true;

	boolean logResult() default true;

	EStackTraceLogType stacktrace() default EStackTraceLogType.Filtered;

	ELogPlace place() default ELogPlace.End;
}
