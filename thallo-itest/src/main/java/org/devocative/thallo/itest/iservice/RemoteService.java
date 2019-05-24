package org.devocative.thallo.itest.iservice;

import org.devocative.thallo.itest.IService;
import org.devocative.thallo.itest.ServiceInfo;
import org.devocative.thallo.itest.domain.service.Remote;

import java.util.Optional;

public class RemoteService implements IService<Remote> {
	private Remote remote;

	@Override
	public void init(Remote service) {
		remote = service;
	}

	@Override
	public Optional<ServiceInfo> start() {
		return Optional.of(new ServiceInfo(remote.getHost(), remote.getPort(), remote.getContext()));
	}

	@Override
	public void stop() {
	}
}
