package org.devocative.thallo.common.dto;

import org.devocative.thallo.common.EConstraintViolationType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConstraintViolationDTO {
	private final EConstraintViolationType type;
	private final String field;
	private final String message;

	private Map<String, Object> criteria = new HashMap<>();

	// ------------------------------

	public ConstraintViolationDTO(EConstraintViolationType type, String field, String message) {
		this.type = type;
		this.field = field;
		this.message = message;
	}

	public EConstraintViolationType getType() {
		return type;
	}

	public String getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, Object> getCriteria() {
		return criteria;
	}

	public ConstraintViolationDTO addCriterion(String criterion, Object value) {
		criteria.put(criterion, value);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConstraintViolationDTO that = (ConstraintViolationDTO) o;
		return type == that.type &&
			Objects.equals(field, that.field) &&
			(message == null || that.message == null || Objects.equals(message, that.message));
	}

	@Override
	public String toString() {
		return "ConstraintViolationDTO{" +
			"type=" + type +
			", field='" + field + '\'' +
			", message='" + message + '\'' +
			(criteria.isEmpty() ? "" : ", criteria=" + criteria) +
			'}';
	}

	// ------------------------------

	public static ConstraintViolationDTO of(EConstraintViolationType type, String field) {
		return of(type, field, null);
	}

	public static ConstraintViolationDTO of(EConstraintViolationType type, String field, String message) {
		return new ConstraintViolationDTO(type, field, message);
	}

}
