package org.devocative.thallo.hlf.service;

import org.devocative.thallo.hlf.dto.HlfTransactionInfo;
import org.hyperledger.fabric.sdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

public class HlfTransactionReaderHandler {
	private static final Logger log = LoggerFactory.getLogger(HlfTransactionReaderHandler.class);

	private final Channel channel;
	private final ThreadPoolTaskExecutor taskExecutor;

	// ------------------------------

	public HlfTransactionReaderHandler(Channel channel, ThreadPoolTaskExecutor taskExecutor) {
		this.channel = channel;
		this.taskExecutor = taskExecutor;
	}

	// ------------------------------

	public void readBlockFrom(long startBlockNumber, Consumer<HlfTransactionInfo> consumer) {
		taskExecutor.execute(() -> {
			log.info("HlfTransactionReaderHandler.readBlockFrom: startingBlock={}", startBlockNumber);

			try {
				final BlockchainInfo channelInfo = channel.queryBlockchainInfo();

				for (long current = startBlockNumber; current < channelInfo.getHeight(); current++) {
					final BlockInfo blockInfo = channel.queryBlockByNumber(current);
					final Long blockNumber = blockInfo.getBlockNumber();

					for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
						processBlock(envelopeInfo, blockNumber, consumer);
					}
				}
			} catch (Exception e) {
				log.error("HlfTransactionReaderHandler.readBlockFrom", e);
			}
		});
	}

	public void registerBlockListener(Consumer<HlfTransactionInfo> consumer) {
		try {
			final BlockListener blockListener = (BlockEvent event) -> {
				log.info("HlfTransactionReaderHandler.registerBlockListener: event.blockNumber={}",
					event.getBlockNumber());

				for (BlockInfo.EnvelopeInfo envelopeInfo : event.getEnvelopeInfos()) {
					final Long blockNumber = event.getBlockNumber();
					processBlock(envelopeInfo, blockNumber, consumer);
				}
			};
			final String listener = channel.registerBlockListener(blockListener);
			log.info("Listener: {} - {}", listener, channel.getBlockListenerHandles());
		} catch (Exception e) {
			log.error("HlfTransactionReaderHandler.registerBlockListener", e);
		}
	}

	// ------------------------------

	private void processBlock(BlockInfo.EnvelopeInfo envelopeInfo, Long blockNumber, Consumer<HlfTransactionInfo> consumer) {
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
				consumer.accept(hlfBlockDTO);
			}
		}
	}
}
