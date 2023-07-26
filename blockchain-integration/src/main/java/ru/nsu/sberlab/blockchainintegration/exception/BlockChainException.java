package ru.nsu.sberlab.blockchainintegration.exception;

/**
 * Класс исключения, используемый для представления ошибок, связанных с взаимодействием с
 * блокчейном.
 */
public class BlockChainException
    extends Exception {

    public BlockChainException() {}

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message Сообщение, описывающее причину возникновения исключения.
     */
    public BlockChainException(String message) {
        super(message);
    }

    /**
     * Конструктор с сообщением об ошибке и причиной.
     *
     * @param message Сообщение, описывающее причину возникновения исключения.
     * @param cause   Объект Throwable, представляющий причину возникновения исключения.
     */
    public BlockChainException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Конструктор с причиной ошибки.
     *
     * @param cause Объект Throwable, представляющий причину возникновения исключения.
     */
    public BlockChainException(final Throwable cause) {
        super(cause);
    }
}
