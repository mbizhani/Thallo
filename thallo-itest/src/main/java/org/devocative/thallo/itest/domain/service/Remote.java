package org.devocative.thallo.itest.domain.service;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;
import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.domain.AbstractService;
import org.devocative.thallo.itest.iservice.RemoteService;

@Getter
@Setter
@XStreamAlias("remote")
public class Remote extends AbstractService {
	@XStreamAsAttribute
	private String host;

	@XStreamAsAttribute
	private Integer port;

	@XStreamAsAttribute
	private String context;

	// ------------------------------

	@Override
	public IService<Remote> create() {
		return new RemoteService();
	}
}
