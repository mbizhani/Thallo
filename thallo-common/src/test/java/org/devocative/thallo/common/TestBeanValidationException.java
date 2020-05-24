package org.devocative.thallo.common;

import org.devocative.thallo.common.app.PersonService;
import org.devocative.thallo.common.app.dto.PersonDTO;
import org.devocative.thallo.common.app.dto.UserDTO;
import org.devocative.thallo.common.dto.ConstraintViolationDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.devocative.thallo.common.EConstraintViolationType.*;
import static org.devocative.thallo.common.dto.ConstraintViolationDTO.of;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestBeanValidationException {

	@Autowired
	private PersonService personService;

	@Test
	public void testConstraints() {
		try {
			personService.create(new PersonDTO());
			fail("Expecting ConstraintViolationException");
		} catch (ConstraintViolationException e) {
			final List<ConstraintViolationDTO> constraints = ExceptionUtil.findConstraints(e);

			assertTrue(constraints.remove(of(NotBlank, "name")));
			assertTrue(constraints.remove(of(NotNull, "user")));

			assertEquals(0, constraints.size());
		}


		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 2);
		final Date wrongBirthDate = cal.getTime();

		Stream.of(-10, 0, 1, 9).forEach(wrongLowWeight -> {
			try {
				personService.create(new PersonDTO()
					.setWeight(wrongLowWeight)
					.setBirthDate(wrongBirthDate)
				);
				fail("Expecting ConstraintViolationException");
			} catch (ConstraintViolationException e) {
				final List<ConstraintViolationDTO> constraints = ExceptionUtil.findConstraints(e);

				final ConstraintViolationDTO expectedWeight = of(Min, "weight");
				final ConstraintViolationDTO actualWeight = constraints.get(constraints.indexOf(expectedWeight));
				assertEquals(PersonDTO.WEIGHT_MIN, actualWeight.getCriteria().get(MIN_ATTR));

				assertTrue(constraints.remove(expectedWeight));
				assertTrue(constraints.remove(of(Past, "birthDate")));

				assertTrue(constraints.remove(of(NotBlank, "name")));
				assertTrue(constraints.remove(of(NotNull, "user")));

				assertEquals(0, constraints.size());
			}

		});

		Stream.of(401, 1000).forEach(wrongHighWeight -> {
			try {
				personService.create(new PersonDTO()
					.setWeight(wrongHighWeight)
					.setBirthDate(wrongBirthDate)
				);
				fail("Expecting ConstraintViolationException");
			} catch (ConstraintViolationException e) {
				final List<ConstraintViolationDTO> constraints = ExceptionUtil.findConstraints(e);

				final ConstraintViolationDTO expectedWeight = of(Max, "weight");
				final ConstraintViolationDTO actualWeight = constraints.get(constraints.indexOf(expectedWeight));
				assertEquals(PersonDTO.WEIGHT_MAX, actualWeight.getCriteria().get(MAX_ATTR));

				assertTrue(constraints.remove(expectedWeight));
				assertTrue(constraints.remove(of(Past, "birthDate")));

				assertTrue(constraints.remove(of(NotBlank, "name")));
				assertTrue(constraints.remove(of(NotNull, "user")));

				assertEquals(0, constraints.size());
			}
		});


		cal.add(Calendar.DATE, -2);
		final Date correctBirthDate = cal.getTime();

		try {
			personService.create(new PersonDTO()
				.setBirthDate(correctBirthDate)
				.setName("A")
				.setWeight(100)
				.setUser(new UserDTO())
			);
			fail("Expecting ConstraintViolationException");
		} catch (ConstraintViolationException e) {
			final List<ConstraintViolationDTO> constraints = ExceptionUtil.findConstraints(e);

			assertTrue(constraints.remove(of(NotBlank, "user.username")));
			assertTrue(constraints.remove(of(NotEmpty, "user.roles")));

			assertEquals(0, constraints.size());
		}

		try {
			personService.create(new PersonDTO()
				.setBirthDate(correctBirthDate)
				.setName("A")
				.setWeight(100)
				.setUser(new UserDTO()
					.setUsername("tes!")
					.setRoles(Collections.emptyList())
					.setExpiration(new Date())
				)
			);
			fail("Expecting ConstraintViolationException");
		} catch (ConstraintViolationException e) {
			final List<ConstraintViolationDTO> constraints = ExceptionUtil.findConstraints(e);

			final ConstraintViolationDTO expectedUsernameSize = of(Size, "user.username");
			final ConstraintViolationDTO actualUsernameSize = constraints.get(constraints.indexOf(expectedUsernameSize));
			assertEquals(UserDTO.USERNAME_MIN_SIZE, actualUsernameSize.getCriteria().get(SIZE_MIN_ATTR));
			assertEquals(UserDTO.USERNAME_MAX_SIZE, actualUsernameSize.getCriteria().get(SIZE_MAX_ATTR));

			assertTrue(constraints.remove(expectedUsernameSize));
			assertTrue(constraints.remove(of(Pattern, "user.username")));
			assertTrue(constraints.remove(of(NotEmpty, "user.roles")));
			assertTrue(constraints.remove(of(Future, "user.expiration")));

			assertEquals(0, constraints.size());
		}

	}
}
