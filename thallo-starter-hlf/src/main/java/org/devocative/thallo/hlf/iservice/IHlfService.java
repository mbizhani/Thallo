package org.devocative.thallo.hlf.iservice;

public interface IHlfService {
	String submit(String method, String... args);

	String submit(String chaincode, String method, String... args);

	String evaluate(String method, String... args);

	String evaluate(String chaincode, String method, String... args);
}
