package org.devocative.thallo.cdc;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.devocative.thallo.cdc.annotation.CdcSource;
import org.devocative.thallo.cdc.annotation.CdcSourceData;

import javax.persistence.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

public class CdcAnnotationIntrospector extends JacksonAnnotationIntrospector {
	@Override
	public PropertyName findNameForSerialization(Annotated a) {
		if (doIt(a)) {
			return PropertyName.USE_DEFAULT;
		}
		return null;
	}

	@Override
	public boolean hasIgnoreMarker(AnnotatedMember m) {
		return !doIt(m);
	}

	// ------------------------------

	private boolean doIt(Annotated a) {
		CdcSource cdcSource = null;
		CdcSourceData cdcSourceData = null;

		Embeddable embeddableType = null;

		Id id = _findAnnotation(a, Id.class);
		Version version = _findAnnotation(a, Version.class);
		OneToMany oneToMany = _findAnnotation(a, OneToMany.class);
		ManyToMany manyToMany = _findAnnotation(a, ManyToMany.class);
		OneToOne oneToOne = _findAnnotation(a, OneToOne.class);

		final AnnotatedElement element = a.getAnnotated();
		if (element instanceof Field) {
			Field field = (Field) element;
			final Class<?> clsOwnerOfField = field.getDeclaringClass();

			cdcSource = clsOwnerOfField.getAnnotation(CdcSource.class);
			cdcSourceData = clsOwnerOfField.getAnnotation(CdcSourceData.class);

			if (cdcSource != null && cdcSourceData != null && !cdcSourceData.value()) {
				throw new CdcException("Invalid Entity for CDC: @CdcSource and @CdcSourceData(false) over " + clsOwnerOfField.getName());
			}

			embeddableType = clsOwnerOfField.getAnnotation(Embeddable.class);
		}

		if (a.hasAnnotation(CdcSourceData.class)) {
			cdcSourceData = _findAnnotation(a, CdcSourceData.class);
		}

		return (cdcSourceData == null || cdcSourceData.value()) &&
			(cdcSourceData != null || cdcSource != null || id != null || version != null || embeddableType != null) &&
			(oneToMany == null || oneToMany.mappedBy() == null) &&
			(manyToMany == null || manyToMany.mappedBy() == null) &&
			(oneToOne == null || oneToOne.mappedBy() == null);
	}
}
