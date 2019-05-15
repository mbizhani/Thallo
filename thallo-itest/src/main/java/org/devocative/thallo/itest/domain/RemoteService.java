package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("remote")
public class RemoteService extends AbstractService {
	@XStreamAsAttribute
	private String host;

	@XStreamAsAttribute
	private Integer port;
}
