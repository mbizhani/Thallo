package org.devocative.thallo.cdc.annotation;

import org.devocative.thallo.cdc.CdcTargetHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CdcTarget {
	boolean create() default true;

	boolean update() default true;

	boolean delete() default true;

	Class<? extends CdcTargetHandler> handler() default CdcTargetHandler.DefaultHandler.class;
}
