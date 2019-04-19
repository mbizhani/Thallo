package org.devocative.thallo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogIt {
	boolean logParams() default true;

	boolean logResult() default true;

	boolean logException() default true;

	StackTraceLogType stacktrace() default StackTraceLogType.Filtered;
}