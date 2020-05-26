package org.devocative.thallo.common;

import org.devocative.thallo.common.dto.ConstraintViolationDTO;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ExceptionUtil {
	private static final String H2_UNIQUE_CONST_SPLITTER = "_index_";

	public static Optional<String> findDbConstraintName(Throwable ex) {
		if (ex instanceof DataIntegrityViolationException) {
			return findDbConstraintName(ex.getCause());
		} else if (ex instanceof ConstraintViolationException) {
			final ConstraintViolationException cve = (ConstraintViolationException) ex;

			String constraintName = cve.getConstraintName();
			if (constraintName != null) {
				constraintName = constraintName.toLowerCase();

				if (constraintName.contains(".")) {
					constraintName = constraintName.split("\\.")[1];

					if (constraintName.contains(H2_UNIQUE_CONST_SPLITTER)) { // FOR H2
						final int idx = constraintName.lastIndexOf(H2_UNIQUE_CONST_SPLITTER);
						constraintName = constraintName.substring(0, idx);
					}
				}

				return Optional.of(constraintName);
			}
			return Optional.empty();
		}

		return Optional.empty();
	}

	public static List<ConstraintViolationDTO> findConstraints(javax.validation.ConstraintViolationException e) {
		return e.getConstraintViolations()
			.stream()
			.map(ExceptionUtil::convertFrom)
			.collect(Collectors.toList());
	}

	public static ConstraintViolationDTO convertFrom(ConstraintViolation<?> violation) {
		final EConstraintViolationType type = EConstraintViolationType.findByConstraint(violation
			.getConstraintDescriptor()
			.getAnnotation()
			.annotationType());

		final String field = StreamSupport
			.stream(violation.getPropertyPath().spliterator(), false)
			.filter(node -> node.getKind() == ElementKind.PROPERTY)
			.map(Path.Node::getName)
			.collect(Collectors.joining("."));

		final String message = violation.getMessage();

		final ConstraintViolationDTO dto = new ConstraintViolationDTO(type, field, message);
		if (type.getCriteria() != null) {
			final Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();
			for (String criterion : type.getCriteria()) {
				if (attributes.containsKey(criterion)) {
					dto.addCriterion(criterion, attributes.get(criterion));
				}
			}
		}
		return dto;
	}
}
