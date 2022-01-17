package org.devocative.thallo.hlf.iservice;

import org.devocative.thallo.hlf.service.HlfTransactionReaderHandler;

public interface IHlfTransactionReader {
	void handleTransaction(HlfTransactionReaderHandler handler);
}
