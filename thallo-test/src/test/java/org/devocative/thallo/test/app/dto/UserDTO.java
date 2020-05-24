package org.devocative.thallo.test.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class UserDTO {
	public static final int USERNAME_MIN_SIZE = 5;
	public static final int USERNAME_MAX_SIZE = 10;

	@NotBlank
	@Size(min = USERNAME_MIN_SIZE, max = USERNAME_MAX_SIZE)
	@Pattern(regexp = "^[A-Za-z]+[_A-Za-z0-9\\-.]*", message = "InvalidUsernameFormat")
	private String username;

	@NotEmpty
	private List<String> roles;

	@Future
	private Date expiration;
}
