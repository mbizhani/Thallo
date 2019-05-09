package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("assert")
public class RestAssert {
	@XStreamAsAttribute
	private String path;

	@XStreamAsAttribute
	private EFieldType type = EFieldType.NONE;

	@XStreamAsAttribute
	private boolean hasValue = false;

	@XStreamAsAttribute
	private String storeAsParam;

	@XStreamAsAttribute
	private String value;
}
