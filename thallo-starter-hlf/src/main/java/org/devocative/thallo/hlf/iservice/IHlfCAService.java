package org.devocative.thallo.hlf.iservice;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;

import java.io.IOException;
import java.security.cert.CertificateException;

public interface IHlfCAService {
	Wallet enroll(String username, String password) throws InvalidArgumentException, EnrollmentException, CertificateException, IOException;

	boolean register(String username, String password, String type, String registerUsername) throws Exception;

	boolean isUserInWallet(String username);
}
