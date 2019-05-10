package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@XStreamAlias("response")
public class RestResponse {
	@XStreamAsAttribute
	private HttpStatus status;

	@XStreamAsAttribute
	private Boolean ignoreOthers;

	@XStreamImplicit
	private List<RestAssert> asserts;

	// ------------------------------

	public List<RestAssert> getAsserts() {
		return asserts != null ? asserts : Collections.emptyList();
	}

	public Boolean getIgnoreOthers() {
		return ignoreOthers != null ? ignoreOthers : false;
	}
}
