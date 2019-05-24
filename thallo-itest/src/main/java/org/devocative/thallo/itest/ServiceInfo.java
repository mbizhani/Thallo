package org.devocative.thallo.itest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServiceInfo {
	private final String host;
	private final int port;
	private final String context;

	// ------------------------------

	public ServiceInfo(String host, int port) {
		this(host, port, null);
	}
}
