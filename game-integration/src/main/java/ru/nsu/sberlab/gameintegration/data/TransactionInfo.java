package ru.nsu.sberlab.gameintegration.data;

import io.neow3j.types.Hash256;
import java.util.Date;

/**
 * Класс TransactionInfo представляет информацию о транзакции и её связь с блоками.
 */
public class TransactionInfo {

    private final Hash256 txHash;
    private final Block[] block;
    private final long transactionTime;

    /**
     * Конструктор класса TransactionInfo.
     *
     * @param txHash Хэш транзакции.
     * @param block  Массив блоков, связанных с данной транзакцией.
     */
    public TransactionInfo(Hash256 txHash, Block... block) {
        this.txHash = txHash;
        this.block = block;
        transactionTime = new Date().getTime();

    }

    public Block[] getBlock() {
        return block;
    }

    public Hash256 getTxHash() {
        return txHash;
    }

    public long getPastTime() {
        return new Date().getTime() - transactionTime;
    }
}
