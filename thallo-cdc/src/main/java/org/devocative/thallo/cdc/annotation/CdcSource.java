package org.devocative.thallo.cdc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CdcSource {
	boolean create() default true;

	boolean update() default true;

	boolean delete() default true;
}
