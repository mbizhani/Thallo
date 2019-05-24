package org.devocative.thallo.itest.domain.service;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;
import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.domain.Param;
import org.devocative.thallo.itest.iservice.BootAppService;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@XStreamAlias("bootApp")
public class BootApp extends AbstractBoot {
	@XStreamAsAttribute
	private String groupId;

	@XStreamAsAttribute
	private String artifactId;

	@XStreamAsAttribute
	private String version;

	private List<Param> params;

	// ------------------------------

	public List<Param> getParams() {
		return params != null ? params : Collections.emptyList();
	}

	@Override
	public IService<BootApp> create() {
		BootAppService service = new BootAppService();
		service.init(this);
		return service;
	}
}
