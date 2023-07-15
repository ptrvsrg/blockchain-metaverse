package ru.nsu.sberlab.gameintegration;

import lombok.extern.log4j.Log4j2;
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
     * @throws InterruptedException если поток прерван во время выполнения
     */
    public void launch() throws InterruptedException {

        log.info("STARTING GAME...");
        StartTask start = new StartTask(BlockchainDataRequestTask.getPlayerPosition());
        BlockchainDataRequestTask blockchainDataRequestTask = new BlockchainDataRequestTask();
        CDataRequestTask cDataRequestTask = new CDataRequestTask();

        start.start();
        blockchainDataRequestTask.start();
        cDataRequestTask.start();

        log.info("ENDING GAME ...");
        start.join();
        CDataRequestTask.getPlayerPositionChange();
        blockchainDataRequestTask.interrupt();
        cDataRequestTask.interrupt();

        log.info("END SESSION");
    }
}
