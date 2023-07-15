package ru.nsu.sberlab.gameintegration.tasks;

import lombok.extern.log4j.Log4j2;
import ru.nsu.sberlab.gameintegration.data.PlayerPosition;
import ru.nsu.sberlab.gameintegration.data.Block;

/**
 * Класс BlockchainDataRequestTask представляет поток для запроса данных из блокчейна и
 * взаимодействия с кодом на языке C/C++. Унаследован от класса Thread.
 */
@Log4j2
public class BlockchainDataRequestTask extends Thread{

    private static native void sendBlockChangeC(Block block);

    /**
     * Время между запросами на изменение данных (в миллисекундах).
     */
    public static final int TIME_REQUEST = 1500;

    /**
     * Отправляет уведомление об изменении блока в блокчейне.
     */
    public void sentBlockChange(){
        //TODO функция чтобы взять из блокчейна блок
        Block block = new Block(0,0,0,0,0,0);
        sendBlockChangeC(block);
    }

    /**
     * Отправляет уведомление об изменении состояния в блокчейне.
     *
     * @return объект PlayerPosition с изменением состояния
     */
    public static PlayerPosition getPlayerPosition(){
        //TODO функция чтобы взять из блокчейна местоположение
        return new PlayerPosition(0,0,0,0, 0);
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
            sentBlockChange();
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
