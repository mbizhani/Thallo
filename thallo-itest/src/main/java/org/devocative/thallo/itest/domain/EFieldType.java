package org.devocative.thallo.itest.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum EFieldType {
	NONE(Object.class),
	STRING(CharSequence.class),
	INTEGER(Number.class),
	REAL(Number.class),
	BOOLEAN(Boolean.class),
	LIST(List.class);

	private final Class<?> javaType;
}
