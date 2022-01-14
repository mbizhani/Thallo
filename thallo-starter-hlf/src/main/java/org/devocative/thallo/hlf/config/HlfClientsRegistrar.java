package org.devocative.thallo.hlf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.devocative.thallo.hlf.EnableHlfClients;
import org.devocative.thallo.hlf.HlfClient;
import org.devocative.thallo.hlf.iservice.IHlfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HlfClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware, BeanFactoryAware {
	private static final Logger log = LoggerFactory.getLogger(HlfClientsRegistrar.class);

	private ResourceLoader resourceLoader;
	private Environment environment;
	private BeanFactory beanFactory;

	// ------------------------------

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	// ---------------

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		log.info("*** HlfClientsRegistrar.registerBeanDefinitions ***");

		final Set<String> basePackages = getBasePackages(metadata);
		log.info("HlfClientsRegistrar - basePackages: {}", basePackages);

		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false, environment) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				final AnnotationMetadata metadata = beanDefinition.getMetadata();
				return metadata.isIndependent() && metadata.isInterface();
			}
		};
		provider.setResourceLoader(resourceLoader);
		provider.addIncludeFilter(new AnnotationTypeFilter(HlfClient.class));

		final ObjectMapper objectMapper = new ObjectMapper();

		try {
			for (String basePackage : basePackages) {
				log.info("HlfClientsRegistrar - process basePackage: {}", basePackage);

				for (BeanDefinition beanDefinition : provider.findCandidateComponents(basePackage)) {
					final Class<?> clientInterfaceClass = Class.forName(beanDefinition.getBeanClassName());
					log.info("HlfClientsRegistrar - Hlf Client: {}", clientInterfaceClass.getName());

					final GenericBeanDefinition gbd = new GenericBeanDefinition();
					gbd.setBeanClass(clientInterfaceClass);
					gbd.setInstanceSupplier(() -> {
						final IHlfService calendarService = beanFactory.getBean(IHlfService.class);

						return Proxy.newProxyInstance(
							getClass().getClassLoader(),
							new Class[]{clientInterfaceClass},
							new HlfClientMethodHandler(clientInterfaceClass, calendarService, objectMapper));
					});
					gbd.setLazyInit(true);
					registry.registerBeanDefinition(clientInterfaceClass.getName(), gbd);
				}
			}

			//TODO: throws exception if there is no HlfClient
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	// ------------------------------

	protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
		final Set<String> basePackages = new HashSet<>();

		final Map<String, Object> attributes = importingClassMetadata
			.getAnnotationAttributes(EnableHlfClients.class.getCanonicalName());

		for (String pkg : (String[]) attributes.get("value")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (String pkg : (String[]) attributes.get("basePackages")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}

		if (basePackages.isEmpty()) {
			basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}

		return basePackages;
	}

}
