package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@XStreamAlias("response")
public class RestResponse {
	@XStreamAsAttribute
	private HttpStatus status;

	private List<RestAssert> asserts;
}
