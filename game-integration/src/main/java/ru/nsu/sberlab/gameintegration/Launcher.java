package ru.nsu.sberlab.gameintegration;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.gameintegration.tasks.BlockchainDataRequestTask;
import ru.nsu.sberlab.gameintegration.tasks.CDataRequestTask;
import ru.nsu.sberlab.gameintegration.tasks.StartTask;

/**
 * Класс Launcher представляет запуск игры и выполнения задач для запроса изменений данных.
 */
@Log4j2
public class Launcher {

    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     *
     */
    public void launch() {

        log.info("STARTING GAME...");
        Thread startTask = new Thread(new StartTask(PlayerPositionHandler.getPlayerPosition()));
        Thread blockchainDataRequestTask = new Thread(new BlockchainDataRequestTask());
        Thread cDataRequestTask = new Thread(new CDataRequestTask());

        startTask.start();
        blockchainDataRequestTask.start();
        cDataRequestTask.start();

        try {
            log.info("ENDING GAME ...");
            startTask.join();
            cDataRequestTask.join();

            PlayerPositionHandler.setPlayerPosition(); //исправить
            blockchainDataRequestTask.interrupt();
        } catch (InterruptedException e){
            log.catching(Level.ERROR, e);
        }

        log.info("END SESSION");
    }
}
