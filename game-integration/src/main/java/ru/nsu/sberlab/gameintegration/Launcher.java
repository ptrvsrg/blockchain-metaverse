package ru.nsu.sberlab.gameintegration;

import io.neow3j.crypto.ECKeyPair;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
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
        System.loadLibrary("glew");
        System.loadLibrary("lodepng");
        System.loadLibrary("noise");
        System.loadLibrary("sqlite");
        System.loadLibrary("tinycthread");

        System.loadLibrary("craft");
        System.loadLibrary("jnative");
    }

    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     *
     * @param httpUrl   http адрес блокчейн ноды
     * @param keyPair   пара ключей для входа в аккаунт
     * @param mapHash   Hash контракта для карты
     * @param stateHash Hash контракта для состояния карты
     * @throws Throwable не удается установить связь с нодой или получить координаты игрока
     */
    public void launch(String httpUrl, ECKeyPair keyPair, Hash160 mapHash, Hash160 stateHash) throws Throwable {
        launch(new MapInteraction(httpUrl, keyPair, mapHash, stateHash));
    }

    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     *
     * @param httpUrl   http адрес блокчейн ноды
     * @param mapHash   Hash контракта для карты
     * @param stateHash Hash контракта для состояния карты
     * @throws Throwable не удается установить связь с нодой или получить координаты игрока
     */
    public void launch(String httpUrl, Hash160 mapHash, Hash160 stateHash) throws Throwable {
        launch(new MapInteraction(httpUrl, mapHash, stateHash));
    }

    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     *
     * @param httpUrl   http адрес блокчейн ноды
     * @param account   аккаунт
     * @param mapHash   Hash контракта для карты
     * @param stateHash Hash контракта для состояния карты
     * @throws Throwable не удается установить связь с нодой или получить координаты игрока
     */
    public void launch(String httpUrl, Account account, Hash160 mapHash, Hash160 stateHash) throws Throwable {
        launch(new MapInteraction(httpUrl, account, mapHash, stateHash));
    }


    /**
     * Запускает игру и выполняет задачи для запроса изменений данных из блокчейна.
     */
    public void launch(MapInteraction mapInBlockchain) throws Throwable {

        log.info("STARTING GAME...");
//        Thread startTask = new Thread(new StartTask(PlayerPositionHandler.getPlayerPosition(mapInBlockchain)));
        Thread startTask = new Thread(new StartTask(new PlayerPosition(0, 0, 0, 0, 0)));
        Thread blockchainDataRequestTask = new Thread(new BlockchainDataRequestTask(mapInBlockchain));
        Thread cDataRequestTask = new Thread(new CDataRequestTask(mapInBlockchain));

        startTask.start();
        blockchainDataRequestTask.start();
        cDataRequestTask.start();

        try {
            startTask.join();
            cDataRequestTask.join();
            log.info("ENDING GAME ...");


            PlayerPositionHandler.setPlayerPosition(mapInBlockchain, PlayerPositionHandler.getPlayerPositionC());
            blockchainDataRequestTask.interrupt();
        } catch (Throwable e) {
            log.catching(Level.ERROR, e);
        }

        log.info("END SESSION");

//        Thread startTask = new Thread(new StartTask(new PlayerPosition(
//                0, 0, 0, 0, 0)));
//        startTask.start();
//
//        Thread blockTask = new Thread(()->{
//            Block block = CDataRequestTask.getBlockChangeC();
//            while (block != null) {
//                System.out.println(block);
//                block = CDataRequestTask.getBlockChangeC();
//            }
//        });
//        blockTask.start();
//        blockTask.join();
//        startTask.join();
    }
}
