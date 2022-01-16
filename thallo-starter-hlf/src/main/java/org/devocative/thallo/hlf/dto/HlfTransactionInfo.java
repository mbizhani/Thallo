package org.devocative.thallo.hlf.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString(exclude = {"methodArgs", "methodResult"})
public class HlfTransactionInfo {
	private final Long blockNumber;
	private final String transactionId;
	private final Integer responseStatus;
	private final String chainCode;
	private final String methodName;
	private final String[] methodArgs;
	private final String methodResult;
	private final Long timestamp;
}
