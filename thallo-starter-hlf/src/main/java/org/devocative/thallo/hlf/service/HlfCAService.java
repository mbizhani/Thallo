package org.devocative.thallo.hlf.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devocative.thallo.hlf.HlfProperties;
import org.devocative.thallo.hlf.iservice.IHlfCAService;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class HlfCAService implements IHlfCAService {
	private final HlfProperties properties;

	private HFCAClient caClient;
	private Wallet wallet;

	// ------------------------------

	@PostConstruct
	public void init() {
		try {
			final Properties props = new Properties();
			props.put("pemFile", properties.getCaServer().getPemFile());
			props.put("allowAllHostNames", "true");
			caClient = HFCAClient.createNewInstance(properties.getCaServer().getUrl(), props);
			caClient.setCryptoSuite(CryptoSuiteFactory.getDefault().getCryptoSuite());

			wallet = Wallets.newFileSystemWallet(Paths.get(properties.getIdentityWalletDir()));
		} catch (Exception e) {
			throw new RuntimeException("HlfCAService.init", e);
		}
	}

	// ---------------

	@Override
	public Wallet enroll(String username, String password) throws InvalidArgumentException, EnrollmentException, CertificateException, IOException {
		if (isUserInWallet(username)) {
			log.info("An identity for the user \"{}\" already exists in the wallet.", username);
			return wallet;
		}

		final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
		enrollmentRequestTLS.addHost("localhost");
		enrollmentRequestTLS.setProfile("tls");
		final Enrollment enrollment = caClient.enroll(username, password, enrollmentRequestTLS);

		final Identity user = Identities.newX509Identity(properties.getOrgMspId(), enrollment);
		wallet.put(username, user);
		log.info("Successfully enrolled user \"{}\" and imported it into the wallet", username);

		return wallet;
	}

	@Override
	public boolean register(String username, String password, String type, String registerUsername) throws Exception {
		if (isUserInWallet(username)) {
			log.info("An identity for the user \"{}\" already exists in the wallet.", username);
			return false;
		}

		final X509Identity registerUserIdentity = (X509Identity) wallet.get(registerUsername);
		if (registerUserIdentity == null) {
			throw new RuntimeException(String.format(
				"\"%s\" needs to be enrolled first before registering new user", registerUsername));
		}
		final CAUser registerUser = new CAUser(registerUsername, registerUserIdentity, properties.getOrgMspId());

		final RegistrationRequest registrationRequest = new RegistrationRequest(username);
		registrationRequest.setEnrollmentID(username);
		registrationRequest.setType(type);
		registrationRequest.setSecret(password);

		try {
			caClient.register(registrationRequest, registerUser);
		} catch (RegistrationException e) {
			if (e.getMessage().contains("is already registered")) {
				log.info("User Already Registered: user={}, msg={}", username, e.getMessage());
				return false;
			} else {
				throw e;
			}
		}
		return true;
	}

	@Override
	public boolean isUserInWallet(String username) {
		try {
			return wallet.get(username) != null;
		} catch (IOException e) {
			throw new RuntimeException("HlfCAService.isUserInWallet", e);
		}
	}

	// ------------------------------

	@RequiredArgsConstructor
	static class CAUser implements User {
		private final String name;
		private final X509Identity identity;
		private final String mspId;

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Set<String> getRoles() {
			return null;
		}

		@Override
		public String getAccount() {
			return null;
		}

		@Override
		public String getAffiliation() {
			return null;
		}

		@Override
		public Enrollment getEnrollment() {
			return new Enrollment() {

				@Override
				public PrivateKey getKey() {
					return identity.getPrivateKey();
				}

				@Override
				public String getCert() {
					return Identities.toPemString(identity.getCertificate());
				}
			};
		}

		@Override
		public String getMspId() {
			return mspId;
		}

	}
}