package ru.nsu.sberlab.blockchain.blockchain_interaction.exception;

public class BlockChainException extends Exception {
    public BlockChainException() {
    }

    public BlockChainException(String message) {
        super(message);
    }

    public BlockChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockChainException(Throwable cause) {
        super(cause);
    }
}
