package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("service")
public class Service {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String host;

	@XStreamAsAttribute
	private Integer port;

	@XStreamAsAttribute
	private String context;
}
