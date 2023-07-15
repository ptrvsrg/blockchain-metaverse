package ru.nsu.sberlab.gameintegration.tasks;

import ru.nsu.sberlab.gameintegration.data.PlayerPosition;

/**
 * Класс Start представляет поток для запуска стартовой операции.
 * Унаследован от класса Thread.
 */
public class StartTask extends Thread{

    public native void start(PlayerPosition playerPosition);
    private final PlayerPosition playerPosition;

    /**
     * Создает новый экземпляр StartTask с указанным объектом PlayerPosition.
     *
     * @param playerPosition объект PlayerPosition для использования в стартовой операции
     */
    public StartTask(PlayerPosition playerPosition){
        this.playerPosition = playerPosition;
    }

    /**
     * Выполняет стартовую операцию, вызывая нативный метод start(PlayerPosition playerPosition).
     */
    @Override
    public void run() {
        start(playerPosition);
    }
}
