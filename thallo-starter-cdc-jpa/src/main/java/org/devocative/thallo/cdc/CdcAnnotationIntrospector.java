package org.devocative.thallo.cdc;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.devocative.thallo.cdc.annotation.CdcData;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.Version;
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
		CdcData cdcData = _findAnnotation(a, CdcData.class);
		Id id = _findAnnotation(a, Id.class);
		Version version = _findAnnotation(a, Version.class);

		Embeddable embeddableType = null;

		final AnnotatedElement element = a.getAnnotated();
		if (element instanceof Field) {
			Field field = (Field) element;
			final Class<?> clsOwnerOfField = field.getDeclaringClass();

			if (cdcData == null) {
				cdcData = clsOwnerOfField.getAnnotation(CdcData.class);
			}
			embeddableType = clsOwnerOfField.getAnnotation(Embeddable.class);
		}

		return (cdcData != null && cdcData.value()) ||
			(cdcData == null && (id != null || version != null || embeddableType != null));
	}
}
