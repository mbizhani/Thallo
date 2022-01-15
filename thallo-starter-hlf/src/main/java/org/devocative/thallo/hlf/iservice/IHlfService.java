package org.devocative.thallo.hlf.iservice;

public interface IHlfService {
	byte[] submit(String method, String... args) throws Exception;

	byte[] submit(String chaincode, String method, String... args) throws Exception;

	byte[] evaluate(String method, String... args) throws Exception;

	byte[] evaluate(String chaincode, String method, String... args) throws Exception;
}
