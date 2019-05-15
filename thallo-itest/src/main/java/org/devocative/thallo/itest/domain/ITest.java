package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Setter
@XStreamAlias("itest")
public class ITest {
	private EmbeddedService kafka;

	private EmbeddedService redis;

	private List<AbstractService> services;

	private List<Param> params;

	private List<Rest> rests;

	// ------------------------------

	public EmbeddedService getKafka() {
		return kafka;
	}

	public EmbeddedService getRedis() {
		return redis;
	}

	public List<AbstractService> getServices() {
		return services != null ? services : Collections.emptyList();
	}

	public List<Param> getParams() {
		return params != null ? params : Collections.emptyList();
	}

	public List<Rest> getRests() {
		return rests != null ? rests : Collections.emptyList();
	}
}
