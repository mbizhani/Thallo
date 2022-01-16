package org.devocative.thallo.hlf.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devocative.thallo.hlf.HlfProperties;
import org.devocative.thallo.hlf.dto.HlfTransactionInfo;
import org.devocative.thallo.hlf.iservice.IHlfService;
import org.devocative.thallo.hlf.iservice.IHlfTransactionReader;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

@Slf4j
@RequiredArgsConstructor
@Service
public class HlfService implements IHlfService {
	private final HlfProperties properties;
	private final List<? extends IHlfTransactionReader> transactionReaders;

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
				final Enrollment enrollment = caClient.enroll(username, properties.getCaServer().getPassword(), enrollmentRequestTLS);
				final Identity user = Identities.newX509Identity(properties.getOrgMspId(), enrollment);
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

		try {
			processBlocks();
		} catch (Exception e) {
			log.error("HLF Service: Process Blocks", e);
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
	public byte[] submit(String method, String... args) throws Exception {
		return submit(properties.getChaincode(), method, args);
	}

	@Override
	public byte[] submit(String chaincode, String method, String... args) throws Exception {
		final Contract contract = network.getContract(getChaincode(chaincode));
		return contract.submitTransaction(method, args != null ? args : new String[0]);
	}

	@Override
	public byte[] evaluate(String method, String... args) throws Exception {
		return evaluate(properties.getChaincode(), method, args);
	}

	@Override
	public byte[] evaluate(String chaincode, String method, String... args) throws Exception {
		final Contract contract = network.getContract(getChaincode(chaincode));
		return contract.evaluateTransaction(method, args != null ? args : new String[0]);
	}

	// ------------------------------

	private String getChaincode(String chaincode) {
		return StringUtils.hasLength(chaincode) ? chaincode : properties.getChaincode();
	}

	private void processBlocks() throws Exception {
		if (!transactionReaders.isEmpty()) {
			log.info("HlfService: Transaction Reader(s) = {}", transactionReaders);

			final Channel channel = network.getChannel();
			final BlockchainInfo channelInfo = channel.queryBlockchainInfo();

			for (long current = 0; current < channelInfo.getHeight(); current++) {
				final BlockInfo blockInfo = channel.queryBlockByNumber(current);
				final Long blockNumber = blockInfo.getBlockNumber();

				for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
					processBlock(envelopeInfo, blockNumber);
				}
			}

			final BlockListener blockListener = (BlockEvent event) -> {
				log.info("HlfService: BlockListener Started");

				for (BlockInfo.EnvelopeInfo envelopeInfo : event.getEnvelopeInfos()) {
					final Long blockNumber = event.getBlockNumber();
					processBlock(envelopeInfo, blockNumber);
				}

				log.info("HlfService: BlockListener Stopped");
			};
			channel.registerBlockListener(blockListener);
		}
	}

	private void processBlock(BlockInfo.EnvelopeInfo envelopeInfo, Long blockNumber) {
		if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
			final String transactionId = envelopeInfo.getTransactionID();
			final Long timestamp = envelopeInfo.getTimestamp().getTime();

			final BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;

			for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
				final String methodName = new String(transactionActionInfo.getChaincodeInputArgs(0), UTF_8);
				final String[] args = new String[transactionActionInfo.getChaincodeInputArgsCount() - 1];
				for (int i = 1; i < transactionActionInfo.getChaincodeInputArgsCount(); i++) {
					args[i - 1] = new String(transactionActionInfo.getChaincodeInputArgs(i), UTF_8);
				}

				final HlfTransactionInfo hlfBlockDTO = new HlfTransactionInfo(
					blockNumber,
					transactionId,
					transactionActionInfo.getProposalResponseStatus(),
					transactionActionInfo.getChaincodeIDName(),
					methodName,
					args,
					new String(transactionActionInfo.getProposalResponsePayload(), UTF_8),
					timestamp
				);
				log.info("TransactionInfo: {}", hlfBlockDTO);

				transactionReaders.forEach(reader -> reader.handleTransaction(hlfBlockDTO));
			}
		}

	}
}
