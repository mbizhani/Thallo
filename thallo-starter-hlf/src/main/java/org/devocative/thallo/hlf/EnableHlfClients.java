package org.devocative.thallo.hlf;

import org.devocative.thallo.hlf.config.HlfClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({HlfClientsRegistrar.class})
public @interface EnableHlfClients {
	String[] value() default {};

	String[] basePackages() default {};

	Class<?>[] basePackageClasses() default {};
}
