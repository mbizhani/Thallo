package org.devocative.thallo.itest.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EFieldType {
	NONE(Object.class), STRING(CharSequence.class), INTEGER(Number.class), REAL(Number.class);

	private final Class<?> javaType;
}
