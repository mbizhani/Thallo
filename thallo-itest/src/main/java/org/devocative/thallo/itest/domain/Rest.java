package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@XStreamAlias("rest")
public class Rest {

	@XStreamAsAttribute
	private String url;

	@XStreamAsAttribute
	private String service;

	@XStreamAsAttribute
	private String uri;

	@XStreamAsAttribute
	private HttpMethod method;

	@XStreamAsAttribute
	private String bearerToken;

	private RestResponse response;

	@XStreamImplicit
	private List<RestRequest> requests;

	// ------------------------------

	public List<RestRequest> getRequests() {
		return requests != null ? requests : Collections.emptyList();
	}
}
