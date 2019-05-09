package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Setter
@XStreamAlias("itest")
public class ITest {
	private List<BootApp> bootApps;

	private List<Service> services;

	private List<Param> params;

	private List<Rest> rests;

	// ------------------------------

	public List<BootApp> getBootApps() {
		return bootApps != null ? bootApps : Collections.emptyList();
	}

	public List<Service> getServices() {
		return services != null ? services : Collections.emptyList();
	}

	public List<Param> getParams() {
		return params != null ? params : Collections.emptyList();
	}

	public List<Rest> getRests() {
		return rests != null ? rests : Collections.emptyList();
	}
}
