package ru.nsu.sberlab.gameintegration.exception;

/**
 * Класс исключения, выбрасываемого при попытке доступа к закрытой очереди. Может быть использован
 * для обработки ситуации, когда очередь уже закрыта, но попытка добавить или извлечь элементы все
 * еще происходит.
 */
public class QueueClosedException
    extends Exception {

    public QueueClosedException() {}

    public QueueClosedException(String message) {
        super(message);
    }

    public QueueClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueueClosedException(Throwable cause) {
        super(cause);
    }
}
