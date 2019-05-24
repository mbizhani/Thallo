package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("param")
public class Param {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String value;

	// ------------------------------

	public Param() {
	}

	public Param(String name, String value) {
		this.name = name;
		this.value = value;
	}
}
