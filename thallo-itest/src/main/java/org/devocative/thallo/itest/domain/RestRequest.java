package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("request")
public class RestRequest {
	private String body;

	private RestResponse response;
}
