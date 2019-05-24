package org.devocative.thallo.itest.domain.service;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;
import org.devocative.thallo.itest.domain.AbstractService;

@Getter
@Setter
public abstract class AbstractBoot extends AbstractService {

	@XStreamAsAttribute
	private String context;

	@XStreamAsAttribute
	private String profile;

	@XStreamAsAttribute
	private Integer port;
}
