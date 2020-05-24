package org.devocative.thallo.test.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
public class PersonDTO {
	public static final long WEIGHT_MIN = 10;
	public static final long WEIGHT_MAX = 400;

	@NotBlank
	@Size(max = 5)
	private String name;

	@Min(WEIGHT_MIN)
	@Max(WEIGHT_MAX)
	private Integer weight;

	@Past
	private Date birthDate;

	@NotNull
	@Valid
	private UserDTO user;
}
