package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("bootApp")
public class BootApp {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String fqn;

	@XStreamAsAttribute
	private String profile;

	@XStreamAsAttribute
	private String context;
}
