package ru.nsu.sberlab.gameintegration.exception;

public class QueueClosedException extends Exception {
    public QueueClosedException() {
    }

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
