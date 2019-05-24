package org.devocative.thallo.itest.domain.service;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.domain.AbstractService;
import org.devocative.thallo.itest.iservice.KafkaService;

@Getter
@Setter
@XStreamAlias("kafka")
public class Kafka extends AbstractService {
	private Boolean defaultPort;

	// ------------------------------

	@Override
	public String getName() {
		return "Kafka";
	}

	@Override
	public IService<Kafka> create() {
		KafkaService service = new KafkaService();
		service.init(this);
		return service;
	}
}
