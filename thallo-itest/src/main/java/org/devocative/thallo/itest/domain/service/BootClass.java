package org.devocative.thallo.itest.domain.service;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;
import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.iservice.BootClassService;

@Getter
@Setter
@XStreamAlias("bootClass")
public class BootClass extends AbstractBoot {
	@XStreamAsAttribute
	private String fqn;

	// ------------------------------

	@Override
	public IService<BootClass> create() {
		BootClassService service = new BootClassService();
		service.init(this);
		return service;
	}
}
