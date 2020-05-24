package org.devocative.thallo.test.app;

import org.devocative.thallo.test.app.dto.PersonDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface PersonService {
	void create(@Valid PersonDTO personDTO);
}
