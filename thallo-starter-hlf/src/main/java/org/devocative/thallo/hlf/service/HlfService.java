package org.devocative.thallo.hlf.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devocative.thallo.hlf.HlfProperties;
import org.devocative.thallo.hlf.iservice.IHlfService;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Service
public class HlfService implements IHlfService {
	private final HlfProperties properties;

	private Gateway gateway;
	private Network network;

	// ------------------------------

	@PostConstruct
	public void init() {
		try {
			final Properties props = new Properties();
			props.put("pemFile", properties.getCaServer().getPemFile());
			props.put("allowAllHostNames", "true");

			final HFCAClient caClient = HFCAClient.createNewInstance(properties.getCaServer().getUrl(), props);
			caClient.setCryptoSuite(CryptoSuiteFactory.getDefault().getCryptoSuite());

			final Wallet wallet = Wallets.newFileSystemWallet(Paths.get(properties.getIdentityWalletDir()));

			final String username = properties.getCaServer().getUsername();
			if (wallet.get(username) == null) {
				final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
				enrollmentRequestTLS.addHost("localhost");
				enrollmentRequestTLS.setProfile("tls");
				Enrollment enrollment = caClient.enroll(username, properties.getCaServer().getPassword(), enrollmentRequestTLS);
				Identity user = Identities.newX509Identity(properties.getOrgMspId(), enrollment);
				wallet.put(username, user);
				log.info("Successfully enrolled user '{}' and imported it into the wallet", username);
			} else {
				log.info("An identity for the user '{}' already exists in the wallet", username);
			}

			final Path networkConfigPath = Paths.get(properties.getConnectionProfileFile());

			final Gateway.Builder builder = Gateway.createBuilder();
			gateway = builder
				.identity(wallet, username)
				.networkConfig(networkConfigPath)
				.connect();

			network = gateway.getNetwork(properties.getChannel());
		} catch (Exception e) {
			throw new RuntimeException("HLF Init Error", e);
		}
	}

	@PreDestroy
	public void shutdown() {
		if (gateway != null) {
			gateway.close();
		}
	}

	// ---------------

	@Override
	public String submit(String method, String... args) {
		return submit(properties.getChaincode(), method, args);
	}

	@Override
	public String submit(String chaincode, String method, String... args) {
		final Contract contract = network.getContract(chaincode);
		try {
			final byte[] bytes = contract.submitTransaction(method, args);
			return new String(bytes);
		} catch (Exception e) {
			throw new RuntimeException("Call Submit on Chaincode Error: ", e);
		}
	}

	@Override
	public String evaluate(String method, String... args) {
		return evaluate(properties.getChaincode(), method, args);
	}

	@Override
	public String evaluate(String chaincode, String method, String... args) {
		final Contract contract = network.getContract(chaincode);
		try {
			final byte[] bytes = contract.evaluateTransaction(method, args);
			return new String(bytes);
		} catch (Exception e) {
			throw new RuntimeException("Call Evaluate on Chaincode Error: ", e);
		}
	}
}