package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@XStreamAlias("rest")
public class Rest {
	@XStreamAsAttribute
	private String app;

	@XStreamAsAttribute
	private String uri;

	@XStreamAsAttribute
	private HttpMethod method;

	private String body;

	private RestResponse response;
}
