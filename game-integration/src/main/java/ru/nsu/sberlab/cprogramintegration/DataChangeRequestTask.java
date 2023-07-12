package ru.nsu.sberlab.cprogramintegration;

import lombok.extern.log4j.Log4j2;

/**
 * Класс DataChangeRequestTask представляет задачу, которая запрашивает
 * изменения данных с помощью нативных методов. Реализует интерфейс
 * Runnable.
 */
@Log4j2
public class DataChangeRequestTask implements Runnable{

    /**
     * Время между запросами на изменение данных (в миллисекундах).
     */
    public static final int TIME_REQUEST = 1500;

    /**
     * Конструктор класса DataChangeRequestTask.
     * Создает и запускает новый поток для выполнения задачи.
     */
    public DataChangeRequestTask(){
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Запускает выполнение задачи.
     * Бесконечно запрашивает изменения данных с помощью нативных методов.
     * После каждого запроса ожидает заданное время перед следующим запросом.
     * В случае прерывания потока выбрасывает исключение InterruptedException.
     */
    @Override
    public void run() {
        while (true){
            CDataExtractor.getBlockChange();
            CDataExtractor.getStateChange();
            try {
                Thread.sleep(TIME_REQUEST);
            }
            catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }
}
