package ru.nsu.sberlab.gameintegration;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.gameintegration.data.Block;
import ru.nsu.sberlab.gameintegration.data.PlayerPosition;
import ru.nsu.sberlab.gameintegration.tasks.BlockchainDataRequestTask;
import ru.nsu.sberlab.gameintegration.tasks.CDataRequestTask;
import ru.nsu.sberlab.gameintegration.tasks.StartTask;

/**
 * Класс Launcher представляет запуск игры и выполнения задач для запроса изменений данных.
 */
@Log4j2
public class Launcher {

    static {
        System.loadLibrary("libglew");
        System.loadLibrary("liblodepng");
        System.loadLibrary("libnoise");
        System.loadLibrary("libsqlite");
        System.loadLibrary("libtinycthread");

        System.loadLibrary("libcraft");
        System.loadLibrary("libjnative");
    }

    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     */
    public void launch() throws InterruptedException {

        /*log.info("STARTING GAME...");
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

            PlayerPositionHandler.setPlayerPosition(PlayerPositionHandler.getPlayerPositionC());
            blockchainDataRequestTask.interrupt();
        } catch (InterruptedException e){
            log.catching(Level.ERROR, e);
        }

        log.info("END SESSION");*/

        Thread startTask = new Thread(new StartTask(new PlayerPosition(
                0, 0, 0, 0, 0)));
        startTask.start();

        Thread blockTask = new Thread(()->{
            Block block = CDataRequestTask.getBlockChangeC();
            while (block != null) {
                System.out.println(block);
                block = CDataRequestTask.getBlockChangeC();
            }
        });
        blockTask.start();
        blockTask.join();
        startTask.join();
    }
}
