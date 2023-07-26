package ru.nsu.sberlab.blockchainintegration.exception;

import io.neow3j.protocol.core.Response;

/**
 * Исключение, возникающее при ошибке получения журнала приложения (application log) транзакции с
 * блокчейна.
 */
public class ApplicationLogError
    extends BlockChainException {

    private final Response.Error e;

    /**
     * Конструктор класса ApplicationLogError.
     *
     * @param e Ошибка, полученная от блокчейн-провайдера при попытке получить журнал приложения
     *          транзакции.
     */
    public ApplicationLogError(final Response.Error e) {
        super("Error fetching transaction's app log: " + e.getMessage());
        this.e = e;
    }

    /**
     * Получить ошибку, полученную от блокчейн-провайдера при попытке получить журнал приложения
     * транзакции.
     *
     * @return Ошибка от блокчейн-провайдера.
     */
    public Response.Error getError() {
        return e;
    }
}
