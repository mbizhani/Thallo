package org.devocative.thallo.itest.domain.service;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.domain.AbstractService;
import org.devocative.thallo.itest.iservice.RedisService;

@Getter
@Setter
@XStreamAlias("redis")
public class Redis extends AbstractService {
	private Boolean defaultPort;

	// ------------------------------

	@Override
	public String getName() {
		return "Redis";
	}

	@Override
	public IService<Redis> create() {
		RedisService service = new RedisService();
		service.init(this);
		return service;
	}
}
