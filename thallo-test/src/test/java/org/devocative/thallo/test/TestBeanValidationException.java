package org.devocative.thallo.test;

import org.devocative.thallo.common.dto.ConstraintViolationDTO.Criterion;
import org.devocative.thallo.test.app.PersonService;
import org.devocative.thallo.test.app.dto.PersonDTO;
import org.devocative.thallo.test.app.dto.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

import static org.devocative.thallo.common.EConstraintViolationType.*;
import static org.devocative.thallo.common.dto.ConstraintViolationDTO.of;
import static org.devocative.thallo.test.TAssert.assertBeanValidationViolation;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestBeanValidationException {

	@Autowired
	private PersonService personService;

	@Test
	public void testConstraints() {

		assertBeanValidationViolation(() -> personService.create(new PersonDTO()),
			of(NotBlank, "name"),
			of(NotNull, "user"));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 2);
		final Date wrongBirthDate = cal.getTime();

		Stream.of(-10, 0, 1, 9).forEach(wrongLowWeight -> {
			assertBeanValidationViolation(() ->
					personService.create(new PersonDTO()
						.setWeight(wrongLowWeight)
						.setBirthDate(wrongBirthDate)
					),
				of(Min, "weight", Criterion.of(MIN_ATTR, PersonDTO.WEIGHT_MIN)),
				of(Past, "birthDate"),
				of(NotBlank, "name"),
				of(NotNull, "user")
			);
		});

		Stream.of(401, 1000).forEach(wrongHighWeight -> {
			assertBeanValidationViolation(() ->
					personService.create(new PersonDTO()
						.setWeight(wrongHighWeight)
						.setBirthDate(wrongBirthDate)
					),
				of(Max, "weight", Criterion.of(MAX_ATTR, PersonDTO.WEIGHT_MAX)),
				of(Past, "birthDate"),
				of(NotBlank, "name"),
				of(NotNull, "user")
			);
		});


		cal.add(Calendar.DATE, -2);
		final Date correctBirthDate = cal.getTime();

		assertBeanValidationViolation(() ->
				personService.create(new PersonDTO()
					.setBirthDate(correctBirthDate)
					.setName("A")
					.setWeight(100)
					.setUser(new UserDTO())
				),
			of(NotBlank, "user.username"),
			of(NotEmpty, "user.roles")
		);

		assertBeanValidationViolation(() ->
				personService.create(new PersonDTO()
					.setBirthDate(correctBirthDate)
					.setName("A")
					.setWeight(100)
					.setUser(new UserDTO()
						.setUsername("tes!")
						.setRoles(Collections.emptyList())
						.setExpiration(new Date())
					)
				),
			of(Pattern, "user.username"),
			of(Size, "user.username",
				Criterion.of(SIZE_MAX_ATTR, UserDTO.USERNAME_MAX_SIZE),
				Criterion.of(SIZE_MIN_ATTR, UserDTO.USERNAME_MIN_SIZE)),
			of(NotEmpty, "user.roles"),
			of(Future, "user.expiration")
		);
	}
}
