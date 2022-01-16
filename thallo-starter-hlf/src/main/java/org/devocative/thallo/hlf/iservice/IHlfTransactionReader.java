package org.devocative.thallo.hlf.iservice;

import org.devocative.thallo.hlf.dto.HlfTransactionInfo;

public interface IHlfTransactionReader {
	void handleTransaction(HlfTransactionInfo info);
}
