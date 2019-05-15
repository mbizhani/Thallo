package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractService {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String context;
}
