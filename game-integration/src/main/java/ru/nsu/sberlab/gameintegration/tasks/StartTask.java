package ru.nsu.sberlab.gameintegration.tasks;

import lombok.RequiredArgsConstructor;
import ru.nsu.sberlab.gameintegration.data.PlayerPosition;

/**
 * Класс Start представляет задачу для запуска стартовой операции. Реализует интерфейс Runnable.
 */
@RequiredArgsConstructor
public class StartTask
    implements Runnable {

    private final PlayerPosition playerPosition;

    public native void start(PlayerPosition playerPosition);

    /**
     * Выполняет стартовую операцию, вызывая нативный метод start(PlayerPosition playerPosition).
     */
    @Override
    public void run() {
        start(playerPosition);
    }
}
