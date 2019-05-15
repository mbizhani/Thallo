package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("bootApp")
public class BootApp extends AbstractService {
	@XStreamAsAttribute
	private String fqn;

	@XStreamAsAttribute
	private String profile;
}
