package ru.nsu.sberlab.gameintegration.tasks;

import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.gameintegration.data.PlayerPosition;
import ru.nsu.sberlab.gameintegration.data.Block;

/**
 * Класс CDataRequestTask представляет поток для запроса изменений данных из C-кода.
 * Реализует интерфейс Runnable.
 */
@Log4j2
public class CDataRequestTask implements Runnable{
    /**
     * Время между запросами на изменение данных (в миллисекундах).
     */
    private static final int TIME_REQUEST = 1500;

    public static native Block getBlockChangeC();

    /**
     * Получает изменение блока и отправляет его на запись в блокчейн.
     */
    public static void getBlockChange(){
        Block block = getBlockChangeC();
        if (block == null) return;
        //TODO отправить на запись блок
    }

    /**
     * Запускает выполнение задачи.
     * Бесконечно запрашивает изменения данных с помощью нативных методов.
     * После каждого запроса ожидает заданное время перед следующим запросом.
     * В случае прерывания потока выбрасывает исключение InterruptedException.
     * @noinspection InfiniteLoopStatement
     */
    @Override
    public void run() {
        while (true){
            getBlockChange();
            try {
                //noinspection BusyWait
                Thread.sleep(TIME_REQUEST);
            }
            catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }
}
