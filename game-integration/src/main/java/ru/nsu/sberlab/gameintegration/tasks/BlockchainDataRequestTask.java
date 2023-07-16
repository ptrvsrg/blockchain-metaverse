package ru.nsu.sberlab.gameintegration.tasks;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.gameintegration.data.PlayerPosition;
import ru.nsu.sberlab.gameintegration.data.Block;

/**
 * Класс BlockchainDataRequestTask представляет поток для запроса данных из блокчейна и
 * взаимодействия с кодом на языке C/C++. Имплементирует класс Runnable.
 **/
@Log4j2
public class BlockchainDataRequestTask implements Runnable {

    /**
     * Время между запросами на изменение данных в блокчейне (в миллисекундах).
     */
    private static final int TIME_REQUEST = 1500;

    private static native void sendBlockChangeC(Block block);

    /**
     * Отправляет уведомление об изменении блока в блокчейне.
     */
    public void sendBlockChange(){
        //TODO функция чтобы взять из блокчейна блок
        Block block = new Block(0,0,0,0,0,0);
        sendBlockChangeC(block);
    }

    /**
     * Запускает выполнение задачи.
     * Бесконечно запрашивает изменения данных из блокчейна и передает полученные
     * данные в программу на си. После каждого запроса ожидает заданное время перед следующим запросом.
     * В случае прерывания потока выбрасывает исключение InterruptedException.
     * @noinspection InfiniteLoopStatement
     */
    @Override
    public void run() {
        while (true){
            sendBlockChange();
            try {
                Thread.sleep(TIME_REQUEST);
            }
            catch (InterruptedException e) {
                log.catching(Level.ERROR, e);
            }
        }
    }
}
