package org.devocative.thallo.hlf.iservice;

public interface IHlfService {
	byte[] submit(String method, String... args);

	byte[] submit(String chaincode, String method, String... args);

	byte[] evaluate(String method, String... args);

	byte[] evaluate(String chaincode, String method, String... args);
}
