package ru.nsu.sberlab.gameintegration.tasks;

import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.gameintegration.data.PlayerPosition;
import ru.nsu.sberlab.gameintegration.data.Block;

/**
 * Класс CDataRequestTask представляет поток для запроса изменений данных из C-кода.
 * Унаследован от класса Thread.
 */
@Log4j2
public class CDataRequestTask extends Thread{

    private static native Block getBlockChangeC();
    private static native PlayerPosition getPlayerPositionChangeC();

    /**
     * Время между запросами на изменение данных (в миллисекундах).
     */
    public static final int TIME_REQUEST = 1500;

    /**
     * Получает изменение блока и отправляет его на запись в блокчейн.
     */
    public static void getBlockChange(){
        Block block = getBlockChangeC();
        if (block == null) return;
        //TODO отправить на запись блок
    }

    /**
     * Получает изменение состояния и отправляет его на запись в блокчейн.
     */
    public static void getPlayerPositionChange(){
        PlayerPosition state = getPlayerPositionChangeC();
        if (state == null) return;
        //TODO отправить на запись положение
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
