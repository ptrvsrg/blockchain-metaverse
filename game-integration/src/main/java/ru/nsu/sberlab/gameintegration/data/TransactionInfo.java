package ru.nsu.sberlab.gameintegration.data;

import io.neow3j.types.Hash256;

import java.util.Date;

public class TransactionInfo {

    private final Hash256 txHash;
    private final long transactionTime;

    public TransactionInfo(Hash256 txHash) {
        this.txHash = txHash;
        transactionTime = new Date().getTime();

    }

    public Hash256 getTxHash() {
        return txHash;
    }

    public long getPastTime() {
        return new Date().getTime() - transactionTime;
    }
}
