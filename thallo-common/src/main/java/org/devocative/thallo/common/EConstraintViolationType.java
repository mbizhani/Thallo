package org.devocative.thallo.common;

import java.lang.annotation.Annotation;

public enum EConstraintViolationType {
	Future(javax.validation.constraints.Future.class),
	Max(javax.validation.constraints.Max.class,
		EConstraintViolationType.MAX_ATTR),
	Min(javax.validation.constraints.Min.class,
		EConstraintViolationType.MIN_ATTR),
	NotBlank(javax.validation.constraints.NotBlank.class),
	NotEmpty(javax.validation.constraints.NotEmpty.class),
	NotNull(javax.validation.constraints.NotNull.class),
	Past(javax.validation.constraints.Past.class),
	Pattern(javax.validation.constraints.Pattern.class),
	Size(javax.validation.constraints.Size.class,
		EConstraintViolationType.SIZE_MAX_ATTR,
		EConstraintViolationType.SIZE_MIN_ATTR);

	// ------------------------------

	public static final String MAX_ATTR = "value";
	public static final String MIN_ATTR = "value";
	public static final String SIZE_MAX_ATTR = "max";
	public static final String SIZE_MIN_ATTR = "min";

	private final Class<? extends Annotation> annotationClass;
	private final String[] criteria;

	// ------------------------------

	EConstraintViolationType(Class<? extends Annotation> annotationClass, String... criteria) {
		this.annotationClass = annotationClass;
		this.criteria = criteria;
	}

	// ---------------

	public String[] getCriteria() {
		return criteria;
	}

	// ------------------------------

	public static EConstraintViolationType findByConstraint(Class<? extends Annotation> cls) {
		for (EConstraintViolationType value : values()) {
			if (value.annotationClass.equals(cls)) {
				return value;
			}
		}

		throw new RuntimeException("Invalid Annotation Class: " + cls.getName());
	}
}
